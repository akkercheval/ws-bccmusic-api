package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.AccountUpgradeRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountUpgradeRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUpgradeRequestService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_DENIED = "DENIED";

    private final AccountUpgradeRequestRepository upgradeRequestRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountUpgradeRequest submitRequest(Account requester, String reason) {
        // Check if user already has a pending request
        upgradeRequestRepository.findByAccountAccountIdAndStatus(requester.getAccountId(), STATUS_PENDING)
                .ifPresent(existing -> {
                    throw new IllegalStateException("You already have a pending upgrade request.");
                });

        // Don't allow if already an owner or admin
        if (requester.getAccountType() == Role.OWNER || requester.getAccountType() == Role.ADMINISTRATOR) {
            throw new IllegalStateException("Your account is already at Owner level or above.");
        }

        AccountUpgradeRequest request = new AccountUpgradeRequest();
        request.setAccount(requester);
        request.setReason(reason);
        request.setStatus(STATUS_PENDING);
        request.setRequestedAt(LocalDateTime.now());

        AccountUpgradeRequest saved = upgradeRequestRepository.save(request);
        log.info("Upgrade request submitted by account {} ({})", requester.getAccountId(), requester.getUsername());

        return saved;
    }

    public List<AccountUpgradeRequest> getPendingRequests() {
        return upgradeRequestRepository.findByStatus(STATUS_PENDING);
    }

    public List<AccountUpgradeRequest> getRequestsForAccount(Long accountId) {
        return upgradeRequestRepository.findByAccountAccountIdOrderByRequestedAtDesc(accountId);
    }

    @Transactional
    public AccountUpgradeRequest resolveRequest(Long requestId, String status, String adminNotes, Account admin) {
        if (!STATUS_APPROVED.equals(status) && !STATUS_DENIED.equals(status)) {
            throw new IllegalArgumentException("Status must be APPROVED or DENIED.");
        }

        AccountUpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Upgrade request not found with id: " + requestId));

        if (!STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("This request has already been resolved.");
        }

        request.setStatus(status);
        request.setAdminNotes(adminNotes);
        request.setResolvedAt(LocalDateTime.now());
        request.setResolvedBy(admin);

        if (STATUS_APPROVED.equals(status)) {
            Account accountToUpgrade = request.getAccount();
            accountToUpgrade.setAccountType(Role.OWNER.name());
            accountRepository.save(accountToUpgrade);
            log.info("Account {} upgraded to OWNER by admin {}", accountToUpgrade.getAccountId(), admin.getUsername());
        } else {
            log.info("Upgrade request {} denied by admin {}", requestId, admin.getUsername());
        }

        return upgradeRequestRepository.save(request);
    }
}

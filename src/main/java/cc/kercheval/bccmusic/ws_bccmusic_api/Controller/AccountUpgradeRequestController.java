package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Mapper.AccountUpgradeRequestMapper;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountUpgradeResolveRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountUpgradeRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountUpgradeSubmitRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountUpgradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/account-upgrade-requests")
@RequiredArgsConstructor
public class AccountUpgradeRequestController {

    private final AccountUpgradeRequestService upgradeRequestService;
    private final AccountService accountService;
    private final AccountUpgradeRequestMapper upgradeRequestMapper;

    /**
     * User submits a request to upgrade their account to Owner.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountUpgradeRequest submitRequest(
            @RequestBody(required = false) AccountUpgradeSubmitRequest request,
            Principal principal) {

        Account requester = accountService.findByUsername(principal.getName());
        String reason = (request != null) ? request.getReason() : null;

        return upgradeRequestMapper.toDto(
                upgradeRequestService.submitRequest(requester, reason)
        );
    }

    /**
     * Admin views all pending upgrade requests.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public List<AccountUpgradeRequest> getPendingRequests() {
        return upgradeRequestService.getPendingRequests().stream()
                .map(upgradeRequestMapper::toDto)
                .toList();
    }

    /**
     * User checks the status of their own upgrade request(s).
     */
    @GetMapping("/my-status")
    public List<AccountUpgradeRequest> getMyRequestStatus(Principal principal) {
        Account account = accountService.findByUsername(principal.getName());
        return upgradeRequestService.getRequestsForAccount(account.getAccountId()).stream()
                .map(upgradeRequestMapper::toDto)
                .toList();
    }

    /**
     * Admin approves or denies an upgrade request.
     * On approval, the user's account type is upgraded to OWNER.
     */
    @PutMapping("/{requestId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public AccountUpgradeRequest resolveRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody AccountUpgradeResolveRequest resolveRequest,
            Principal principal) {

        Account admin = accountService.findByUsername(principal.getName());

        return upgradeRequestMapper.toDto(
                upgradeRequestService.resolveRequest(
                        requestId,
                        resolveRequest.getStatus(),
                        resolveRequest.getAdminNotes(),
                        admin
                )
        );
    }
}

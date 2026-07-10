package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.AccountUpgradeRequest;

public interface AccountUpgradeRequestRepository extends JpaRepository<AccountUpgradeRequest, Long> {

    List<AccountUpgradeRequest> findByStatus(String status);

    Optional<AccountUpgradeRequest> findByAccountAccountIdAndStatus(Long accountId, String status);

    List<AccountUpgradeRequest> findByAccountAccountIdOrderByRequestedAtDesc(Long accountId);
}

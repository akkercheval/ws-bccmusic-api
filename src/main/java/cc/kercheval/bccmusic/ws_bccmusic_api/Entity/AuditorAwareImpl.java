package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import lombok.RequiredArgsConstructor;

@Component("auditorAware")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Account> {
	
	private final AccountService accountService;

    @Override
    public Optional<Account> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();  // system-created or anonymous
        }

        String username = auth.getName();
        Account currentUser = accountService.findByUsername(username);  

        return Optional.ofNullable(currentUser);
    }
}

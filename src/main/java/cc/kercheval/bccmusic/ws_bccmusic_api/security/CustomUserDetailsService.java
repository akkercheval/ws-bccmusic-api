package cc.kercheval.bccmusic.ws_bccmusic_api.security;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        List<? extends GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + account.getAccountType().name())
        );

        return new CustomUserDetails(
            account.getAccountId(),
            account.getUsername(),
            account.getHashedPassword(),
            authorities
        );
    }
}

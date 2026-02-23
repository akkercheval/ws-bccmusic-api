package cc.kercheval.bccmusic.ws_bccmusic_api.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomUserDetails extends User {

	private static final long serialVersionUID = -5296209265554307672L;
	
	private final Long accountId;

	public CustomUserDetails(
            Long accountId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);
        this.accountId = accountId;
    }
}

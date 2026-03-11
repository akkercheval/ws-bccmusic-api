package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
	
	private final AccountService accountService;
	private final ModelMapper modelMapper;

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
    	Map<String, Object> response = new LinkedHashMap<>();
    	
        if (authentication == null || !authentication.isAuthenticated()) {
        	response.put("authenticated", false);
            return response;
        }

        String username = authentication.getName();
        Account account = modelMapper.map(accountService.findByUsername(username), Account.class);

        if (account == null) {
        	response.put("authenticated", false);
            return response;
        }

        response = Map.of(
            "authenticated", true,
            "accountId", account.getAccountId(),
            "username", account.getUsername(),
            "accountName", account.getAccountName(),
            "accountType", account.getAccountType(),
            "roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        return response;
    }

    @GetMapping("/auth/status")
    public Map<String, Object>  authStatus(Authentication authentication) {
    	Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {           
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            return response;
        }
        
    	response.put("authenticated", false);
        return response;
    }
}
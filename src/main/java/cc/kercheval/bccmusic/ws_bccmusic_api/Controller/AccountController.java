package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/accounts")
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService accountService;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	@GetMapping(value = "/{accountId}")
	@PreAuthorize("hasRole('ADMINISTRATOR') or #accountId == authentication.principal.accountId")
	public Account getAccount(@PathVariable Long accountId) {
		
		Account account = modelMapper.map(accountService.getAccountById(accountId), Account.class);
		return account;		
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public List<Account> getAllAccounts() {
		return accountService.getAllAccounts().stream()
				.map(a -> 
				modelMapper.map(a, Account.class))
				.collect(Collectors.toList());
	}
	
	@PostMapping
	public Account createNewAccount(@Valid @RequestBody Account newAccount) throws AccountValidationException, AccountNotFoundException {
		log.info("Creating New account: {}", newAccount.getUsername());
		
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account newAccountEntity = modelMapper.map(newAccount, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account.class);
		if (StringUtils.hasText(newAccount.getPassword())) {
			newAccountEntity.setHashedPassword(passwordEncoder.encode(newAccount.getPassword()));
	    } else {
	        throw new IllegalArgumentException("Password is required");
	    }
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account createdAccount =  accountService.createAccount(newAccountEntity);
		log.info("Successfully created AccountId: {} for username: {}", createdAccount.getAccountId(), createdAccount.getUsername());
		
		return modelMapper.map(createdAccount, Account.class);
	}
	
	@PutMapping(value="/{accountId}")
	@PreAuthorize("hasRole('ADMINISTRATOR') or #accountId == authentication.principal.accountId")
	public Account updateAccount(@Valid @RequestBody Account updatedAccount) throws AccountValidationException, AccountNotFoundException {
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account updatedEntity = accountService.updateAccount(modelMapper.map(updatedAccount, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account.class));
		return modelMapper.map(updatedEntity, Account.class);
	}
	
	@PatchMapping(value = "/{accountId}/password")
	@PreAuthorize("hasRole('ADMINISTRATOR') or #accountId == authentication.principal.accountId")
	public String updatePassword(@PathVariable Long accountId, @RequestParam(required = true) String updatedPassword, Principal principal) throws AccountValidationException {
		Long accountToUpdate = getAccountFromPrincipal(principal);
		
		if(accountId != accountToUpdate) {
			throw new IllegalArgumentException("Cannot update password for another account.");
		}
		
		return accountService.updatePassword(accountToUpdate, updatedPassword);		
	}
	
	private Long getAccountFromPrincipal(Principal principal) {
		return accountService.findByUsername(principal.getName()).getAccountId();
	}

}

package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountAlreadyExistsException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.util.ValidationConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	
	public Account getAccountById(Long accountId) {
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + accountId));
	}
	
	public Account createAccount(Account account) throws AccountValidationException {
		
		validateCreateAccount(account);
				
		List<Role> validRoles = Arrays.asList(Role.values());
		
		if (account.getAccountType() == null || !validRoles.contains(account.getAccountType())) {
	        account.setAccountType(Role.VIEWER.name());
	    }
		
		Account createdAccount = accountRepository.save(account);
		
		return createdAccount;
	}

	public Account updateAccount(Account account) throws AccountValidationException, AccountNotFoundException {
		
		List<String> validationErrors = validateAccount(account);
				
		Account currentAccount = accountRepository.getAccountByAccountId(account.getAccountId());
		if(currentAccount == null) {
			throw new AccountNotFoundException ("Cannot update account: " + account.getAccountId() + ".  Account does not exist.");
		}

		if(!account.getUsername().equals(currentAccount.getUsername()) && accountRepository.countUsername(account.getUsername()) > 0 ) {
				throw new AccountAlreadyExistsException("Cannot update username.  Username already exists.");
		}
		
		if(!validationErrors.isEmpty()) {
			throw new AccountValidationException(validationErrors);
		}
		
		currentAccount.setUsername(account.getUsername());
		currentAccount.setAccountName(account.getAccountName());
		currentAccount.setAccountType(account.getAccountType().name());
		currentAccount.setStreetAddress(account.getStreetAddress());
		currentAccount.setCity(account.getCity());
		currentAccount.setStateAbbr(account.getStateAbbr());
		currentAccount.setZipCode(account.getZipCode());
		currentAccount.setPhoneNumber(account.getPhoneNumber());
		currentAccount.setPhoneType(account.getPhoneType());
		currentAccount.setWebsite(account.getWebsite());
		currentAccount.setEmail(account.getEmail());
		
		return accountRepository.save(currentAccount);
	}

	public Account findByUsername(String username) {
		
		return accountRepository.findByUsername(username);
	}
	
	public String updatePassword(Long accountId, String updatedPassword) throws AccountValidationException {
		Account currentAccount = accountRepository.getAccountByAccountId(accountId);
		if(currentAccount == null) {
			ResponseEntity.notFound().build();
		}
		String newHashedPassword = passwordEncoder.encode(updatedPassword);
		if(newHashedPassword.equals(currentAccount.getHashedPassword())) {
			throw new AccountValidationException(List.of("Cannot update password.  New password matches previous password."));
		}
		
		currentAccount.setHashedPassword(newHashedPassword);
		accountRepository.save(currentAccount);
		
		return "Password successfully updated for username: " + currentAccount.getUsername();
	}
	
	private void validateCreateAccount(Account account) throws AccountValidationException {
		List<String> validationErrors = new ArrayList<>();
		if (account.getHashedPassword() == null || account.getHashedPassword().isEmpty()) {
	        validationErrors.add("Password is required");
	    }
		
		int existingAccounts = accountRepository.countUsername(account.getUsername());
		if(existingAccounts > 0) {
			validationErrors.add("Cannot create account.  Username already exists.");
		}
		
		validationErrors.addAll(validateAccount(account));
		
		if(validationErrors.size() > 0) {
			throw new AccountValidationException(validationErrors);
		}
	}
	
	private List<String> validateAccount(Account account) {
		List<String> validationErrors = new ArrayList<>();
		
		if (account.getUsername() == null || account.getUsername().isEmpty()) {
			validationErrors.add("Username is required.");
	    }
		
		if(account.getZipCode() != null && !account.getZipCode().isBlank() && !ValidationConstants.zipCodePattern.matcher(account.getZipCode()).matches()) {
			validationErrors.add("Zip Code does not match valid zip code pattern.");
		}
		
		if(account.getStateAbbr() != null && !account.getStateAbbr().isBlank() && !ValidationConstants.usStatePattern.matcher(account.getStateAbbr()).matches()) {
			validationErrors.add("Provided State Abbreviation is not a valid US State Abbreviation.");
		}
		
		try {
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			boolean isValid = account.getPhoneNumber() == null || account.getPhoneNumber().isBlank() || phoneUtil.isValidNumber(phoneUtil.parse(account.getPhoneNumber(), "US"));
		
			if(!isValid) {
				validationErrors.add("Provided Phone Number does not match valid phone number pattern");
			}
		} catch(NumberParseException ex) {
			String errorDetail = switch (ex.getErrorType()) {
	        case INVALID_COUNTRY_CODE   -> "Phone number contains Invalid or missing country code.  ";
	        case NOT_A_NUMBER           -> "Phone number contains invalid characters.  ";
	        case TOO_SHORT_NSN          -> "Phone number is too short.  ";
	        case TOO_LONG               -> "Phone number is too long.  ";
	        default                     -> "Invalid phone number format.  ";
	    };
			validationErrors.add(errorDetail);
		}
		
		return validationErrors;
	}

	public List<Account> findAllOwners() {
		return accountRepository.findByAccountType(Role.OWNER.name());
	}
}

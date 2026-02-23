package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;

public interface AccountRepository extends CrudRepository<Account, Long>{
	public Account getAccountByAccountId(Long accountId);
	
	@Query("SELECT count(a) FROM Account a WHERE a.username = :username")
	public int countUsername(String username);
	
	public Account findByUsername(String username);
	
	public List<Account> findByAccountType(String accountType);
}

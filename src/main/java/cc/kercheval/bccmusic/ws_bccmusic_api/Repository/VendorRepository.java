package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor;

public interface VendorRepository extends CrudRepository<Vendor, Long> {
	public Vendor findByVendorId(Long vendorId);
	
	@Query("SELECT v FROM Vendor v WHERE v.vendorName LIKE :vendorSearch")
	public List<Vendor> searchVendors(String vendorSearch);
	
	public Vendor findByVendorName(String vendorName);
}

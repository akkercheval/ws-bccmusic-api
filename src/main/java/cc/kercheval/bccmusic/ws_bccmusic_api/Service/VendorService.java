package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.VendorRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.util.ValidationConstants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorService {
	
	private final VendorRepository vendorRepository;
	
	public Vendor getVendorById(Long vendorId) throws VendorNotFoundException {
		Optional<cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor> vendorEntity = vendorRepository.findById(vendorId);
		if(vendorEntity.isEmpty()) {
			throw new VendorNotFoundException("No vendor found with vendorId: " + vendorId);
		}
		
		return vendorEntity.get();
	}

	public List<Vendor> searchVendor(String vendorSearch) {

		List<Vendor> vendorEntityList = vendorRepository.searchVendors(vendorSearch.trim());
		if(vendorEntityList.size() == 0) {
			return new ArrayList<>();
		}

		
		return vendorEntityList;		
	}

	public Vendor createVendor(Vendor newVendor) throws VendorValidationException {
		List<String> validationErrors = validateVendor(newVendor);
		if(!validationErrors.isEmpty()) {
			throw new VendorValidationException(validationErrors.toString());
		}
				
		return vendorRepository.save(newVendor);
	}

	public Vendor updateVendor(Vendor updatedVendor) throws VendorValidationException, VendorNotFoundException {
		List<String> validationErrors = validateVendor(updatedVendor);
		if(!validationErrors.isEmpty()) {
			throw new VendorValidationException(validationErrors.toString());
		}
		
		Optional<cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor> existingVendor = vendorRepository.findById(updatedVendor.getVendorId());
		if(existingVendor.isEmpty()) {
			throw new VendorNotFoundException("Vendor " +  updatedVendor.getVendorName() + " cound not be updated because the vendor could not be found.");
		}
		
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor vendorToUpdate = existingVendor.get();
		if(!vendorToUpdate.getVendorName().equals(updatedVendor.getVendorName())) {
			if(vendorRepository.findByVendorName(updatedVendor.getVendorName()) != null) {
				throw new VendorValidationException("Cannot update vendor name.  Another vendor by that name already exists.");
			}
			vendorToUpdate.setVendorName(updatedVendor.getVendorName());
		}
		vendorToUpdate.setStreetAddress(updatedVendor.getStreetAddress());
		vendorToUpdate.setCity(updatedVendor.getCity());
		vendorToUpdate.setStateAbbr(updatedVendor.getStateAbbr());
		vendorToUpdate.setZipCode(updatedVendor.getZipCode());
		vendorToUpdate.setPhoneNumber(vendorToUpdate.getPhoneNumber());
		vendorToUpdate.setPhoneType(updatedVendor.getPhoneType());
		vendorToUpdate.setEmail(updatedVendor.getEmail());
		vendorToUpdate.setWebsite(updatedVendor.getWebsite());
		
		return vendorRepository.save(vendorToUpdate);
	}
	
	private List<String> validateVendor(Vendor vendor) {
		List<String> validationErrors = new ArrayList<>();
		if(vendorRepository.findByVendorName(vendor.getVendorName()) != null) {
			validationErrors.add("A vendor by that name already exists.  Cannot create a new vendor.");
		}
		if(vendor.getZipCode()!= null && !ValidationConstants.zipCodePattern.matcher(vendor.getZipCode()).matches()) {
			validationErrors.add("Zip Code does not match valid zip code pattern.");
		}
		if(vendor.getStateAbbr() != null && !ValidationConstants.usStatePattern.matcher(vendor.getStateAbbr()).matches()) {
			validationErrors.add("Provided State Abbreviation is not a valid US State Abbreviation.");
		}
		
		try {
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			boolean isValid = vendor.getPhoneNumber() == null || phoneUtil.isValidNumber(phoneUtil.parse(vendor.getPhoneNumber(), "US"));
			
			if(!isValid) {
				validationErrors.add("Provided Phone Number does not match valid phone number pattern. ");
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
}

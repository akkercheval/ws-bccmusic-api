package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Mapper.VendorMapper;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Vendor;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.VendorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/vendors")
@RequiredArgsConstructor
public class VendorController {
	
	private final VendorService vendorService;
	private final VendorMapper vendorMapper;
	
	@GetMapping
	public List<Vendor> getAllVendors() {
		return vendorService.getAllVendors()
				.stream()
				.map(vendorMapper::toDto)
				.toList();
	}
	
	@GetMapping(value = "/{vendorId}")
	public Vendor getVendor(@PathVariable Long vendorId) throws VendorNotFoundException {
		return vendorMapper.toDto(vendorService.getVendorById(vendorId));
	}
	
	@GetMapping(value = "/search")
	public List<Vendor> searchVendor(@RequestBody @NotBlank String vendorSearch) {
		return vendorService.searchVendor(vendorSearch)
				.stream()
				.map(vendorMapper::toDto)
				.toList();
	}

	@PostMapping
	public Vendor createNewVendor(@Valid @RequestBody Vendor newVendor) throws VendorValidationException {
		log.info("Creating New Vendor: {}", newVendor.getVendorName());
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor newVendorEntity = vendorMapper.toEntity(newVendor);
		return vendorMapper.toDto(vendorService.createVendor(newVendorEntity));
	}
	
	@PutMapping
	public Vendor updateVendor(@Valid @RequestBody Vendor updatedVendor) throws VendorValidationException, VendorNotFoundException {
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor updatedVendorEntity = vendorMapper.toEntity(updatedVendor);
		return vendorMapper.toDto(vendorService.updateVendor(updatedVendorEntity));
	}
}

package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.VendorValidationException;
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
	
	VendorService vendorService;
	ModelMapper modelMapper;
	
	@GetMapping
	public List<Vendor> getAllVendors() {
		
		List<Vendor> vendors = vendorService.getAllVendors()
				.stream()
				.map(e ->
					modelMapper.map(e, Vendor.class))
				.collect(Collectors.toList());
		
		return vendors;
	}
	
	@GetMapping(value = "/{vendorId}")
	public Vendor getVendor(@PathVariable Long vendorId) throws VendorNotFoundException {
		return modelMapper.map(vendorService.getVendorById(vendorId), Vendor.class);		
	}
	
	@GetMapping(value = "/search")
	public List<Vendor> searchVendor(@RequestBody @NotBlank String vendorSearch) {
		
		List<Vendor> vendors = vendorService.searchVendor(vendorSearch)
				.stream()
				.map(e ->
					modelMapper.map(e, Vendor.class))
				.collect(Collectors.toList());
		
		return vendors;
	}

	@PostMapping
	public Vendor createNewVendor(@Valid @RequestBody Vendor newVendor) throws VendorValidationException {
		log.info("Creating New Vendor: {}", newVendor.getVendorName());
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor newVendorEntity = modelMapper.map(newVendor, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor.class);
		return modelMapper.map(vendorService.createVendor(newVendorEntity), Vendor.class);
	}
	
	@PutMapping
	public Vendor updateVendor(@Valid @RequestBody Vendor updatedVendor) throws VendorValidationException, VendorNotFoundException {
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor updatedVendorEntity = modelMapper.map(updatedVendor, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor.class);
		return modelMapper.map(vendorService.updateVendor(updatedVendorEntity), Vendor.class);
	}

}

package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Composer;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.ComposerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/composers")
@RequiredArgsConstructor
public class ComposerController {
	
	private final ComposerService composerService;
	private final ModelMapper modelMapper;
	
	@GetMapping(value = "/{composerId}")
	public Composer getComposer(@PathVariable Long composerId) {
		return modelMapper.map(composerService.getComposerById(composerId), Composer.class);		
	}
	
	@GetMapping
	public List<Composer> getAllComposers() {
		List<Composer> allComposers = composerService.getAllComposers()
				.stream()
				.map(c -> 
				modelMapper.map(c, Composer.class))
				.toList();
				
		return allComposers;
	}

	@PostMapping
	public Composer createNewComposer(@Valid @RequestBody Composer newComposer) {
	    log.info("New Composer: firstName={}, middleName={}, lastName={}", 
	             newComposer.getFirstName(), 
	             newComposer.getMiddleName(), 
	             newComposer.getLastName());
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer newComposerEntity = modelMapper.map(newComposer, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer.class);
		return modelMapper.map(composerService.createComposer(newComposerEntity), Composer.class);
	}
	
	@PutMapping
	public Composer updateComposer(@Valid @RequestBody Composer updatedComposer) {
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer updatedComposerEntity = modelMapper.map(updatedComposer, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer.class);
		return modelMapper.map(composerService.updateComposer(updatedComposerEntity), Composer.class);
	}

}

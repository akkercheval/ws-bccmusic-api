package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Mapper.ComposerMapper;
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
	private final ComposerMapper composerMapper;
	
	@GetMapping(value = "/{composerId}")
	public Composer getComposer(@PathVariable Long composerId) {
		return composerMapper.toDto(composerService.getComposerById(composerId));
	}
	
	@GetMapping
	public List<Composer> getAllComposers() {
		return composerService.getAllComposers()
				.stream()
				.map(composerMapper::toDto)
				.toList();
	}

	@PostMapping
	public Composer createNewComposer(@Valid @RequestBody Composer newComposer) {
	    log.info("New Composer: firstName={}, middleName={}, lastName={}", 
	             newComposer.getFirstName(), 
	             newComposer.getMiddleName(), 
	             newComposer.getLastName());
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer newComposerEntity = composerMapper.toEntity(newComposer);
		return composerMapper.toDto(composerService.createComposer(newComposerEntity));
	}
	
	@PutMapping
	public Composer updateComposer(@Valid @RequestBody Composer updatedComposer) {
		cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer updatedComposerEntity = composerMapper.toEntity(updatedComposer);
		return composerMapper.toDto(composerService.updateComposer(updatedComposerEntity));
	}
}

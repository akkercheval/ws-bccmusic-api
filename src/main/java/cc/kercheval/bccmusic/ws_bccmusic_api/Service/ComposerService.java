package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.ComposerValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ComposerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComposerService {
	
	private final ComposerRepository composerRepository;

	public Composer getComposerById(Long composerId) {
		return composerRepository.findById(composerId).orElseThrow();
	}

	public List<Composer> getAllComposers() {
		return StreamSupport.stream(composerRepository.findAll().spliterator(), false).toList();
	}

	public Composer createComposer(Composer newComposerEntity) {
		validateComposer(newComposerEntity);
		
		return composerRepository.save(newComposerEntity);
	}

	public Composer updateComposer(Composer updatedComposerEntity) {
		validateComposer(updatedComposerEntity);
		
		return composerRepository.save(updatedComposerEntity);
	}

	protected void validateComposer(Composer composer) {
		if(composer.getLastName() == null || composer.getLastName().isBlank()) {
			throw new ComposerValidationException(List.of("Composer save failed.  Composer's last name cannot be blank."));
		}
	}
}

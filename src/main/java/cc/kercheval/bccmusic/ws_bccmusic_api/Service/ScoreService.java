package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Medley;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Part;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreComposer;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreSpecification;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreTag;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.ScoreValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ScoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreService {
	
	private final ScoreRepository scoreRepository;
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;

	public Score getScoreById(Long scoreId) {
		return scoreRepository.findById(scoreId)
                .orElseThrow(() -> new EntityNotFoundException("Score not found with id: " + scoreId));
	}

	public List<Score> getAllScores() {
		return scoreRepository.findAll();
	}

	public List<Score> getScoresByAccountId(Long accountId) {
		return scoreRepository.findScoresByOwner(accountId);
	}

	@Transactional
	public Score createScore(Score score, Account editorAccount) {
		LocalDateTime currentTime = LocalDateTime.now();
		score.setCreatedAt(currentTime);
		score.setCreatedBy(editorAccount);
		score.setUpdatedAt(currentTime);
		score.setUpdatedBy(editorAccount);
		
		if (score.getMedleys() != null) {
		    for (Medley medley : score.getMedleys()) {
		      medley.setScore(score);
		    }
		  }

		  if (score.getParts() != null) {
		    for (Part part : score.getParts()) {
		      part.setScore(score);
		    }
		  }
		  
		  if (score.getScoreComposers() != null) {
		    for (ScoreComposer sc : score.getScoreComposers()) {
		      sc.setScore(score);
		    }
		  }
		  
		  if (score.getScoreTags() != null) {
		    for (ScoreTag tag : score.getScoreTags()) {
		      tag.setScore(score);
		    }
		  }		
		
		return scoreRepository.save(score);
	}

	@Transactional
	public Score updateScore(Score score, Account editorAccount) {
		Score entity = scoreRepository.findById(score.getScoreId())
		        .orElseThrow(() -> new EntityNotFoundException("Score not found"));
		LocalDateTime currentTime = LocalDateTime.now();
		entity.setUpdatedAt(currentTime);
		entity.setUpdatedBy(editorAccount);
		entity.setArrangementType(score.getArrangementType());
		entity.setGrade(score.getGrade());
		
		if (score.getMedleys() != null) {
		    for (Medley medley : score.getMedleys()) {
		      medley.setScore(entity);
		    }
		  }
		replaceChildCollection(
		        score.getMedleys(),
		        entity.getMedleys(),
		        entity::setMedleys,
		        modelMapper,
		        Medley::getMedleyId
		    );
		
		  if (score.getParts() != null) {
		    for (Part part : score.getParts()) {
		      part.setScore(entity);
		    }
		  }
		  replaceChildCollection(
			        score.getParts(),
			        entity.getParts(),
			        entity::setParts,
			        modelMapper,
			        Part::getPartId
			    );
		  
//		  if (score.getScoreComposers() != null) {
//		        for (ScoreComposer sc : score.getScoreComposers()) {
//		            sc.setScore(entity);
//		        }
//		    }
//		  replaceChildCollection(
//				  score.getScoreComposers(),
//				  entity.getScoreComposers(),
//				  entity::setScoreComposers,
//				  modelMapper,
//				  ScoreComposer::getScoreComposerId
//				  );
		  
		  if (score.getScoreTags() != null) {
		    for (ScoreTag tag : score.getScoreTags()) {
		      tag.setScore(entity);
		    }
		  }	
		  replaceChildCollection(
			        score.getScoreTags(),
			        entity.getScoreTags(),
			        entity::setScoreTags,
			        modelMapper,
			        ScoreTag::getScoreTagId
			    );
		  entity.setPurchasedCost(score.getPurchasedCost());
		  entity.setPurchasedDate(score.getPurchasedDate());
		  entity.setPurchasedFrom(score.getPurchasedFrom());
		  entity.setScoreSubtitle(score.getScoreSubtitle());
		  entity.setScoreTitle(score.getScoreTitle());

		return scoreRepository.save(entity);
	}

	public Page<Score> searchScore(String title, List<String> tags, Pageable pageable) {
		if(title.isBlank() && tags.size() == 0) {
			throw new ScoreValidationException("Must include at least one search parameter.");
		}
		
		List<Specification<Score>> specs = new ArrayList<>();

        if (StringUtils.hasText(title)) {
            specs.add(ScoreSpecification.titleContains(title));
        }

        if (tags != null && !tags.isEmpty()) {
            specs.add(ScoreSpecification.hasAnyTag(tags));
        }

        Specification<Score> combinedSpec = Specification.allOf(specs);		
		
	    return scoreRepository.findAll(combinedSpec, pageable);	    
	}

	
	public void deleteScore(Long scoreId, String userName) {
		Score entity = scoreRepository.findById(scoreId)
		        .orElseThrow(() -> new EntityNotFoundException("Score not found"));
		
		Account account = accountRepository.findByUsername(userName);
		if(account == null) throw new EntityNotFoundException("User not found.  Cannot delete score: " + scoreId);
		
		entity.setDeletedAt(LocalDateTime.now());
		entity.setDeletedBy(account);
		
		scoreRepository.delete(entity);
	}
	
	private <T> void replaceChildCollection(
	        List<T> incoming,
	        List<T> current,
	        Consumer<List<T>> setter,
	        ModelMapper mapper,
	        Function<T, Object> idExtractor) {

	    if (incoming == null) {
	        setter.accept(new ArrayList<>());
	        return;
	    }

	    if (current == null) {
	        current = new ArrayList<>();
	    }

	    // Remove orphans (parts/medleys/tags that were removed in the UI)
	    current.removeIf(existing ->
	        incoming.stream().noneMatch(inc ->
	            Objects.equals(idExtractor.apply(inc), idExtractor.apply(existing))
	        )
	    );

	    // Update existing or add new
	    for (T inc : incoming) {
	        T existing = current.stream()
	                .filter(e -> Objects.equals(idExtractor.apply(inc), idExtractor.apply(e)))
	                .findFirst()
	                .orElse(null);

	        if (existing != null) {
	            mapper.map(inc, existing);   // ← updates the managed entity (no INSERT)
	        } else {
	            current.add(inc);
	        }
	    }
	    setter.accept(current);
	}
}

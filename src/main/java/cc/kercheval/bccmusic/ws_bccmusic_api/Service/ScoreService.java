package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreSpecification;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.ScoreValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.MusicScore;
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

	public MusicScore getScoreById(Long scoreId) {
		return scoreRepository.findById(scoreId)
                .map(score -> modelMapper.map(score, MusicScore.class))
                .orElseThrow(() -> new EntityNotFoundException("Score not found with id: " + scoreId));
	}

	public List<MusicScore> getAllScores() {
		return StreamSupport.stream(scoreRepository.findAll().spliterator(), false)
                .map(s -> modelMapper.map(s, MusicScore.class))
                .toList();
	}

	public List<MusicScore> getScoresByAccountId(Long accountId) {
		List<Score> scores = scoreRepository.findScoresByOwner(accountId);
		List<MusicScore> musicScores = scores.stream()
				.map(s ->
				modelMapper.map(s, MusicScore.class))
			.collect(Collectors.toList());
		
		return musicScores;
	}

	@Transactional
	public Score createScore(Score score) {
		
		return scoreRepository.save(score);
	}

	@Transactional
	public MusicScore updateScore(MusicScore score) {
		Score entity = scoreRepository.findById(score.getScoreId())
		        .orElseThrow(() -> new EntityNotFoundException("Score not found"));

		modelMapper.map(entity, MusicScore.class);
		Score saved = scoreRepository.save(entity);
		return modelMapper.map(saved, MusicScore.class);
	}

	public Page<Score> searchScore(String title, List<String> tags, Pageable pageable) {
		if(title.isBlank() && tags.size() == 0) {
			throw new ScoreValidationException("Must include at least one search parameter.");
		}
		
		List<Specification<Score>> specs = new ArrayList<>();

        // Title filter (case-insensitive partial match)
        if (StringUtils.hasText(title)) {
            specs.add(ScoreSpecification.titleContains(title));
        }

        // Multiple tags → any match (OR logic)
        if (tags != null && !tags.isEmpty()) {
            specs.add(ScoreSpecification.hasAnyTag(tags));
        }

        // Add more filters here in the future, e.g.:
        // if (composerId != null) specs.add(hasComposer(composerId));
        // if (minGrade != null) specs.add(hasGradeAtLeast(minGrade));

        // Combine all specifications with AND
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
}

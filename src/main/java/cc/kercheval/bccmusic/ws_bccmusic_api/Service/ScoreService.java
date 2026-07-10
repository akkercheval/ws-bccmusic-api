package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreComment;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreComposer;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreSpecification;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreTag;
import cc.kercheval.bccmusic.ws_bccmusic_api.Mapper.ScoreMapper;
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
	private final ScoreMapper scoreMapper;

	// --- Public API ---

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

		linkChildrenToParent(score);

		return scoreRepository.save(score);
	}

	@Transactional
	public Score updateScore(Score incoming, Account editorAccount) {
		Score entity = scoreRepository.findById(incoming.getScoreId())
				.orElseThrow(() -> new EntityNotFoundException("Score not found"));

		updateScalarFields(entity, incoming, editorAccount);
		syncChildren(incoming, entity);

		return scoreRepository.save(entity);
	}

	public Page<Score> searchScore(String title, List<String> tags, Long accountId, Pageable pageable) {
		if ((title == null || title.isBlank())
				&& (tags == null || tags.isEmpty())
				&& accountId == null) {
			return scoreRepository.findAll(pageable);
		}

		List<Specification<Score>> specs = new ArrayList<>();

		if (StringUtils.hasText(title)) {
			specs.add(ScoreSpecification.titleContains(title));
		}
		if (tags != null && !tags.isEmpty()) {
			specs.add(ScoreSpecification.hasAnyTag(tags));
		}
		if (accountId != null) {
			specs.add(ScoreSpecification.hasOwner(accountId));
		}

		return scoreRepository.findAll(Specification.allOf(specs), pageable);
	}

	public void deleteScore(Long scoreId, String userName) {
		Score entity = scoreRepository.findById(scoreId)
				.orElseThrow(() -> new EntityNotFoundException("Score not found"));

		Account account = accountRepository.findByUsername(userName);
		if (account == null) {
			throw new EntityNotFoundException("User not found. Cannot delete score: " + scoreId);
		}

		entity.setDeletedAt(LocalDateTime.now());
		entity.setDeletedBy(account);
		scoreRepository.delete(entity);
	}

	// --- Score update helpers ---

	private void updateScalarFields(Score entity, Score incoming, Account editorAccount) {
		LocalDateTime currentTime = LocalDateTime.now();
		entity.setUpdatedAt(currentTime);
		entity.setUpdatedBy(editorAccount);
		entity.setScoreTitle(incoming.getScoreTitle());
		entity.setScoreSubtitle(incoming.getScoreSubtitle());
		entity.setArrangementType(incoming.getArrangementType());
		entity.setGrade(incoming.getGrade());
		entity.setPurchasedCost(incoming.getPurchasedCost());
		entity.setPurchasedDate(incoming.getPurchasedDate());
		entity.setPurchasedFrom(incoming.getPurchasedFrom());
	}

	// --- Child collection management ---

	/**
	 * Sets the parent back-reference on all child collections for a new Score.
	 */
	private void linkChildrenToParent(Score score) {
		setParentOnChildren(score.getMedleys(), m -> m.setScore(score));
		setParentOnChildren(score.getParts(), p -> p.setScore(score));
		setParentOnChildren(score.getScoreComposers(), sc -> sc.setScore(score));
		setParentOnChildren(score.getScoreTags(), t -> t.setScore(score));
		setParentOnChildren(score.getComments(), c -> c.setScore(score));
	}

	/**
	 * Synchronizes all child collections from the incoming Score onto the persisted entity.
	 * Each collection is: re-parented to the entity, then merged (add/update/remove).
	 */
	private void syncChildren(Score incoming, Score entity) {
		syncCollection(
				incoming.getMedleys(), entity.getMedleys(), entity::setMedleys,
				m -> m.setScore(entity), scoreMapper::updateMedley, Medley::getMedleyId
		);
		syncCollection(
				incoming.getParts(), entity.getParts(), entity::setParts,
				p -> p.setScore(entity), scoreMapper::updatePart, Part::getPartId
		);
		syncCollection(
				incoming.getScoreComposers(), entity.getScoreComposers(), entity::setScoreComposers,
				sc -> sc.setScore(entity), scoreMapper::updateScoreComposer, ScoreComposer::getScoreComposerId
		);
		syncCollection(
				incoming.getScoreTags(), entity.getScoreTags(), entity::setScoreTags,
				t -> t.setScore(entity), scoreMapper::updateScoreTag, ScoreTag::getScoreTagId
		);
		syncCollection(
				incoming.getComments(), entity.getComments(), entity::setComments,
				c -> c.setScore(entity), scoreMapper::updateScoreComment, ScoreComment::getCommentId
		);
	}

	/**
	 * Handles the full lifecycle of a single child collection:
	 * 1. Sets parent back-reference on incoming items
	 * 2. Merges incoming into current (adds new, updates existing, removes missing)
	 */
	private <T> void syncCollection(
			List<T> incoming,
			List<T> current,
			Consumer<List<T>> setter,
			Consumer<T> parentLinker,
			BiConsumer<T, T> updater,
			Function<T, Object> idExtractor) {

		setParentOnChildren(incoming, parentLinker);
		replaceChildCollection(incoming, current, setter, updater, idExtractor);
	}

	private <T> void setParentOnChildren(List<T> children, Consumer<T> parentLinker) {
		if (children != null) {
			children.forEach(parentLinker);
		}
	}

	/**
	 * Generic collection merge: removes items no longer present, updates existing by ID,
	 * and adds new items (those without an ID or with an ID not in current).
	 */
	private <T> void replaceChildCollection(
			List<T> incoming,
			List<T> current,
			Consumer<List<T>> setter,
			BiConsumer<T, T> updater,
			Function<T, Object> idExtractor) {

		if (incoming == null) {
			setter.accept(new ArrayList<>());
			return;
		}

		if (current == null) {
			current = new ArrayList<>();
		}

		// Remove items that are no longer in the incoming list
		current.removeIf(existing -> {
			Object exId = idExtractor.apply(existing);
			return exId != null &&
					incoming.stream().noneMatch(inc -> Objects.equals(exId, idExtractor.apply(inc)));
		});

		// Update existing or add new
		for (T inc : incoming) {
			Object incId = idExtractor.apply(inc);
			T existing = null;

			if (incId != null) {
				existing = current.stream()
						.filter(e -> Objects.equals(incId, idExtractor.apply(e)))
						.findFirst()
						.orElse(null);
			}

			if (existing != null) {
				updater.accept(existing, inc);
			} else {
				current.add(inc);
			}
		}

		setter.accept(current);
	}
}

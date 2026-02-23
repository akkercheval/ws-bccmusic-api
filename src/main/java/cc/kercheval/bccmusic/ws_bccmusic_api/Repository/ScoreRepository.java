package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {
	
	@Query("SELECT s FROM Score s WHERE s.owner.accountId = :accountId")	
	public List<Score> findScoresByOwner(Long accountId);
	
	public Score findByScoreId(Long scoreId);

	public Page<Score> findAll(Specification<Score> combinedSpec, Pageable pageable);
}

package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreTag;

public interface ScoreTagRepository extends JpaRepository<ScoreTag, Long> {
	
	@Query("SELECT DISTINCT t.tag FROM ScoreTag t ORDER BY t.tag")
	public List<String> findDistinctTags();

}

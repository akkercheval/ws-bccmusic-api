package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreTag;

public interface ScoreTagRepository extends CrudRepository<ScoreTag, Long> {
	
	@Query("SELECT DISTINCT t.tag FROM ScoreTag t")
	public List<String> findDistinctTags();

}

package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ScoreTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreTagService {
	
	private final ScoreTagRepository scoreTagRepository;
	
	public List<String> getDistinctTags() {
		return scoreTagRepository.findDistinctTags();
	}

}

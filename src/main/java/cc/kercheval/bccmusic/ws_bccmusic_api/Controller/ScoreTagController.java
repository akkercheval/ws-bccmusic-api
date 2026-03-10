package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Service.ScoreTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/score-tags")
@RequiredArgsConstructor
public class ScoreTagController {
	
	private final ScoreTagService scoreTagService;
	
	@GetMapping
	public List<String> getDistinctTags() {
		return scoreTagService.getDistinctTags();
	}
}
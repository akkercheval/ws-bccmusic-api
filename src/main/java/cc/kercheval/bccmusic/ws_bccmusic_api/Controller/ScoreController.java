package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.MusicScore;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/scores")
@RequiredArgsConstructor
public class ScoreController {
	
	private final ScoreService scoreService;
	private final AccountService accountService;
	private final ModelMapper modelMapper;
	
	@GetMapping
	public List<MusicScore> getAllScores() {
		return scoreService.getAllScores(); 
	}
	
	@GetMapping(value = "/{scoreId}")
	public MusicScore getScoreById(@PathVariable Long scoreId) {
		
		return scoreService.getScoreById(scoreId);
	}
	
	@GetMapping(value = "/search")
	public Page<MusicScore> searchScores(
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) List<String> tags,
	        @PageableDefault(size = 25, sort = "scoreTitle") Pageable pageable) {
		Page<Score> pageableScore = scoreService.searchScore(title, tags, pageable);
		return pageableScore.map(s -> modelMapper.map(s, MusicScore.class));
	}
	
	@GetMapping("/my-scores")
    public List<MusicScore> getMyScores(Principal principal) {

        Long accountId = getAccountFromPrincipal(principal).getAccountId();
        return scoreService.getScoresByAccountId(accountId);
    }
	
	@GetMapping("/other-scores")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasViewScoresPermission(#accountId, authentication)")
	public List<MusicScore> getScoresByAccountId(@RequestParam(required = true) Long accountId, Principal principal) {
	
		return scoreService.getScoresByAccountId(accountId);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("@collaboratorPermissionEvaluator.hasBasicEditScoresPermission(#score.ownerAccountId, authentication)")
	public MusicScore createScore(@Valid @RequestBody MusicScore score, Principal principal) {
		Score newScore = modelMapper.map(score, Score.class);
		newScore.setCreatedBy(getAccountFromPrincipal(principal));
				
		return modelMapper.map(scoreService.createScore(newScore), MusicScore.class);
	}
	
	@PutMapping("/{scoreId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasBasicEditScoresPermission(#accountId, authentication)")
	public MusicScore updateScore(@PathVariable Long scoreId, @Valid @RequestBody MusicScore score) {
		if (!scoreId.equals(score.getScoreId())) {
            throw new IllegalArgumentException("Score ID in path must match body");
        }		
		return scoreService.updateScore(score);
	}
	
	@DeleteMapping("/{scoreId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasFullEditScoresPermission(#accountId, authentication)")
	public void deleteScore(@PathVariable Long scoreId, Principal principal) {
		scoreService.deleteScore(scoreId, principal.getName());
	}
	
	private Account getAccountFromPrincipal(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}
}
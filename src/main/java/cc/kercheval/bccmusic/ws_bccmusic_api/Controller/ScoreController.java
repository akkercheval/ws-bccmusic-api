package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.io.Console;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import cc.kercheval.bccmusic.ws_bccmusic_api.security.evaluator.CollaboratorPermissionEvaluator;
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
	private final CollaboratorPermissionEvaluator permissionEvaluator;
	
	@GetMapping
	public List<MusicScore> getAllScores() {
		List<Score> allScores = scoreService.getAllScores();
		
		List<MusicScore> allMusicScores = 
		StreamSupport.stream(allScores.spliterator(), false)
        .map(s -> modelMapper.map(s, MusicScore.class))
        .toList();
		return allMusicScores;
	}
	
	@GetMapping(value = "/{scoreId}")
	public MusicScore getScoreById(@PathVariable Long scoreId) {
		
		return modelMapper.map(scoreService.getScoreById(scoreId), MusicScore.class);
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
        List<MusicScore> myScores = 
        scoreService.getScoresByAccountId(accountId).stream()
				.map(s ->
				modelMapper.map(s, MusicScore.class))
			.collect(Collectors.toList());
        log.info("My Scores: {}", myScores.toString());
        return myScores;
			
    }
	
	@GetMapping("/other-scores")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasViewScoresPermission(#accountId, authentication)")
	public List<MusicScore> getScoresByAccountId(@RequestParam(required = true) Long accountId, Principal principal) {
	
		return scoreService.getScoresByAccountId(accountId).stream()
				.map(s ->
				modelMapper.map(s, MusicScore.class))
			.collect(Collectors.toList());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("@collaboratorPermissionEvaluator.hasBasicEditScoresPermission(#score.owner.accountId, authentication)")
	public MusicScore createScore(@Valid @RequestBody MusicScore score, Principal principal) {
		log.info("Creating new music score: {}", score.toString());
		Score newScore = modelMapper.map(score, Score.class);
		Account editorAccount = getAccountFromPrincipal(principal);
				
		return modelMapper.map(scoreService.createScore(newScore, editorAccount), MusicScore.class);
	}
	
	@PutMapping("/{scoreId}")
	public MusicScore updateScore(@PathVariable Long scoreId, @Valid @RequestBody MusicScore score, Authentication authentication, Principal principal) {
		
		if (!scoreId.equals(score.getScoreId())) {
            throw new IllegalArgumentException("Score ID in path must match body");
		}
		Score scoreToBeDeleted = scoreService.getScoreById(scoreId);
		if(!permissionEvaluator.hasBasicEditScoresPermission(scoreToBeDeleted.getOwner().getAccountId(), authentication)) {
			throw new AccessDeniedException("User does not have permission to delete score.");
		}
		Score scoreEntity = modelMapper.map(score, Score.class);
		Account editorAccount = getAccountFromPrincipal(principal);
		System.out.println("Medleys to Save: " + scoreEntity.getMedleys().toString());
		
		return modelMapper.map(scoreService.updateScore(scoreEntity, editorAccount), MusicScore.class);
	}
	
	@DeleteMapping("/{scoreId}")
	public void deleteScore(@PathVariable Long scoreId, Authentication authentication, Principal principal) {
		Score scoreToBeDeleted = scoreService.getScoreById(scoreId);
		if(!permissionEvaluator.hasFullEditScoresPermission(scoreToBeDeleted.getOwner().getAccountId(), authentication)) {
			throw new AccessDeniedException("User does not have permission to delete score.");
		}
		scoreService.deleteScore(scoreId, principal.getName());
	}
	
	private Account getAccountFromPrincipal(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}
}
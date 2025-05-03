package aiss.BitbucketMiner.BitbucketMiner.controllers;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class CommitController {

    @Autowired
    CommitService commitService;

    @GetMapping("/{workspace}/{repo_slug}/commits")
    public List<MinerCommit> getCommits(
            @PathVariable String workspace,
            @PathVariable("repo_slug") String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        return commitService.getCommits(workspace, repoSlug, nCommits, maxPages);
    }


    @GetMapping("/commits/{id}")
    public MinerCommit getCommit(@PathVariable String id) {
        return commitService.getCommitById("gentlero", "bitbucket-api", id);
    }

    @PostMapping("/{workspace}/{repo_slug}")
    public String sendCommitsToGitMiner(
            @PathVariable String workspace,
            @PathVariable("repo_slug") String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        int saved = commitService.sendCommitsToGitMiner(workspace, repoSlug, nCommits, maxPages);
        return saved + " commits enviados a GitMiner correctamente.";
    }

}
package aiss.BitbucketMiner.BitbucketMiner.controllers;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit;
import aiss.BitbucketMiner.BitbucketMiner.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class CommitController {

    @Autowired
    CommitService CommitService;

    @GetMapping("/{workspace}/{repo_slug}/commits")
    public List<Commit> getCommits(
            @PathVariable String workspace,
            @PathVariable("repo_slug") String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages) throws Exception {

        return CommitService.getCommits(workspace, repoSlug, nCommits, maxPages);
    }
}
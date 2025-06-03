package aiss.BitbucketMiner.BitbucketMiner.controllers;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.service.CommitService;
import aiss.BitbucketMiner.BitbucketMiner.service.IssueService;
import aiss.BitbucketMiner.BitbucketMiner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    CommitService commitService;

    @Autowired
    IssueService issueService;

    @GetMapping("/{workspace}/{repoSlug}")
    public List<MinerProject> getProjects(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "1") int maxPages
    ) {
        String projectUuid = commitService.getProjectUuidFromRepo(workspace, repoSlug);

        List<MinerCommit> commits = commitService.getCommits(workspace, repoSlug, projectUuid, nCommits, maxPages);
        List<MinerIssue> issues = issueService.getIssues(workspace, repoSlug, projectUuid, nIssues, maxPages);

        return projectService.getProjects(workspace, commits, issues);
    }


    @PostMapping("/{owner}/{repoName}")
    public String sendProjectFromRepoToGitMiner(
            @PathVariable String owner,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "5") int sinceCommits,
            @RequestParam(defaultValue = "30") int sinceIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {

        String projectUuid = commitService.getProjectUuidFromRepo(owner, repoName);
        String projectUuidIssue = issueService.getProjectUuidFromRepo(owner, repoName);

        List<MinerCommit> commits = commitService.getCommits(owner, repoName, projectUuid, sinceCommits, maxPages);
        List<MinerIssue> issues = issueService.getIssues(owner, repoName, projectUuidIssue, sinceIssues, maxPages);

        int sent = projectService.sendProjectsToGitMiner(owner, commits, issues);
        return sent + " proyecto enviado a GitMiner correctamente." ;
    }

}

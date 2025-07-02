package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.issue.Issue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.transformer.IssueTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IssueServiceTest {

    @Value("${bitbucketminer.maxpages}")
    private int maxPages;

    @Value("${bitbucketminer.nissues}")
    private int nIssues;

    @Autowired
    IssueService issueService;

    @Test
    public void testToGitMinerIssue() {
        Issue bitbucketIssue = new Issue();
        bitbucketIssue.setId(123);
        bitbucketIssue.setTitle("Test Issue");
        bitbucketIssue.setState("new");

        MinerIssue transformed = IssueTransformer.toGitMinerIssue(bitbucketIssue);

        assertNotNull(transformed, "El MinerIssue no debe ser nulo");
        assertEquals("123", transformed.getId());
        assertEquals("Test Issue", transformed.getTitle());
        assertEquals("new", transformed.getState());
    }
/*
    @Test
    @DisplayName("Get issues from Bitbucket, transform to GitMiner format and print them")
    public void getIssues() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";
        int nIssues = 5;
        int maxPages = 1;

        List<MinerIssue> issues = issueService.getIssues(workspace, repoSlug, nIssues, maxPages);

        assertNotNull(issues);
        assertFalse(issues.isEmpty());

        issues.forEach(issueService::printIssue);
    }
    */

    @Test
    @DisplayName("Get a single issue by ID")
    public void getSingleIssue() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";
        String issueId = "1"; // Debes usar un ID real aquí

        MinerIssue issue = issueService.getIssueById(workspace, repoSlug, issueId);
        assertNotNull(issue, "El MinerIssue no debe ser nulo");
        issueService.printIssue(issue);
    }

    @Test
    @DisplayName("Send issues from Bitbucket to GitMiner")
    public void sendIssuesToGitMiner_test() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";

        int sent = issueService.sendIssuesToGitMiner(workspace, repoSlug, nIssues, maxPages);
        System.out.println("Issues enviados: " + sent);
        assertTrue(sent > 0, "El número de issues enviadas no puede ser menor que 1");
    }
}

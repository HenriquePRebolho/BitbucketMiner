package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommitTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CommitServiceTest {

    @Test
    public void testToGitMinerCommit() throws Exception {
        // Simulación de un commit mínimo
        BitBucketCommit bitbucketCommit = new BitBucketCommit();
        bitbucketCommit.setHash("abc123");
        bitbucketCommit.setMessage("Mensaje de prueba");

        MinerCommit transformed = CommitTransformer.toGitMinerCommit(bitbucketCommit);


        assertNotNull(transformed);
        assertEquals("abc123", transformed.getId());
        assertEquals("Mensaje de prueba", transformed.getMessage());
    }

    @Autowired
    CommitService commitService;

    @Test
    @DisplayName("Get commits from Bitbucket transform them to GitMiner format and print them on the console")
    public void getCommits() {
        String workspace = "gentlero"; // ejemplo real
        String repoSlug = "bitbucket-api"; // ejemplo real
        int nCommits = 5;
        int maxPages = 1;

        List<MinerCommit> commits = commitService.getCommits(workspace, repoSlug, nCommits, maxPages);

        assertNotNull(commits);
        assertFalse(commits.isEmpty());

        // Imprimir los commits transformados a formato GitMiner
        commits.forEach(commitService::printCommit);
    }

    @Test
    @DisplayName("Get commit by commit id")
    public void getSingleCommit() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";
        String commitId = "67a0362b29f34c45251ce88c5851756fb30a65cc"; // Usa un ID real

        MinerCommit commit = commitService.getCommitById(workspace, repoSlug, commitId);
        commitService.printCommit(commit);

    }
// Esta prueba solo se puede realizar una vex completo git miner y esta ejecutandose en el puerto adecuado
    @Test
    @DisplayName("Enviar commits desde Bitbucket a GitMiner")
    public void sendCommitsToGitMiner_test() throws Exception {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";
        int nCommits = 5;
        int maxPages = 1;

        commitService.sendCommitsToGitMiner(workspace, repoSlug, nCommits, maxPages);
    }
}

package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommitTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommitService {

    @Autowired
    RestTemplate restTemplate;

    public List<MinerCommit> getCommits(String workspace, String repoSlug, int nCommits, int maxPages) {
        List<MinerCommit> result = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            String uri = String.format(
                    "https://api.bitbucket.org/2.0/repositories/%s/%s/commits?pagelen=%d&page=%d",
                    workspace, repoSlug, nCommits, page);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            );

            try {
                JsonNode valuesNode = response.getBody().get("values");
                ObjectMapper mapper = new ObjectMapper();
                BitBucketCommit[] commits = mapper.treeToValue(valuesNode, BitBucketCommit[].class);

                if (commits != null) {
                    for (BitBucketCommit commit : commits) {
                        result.add(CommitTransformer.toGitMinerCommit(commit));
                    }
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error parsing JSON from Bitbucket: " + e.getMessage());
                e.printStackTrace(); // Opcional: comentar si no quieres stacktrace
            }
        }

        return result;
    }

    public MinerCommit getCommitById(String workspace, String repoSlug, String commitId) {
        String uri = String.format("https://api.bitbucket.org/2.0/repositories/%s/%s/commit/%s",
                workspace, repoSlug, commitId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<BitBucketCommit> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                BitBucketCommit.class
        );

        return CommitTransformer.toGitMinerCommit(response.getBody());
    }

    // Metodo que nos piden para POST commits desde bitbucket a git miner
    public int sendCommitsToGitMiner(String workspace, String repoSlug, int nCommits, int maxPages) {
        List<MinerCommit> commits = getCommits(workspace, repoSlug, nCommits, maxPages);
        String gitMinerUrl = "http://localhost:8080/commits"; // URL real del endpoint de GitMiner

        int sent = 0;
        for (MinerCommit commit : commits) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MinerCommit> request = new HttpEntity<>(commit, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("✔ Commit enviado correctamente: " + commit.getId());
                    sent++;
                } else {
                    System.err.println("✖ Error al enviar commit " + commit.getId() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println("⚠ Error al enviar commit " + commit.getId() + ": " + e.getMessage());
            }
        }
        return sent;
    }




    public void printCommit(MinerCommit commit) {
        if (commit != null) {
            System.out.println(" COMMIT [" + commit.getId() + "]");
            System.out.println("    - Title: " + commit.getTitle());
            System.out.println("    - Message: " + commit.getMessage());
            System.out.println("    - Author Name: " + commit.getAuthorName());
            System.out.println("    - Author Email: " + commit.getAuthorEmail());
            System.out.println("    - Authored Date: " + commit.getAuthoredDate());
            System.out.println("    - Web URL: " + commit.getWebUrl());
        } else {
            System.out.println("COMMIT [NULL]");
        }
    }
}

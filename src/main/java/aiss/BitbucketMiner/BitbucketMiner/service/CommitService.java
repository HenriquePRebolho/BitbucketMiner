package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommitTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommitService {

    @Value("${gitminer.api.url}")
    private String gitminerApiUrl;

    @Value("${bitbucketminer.baseuri}")
    private String baseuri;


    @Autowired
    RestTemplate restTemplate;

    public List<MinerCommit> getCommits(String workspace, String repoSlug, String projectUuid, int nCommits, int maxPages) {
        List<MinerCommit> result = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            String uri = baseuri + workspace + "/" + repoSlug + "/commits?pagelen=" + nCommits + "&page=" + page;


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
                        MinerCommit mc = CommitTransformer.toGitMinerCommit(commit);
                        mc.setProjectId(projectUuid); //  Aquí asigno el projectId
                        result.add(mc);
                    }
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error parsing JSON from Bitbucket: " + e.getMessage());
                e.printStackTrace();
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

        String projectUuid = getProjectUuidFromRepo(workspace, repoSlug);

        List<MinerCommit> commits = getCommits(workspace, repoSlug,projectUuid, nCommits, maxPages);
        String gitMinerUrl = gitminerApiUrl + "/commits"; // URL real del endpoint de GitMiner

        int sent = 0;
        for (MinerCommit commit : commits) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MinerCommit> request = new HttpEntity<>(commit, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Commit enviado correctamente: " + commit.getTitle());
                    sent++;
                } else {
                    System.err.println(" Error al enviar commit " + commit.getId() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println(" Error al enviar commit " + commit.getId() + ": " + e.getMessage());
            }
        }
        return sent;
    }

    // estre metodo es importante porque lo usamos para asociar issues commits users y comments de un mismo project

    public String getProjectUuidFromRepo(String workspace, String repoSlug) {
        String uri = String.format("https://api.bitbucket.org/2.0/repositories/%s/%s", workspace, repoSlug);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);
            JsonNode projectNode = response.getBody().get("project");
            if (projectNode != null && projectNode.get("uuid") != null) {
                return projectNode.get("uuid").asText();
            } else {
                System.err.println("No se encontró project UUID para el repo: " + repoSlug);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener UUID del proyecto desde el repo: " + e.getMessage());
        }

        return null;
    }



    public void printCommit(MinerCommit commit) {
        if (commit != null) {
            System.out.println(" COMMIT [" + commit.getId() + "]");
            System.out.println("    - Title: " + commit.getTitle());
            System.out.println("    - Message: " + commit.getMessage());
            System.out.println("    - Author Name: " + commit.getAuthor_name());
            System.out.println("    - Author Email: " + commit.getAuthor_email());
            System.out.println("    - Authored Date: " + commit.getAuthored_date());
            System.out.println("    - Web URL: " + commit.getWeb_url());
        } else {
            System.out.println("COMMIT [NULL]");
        }
    }
}

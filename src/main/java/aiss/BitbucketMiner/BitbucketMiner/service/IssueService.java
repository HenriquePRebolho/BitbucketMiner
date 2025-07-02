package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerComment;
import aiss.BitbucketMiner.BitbucketMiner.model.issue.Issue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommentTransformer;
import aiss.BitbucketMiner.BitbucketMiner.transformer.IssueTransformer;
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
public class IssueService {

    @Value("${gitminer.api.url}")
    private String gitminerApiUrl;

    @Value("${bitbucketminer.baseuri}")
    private String baseuri;

    @Autowired
    RestTemplate restTemplate;

    public List<MinerIssue> getIssues(String workspace, String repoSlug, String projectUuid, int nIssues, int maxPages) {
        List<MinerIssue> result = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {

            String uri = baseuri + workspace + "/" + repoSlug + "/issues?pagelen=" + nIssues + "&page=" + page;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            );

            try {
                JsonNode valuesNode = response.getBody().get("values");
                if (valuesNode == null || !valuesNode.isArray() || valuesNode.size() == 0) {
                    // No hay más issues que procesar, salimos del bucle
                    break;
                }

                ObjectMapper mapper = new ObjectMapper();
                Issue[] issues = mapper.treeToValue(valuesNode, Issue[].class);

                for (Issue issue : issues) {
                    MinerIssue mi = IssueTransformer.toGitMinerIssue(issue);
                    mi.setProjectId(projectUuid);
                    result.add(mi);
                }

                // Si el número de issues recibidos es menor al pagelen, no hay más páginas
                if (valuesNode.size() < nIssues) {
                    break;
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error parsing JSON from Bitbucket: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }


    public MinerIssue getIssueById(String workspace, String repoSlug, String issueId) {
        String uri = String.format("https://api.bitbucket.org/2.0/repositories/%s/%s/issues/%s",
                workspace, repoSlug, issueId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Issue> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                Issue.class
        );

        return IssueTransformer.toGitMinerIssue(response.getBody());
    }

    public int sendIssuesToGitMiner(String workspace, String repoSlug, int nIssues, int maxPages) {
        String projectUuid = getProjectUuidFromRepo(workspace, repoSlug);
        List<MinerIssue> issues = getIssues(workspace, repoSlug, projectUuid, nIssues, maxPages);

        String gitMinerUrl = gitminerApiUrl + "/issues";

        int sent = 0;
        for (MinerIssue issue : issues) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MinerIssue> request = new HttpEntity<>(issue, headers);

                System.out.println("Enviando proyecto: " + new ObjectMapper().writeValueAsString(issues));

                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Issue enviada correctamente: " + issue.getAuthor());
                    sent++;
                } else {
                    System.err.println("Error al enviar issue " + issue.getTitle() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println("Error al enviar issue " + issue.getTitle() + ": " + e.getMessage());
            }
        }
        return sent;
    }

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

    // este metodo lo tenemos que usar para extraer los comentrarios de cada issue

    public static List<MinerComment> getCommentsFromIssue(Issue bitbucketIssue) {
        List<MinerComment> result = new ArrayList<>();

        String commentsUrl = bitbucketIssue.getLinks().getComments().getHref();

        if (commentsUrl != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ResponseEntity<JsonNode> response = new RestTemplate().exchange(
                        commentsUrl, HttpMethod.GET, requestEntity, JsonNode.class
                );

                JsonNode valuesNode = response.getBody().get("values");
                ObjectMapper mapper = new ObjectMapper();

                for (JsonNode node : valuesNode) {
                    aiss.BitbucketMiner.BitbucketMiner.model.Comments original =
                            mapper.treeToValue(node, aiss.BitbucketMiner.BitbucketMiner.model.Comments.class);

                    // Validación: ignorar comentarios sin contenido válido
                    if (original.getContent() != null && original.getContent().getRaw() != null) {
                        MinerComment transformed = CommentTransformer.toGitMinerComment(original);
                        if (transformed != null && transformed.getBody() != null) {
                            result.add(transformed);
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error al obtener comentarios del issue: " + e.getMessage());
            }
        }

        return result;
    }




    public void printIssue(MinerIssue issue) {
        if (issue != null) {
            System.out.println(" ISSUE [");
            System.out.println("    - Title: " + issue.getTitle());
            System.out.println("    - Description: " + issue.getDescription());
            System.out.println("    - State: " + issue.getState());
            System.out.println("    - CreatedAt: " + issue.getCreatedAt());
            System.out.println("    - UpdatedAt: " + issue.getUpdatedAt());
            System.out.println("    - ClosedAt: " + issue.getClosedAt());
            System.out.println("    - Votes: " + issue.getVotes());
        } else {
            System.out.println("ISSUE [NULL]");
        }
    }
}
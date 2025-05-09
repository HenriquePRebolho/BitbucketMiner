package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Comments;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerComment;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommentTransformer;
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
public class
CommentService {

    @Autowired
    RestTemplate restTemplate;

    public List<MinerComment> getComments(String workspace, String repoSlug, int issueId, int maxPages) {
        List<MinerComment> result = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            String uri = String.format(
                    "https://api.bitbucket.org/2.0/repositories/%s/%s/issues/%d/comments?page=%d",
                    workspace, repoSlug, issueId, page);

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
                Comments[] comments = mapper.treeToValue(valuesNode, Comments[].class);

                if (comments != null) {
                    for (Comments comment : comments) {
                        result.add(CommentTransformer.toGitMinerComment(comment));
                    }
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error parsing JSON from Bitbucket: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }

    public int sendCommentsToGitMiner(String workspace, String repoSlug, int issueId, int maxPages) {
        List<MinerComment> comments = getComments(workspace, repoSlug, issueId, maxPages);
        String gitMinerUrl = "http://localhost:8080/gitminer/comments";

        int sent = 0;
        for (MinerComment comment : comments) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MinerComment> request = new HttpEntity<>(comment, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Comentario enviado correctamente: " + comment.getAuthor());
                    sent++;
                } else {
                    System.err.println("Error al enviar comentario " + comment.getId() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println("Error al enviar comentario " + comment.getId() + ": " + e.getMessage());
            }
        }
        return sent;
    }

    public void printComment(MinerComment comment) {
        if (comment != null) {
            System.out.println(" COMMENT [" + comment.getId() + "]");
            System.out.println("    - Body: " + comment.getBody());
            System.out.println("    - Created at: " + comment.getCreatedAt());
            System.out.println("    - Updated at: " + comment.getUpdatedAt());
        } else {
            System.out.println("COMMENT [NULL]");
        }
    }
}

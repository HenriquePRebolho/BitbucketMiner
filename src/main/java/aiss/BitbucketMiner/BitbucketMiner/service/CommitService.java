package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommitService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public List<Commit> getCommits(String workspace, String repoSlug, int nCommits, int maxPages) throws Exception {
        List<Commit> result = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            String uri = String.format(
                    "https://api.bitbucket.org/2.0/repositories/%s/%s/commits?page=%d&pagelen=%d",
                    workspace, repoSlug, page, nCommits
            );

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode values = root.get("values");

            if (values != null && values.isArray()) {
                for (JsonNode node : values) {
                    Commit commit = new Commit();
                    commit.setId(node.get("hash").asText());
                    commit.setMessage(node.get("message").asText());
                    commit.setTitle(node.get("summary").get("raw").asText().split("\n")[0]);
                    commit.setAuthorName(node.get("author").get("user").get("display_name").asText());
                    commit.setAuthorEmail(extractEmail(node.get("author").get("raw").asText()));
                    commit.setAuthoredDate(node.get("date").asText());
                    commit.setWebUrl(node.get("links").get("html").get("href").asText());
                    result.add(commit);
                }
            } else {
                break;
            }
        }

        return result;
    }

    private String extractEmail(String raw) {
        int start = raw.indexOf("<");
        int end = raw.indexOf(">");
        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start + 1, end);
        }
        return "";
    }
}

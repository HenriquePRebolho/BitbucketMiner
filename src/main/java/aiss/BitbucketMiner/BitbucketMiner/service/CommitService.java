package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommitTransformer;
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

            // En este caso no necesitas token, pero mantenemos headers por coherencia
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<BitBucketCommit[]> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    requestEntity,
                    BitBucketCommit[].class
            );

            BitBucketCommit[] commits = response.getBody();
            if (commits != null) {
                for (BitBucketCommit commit : commits) {
                    result.add(CommitTransformer.toGitMinerCommit(commit));
                }
            }
        }

        return result;
    }


}

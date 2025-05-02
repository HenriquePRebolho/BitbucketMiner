package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommitTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;

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
}

package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerComment;
import aiss.BitbucketMiner.BitbucketMiner.model.issue.Issue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.service.IssueService;
import aiss.BitbucketMiner.BitbucketMiner.transformer.UserTransformer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class IssueTransformer {

    public static MinerIssue toGitMinerIssue(Issue bitbucketIssue) {
        if (bitbucketIssue == null) return null;

        MinerIssue result = new MinerIssue();
        List<String> labels = new ArrayList<>();

        labels.add(bitbucketIssue.getKind());

        result.setTitle(bitbucketIssue.getTitle());
        result.setDescription(bitbucketIssue.getContent() != null ? bitbucketIssue.getContent().getRaw() : null);

        result.setState(bitbucketIssue.getState());

        result.setCreatedAt(bitbucketIssue.getCreatedOn());

        result.setUpdatedAt(bitbucketIssue.getUpdatedOn());

        result.setClosedAt("closed".equalsIgnoreCase(bitbucketIssue.getState()) ? bitbucketIssue.getUpdatedOn() : null);

        result.setLabels(new java.util.ArrayList<>());

        result.setVotes(bitbucketIssue.getVotes() != null ? bitbucketIssue.getVotes() : 0);

        result.setAuthor(UserTransformer.toGitMinerUser(bitbucketIssue.getReporter()));

        List<MinerComment> comments = IssueService.getCommentsFromIssue(bitbucketIssue);

        result.setComments(comments);

        result.setLabels(labels);



        return result;
    }



}

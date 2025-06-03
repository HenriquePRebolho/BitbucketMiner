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

        // Title y Description (content.raw)
        result.setTitle(bitbucketIssue.getTitle());
        result.setDescription(bitbucketIssue.getContent() != null ? bitbucketIssue.getContent().getRaw() : null);

        // Estado (open/closed/etc.)
        result.setState(bitbucketIssue.getState());

        // Fechas
        result.setCreatedAt(bitbucketIssue.getCreatedOn());
        result.setUpdatedAt(bitbucketIssue.getUpdatedOn());

        // Fecha de cierre (solo si el estado es "closed")
        result.setClosedAt("closed".equalsIgnoreCase(bitbucketIssue.getState()) ? bitbucketIssue.getUpdatedOn() : null);

        // Etiquetas vacías (Bitbucket no proporciona)
        result.setLabels(new java.util.ArrayList<>());

        // Votos
        result.setVotes(bitbucketIssue.getVotes() != null ? bitbucketIssue.getVotes() : 0);

        // Autor del issue (reporter)
        result.setAuthor(UserTransformer.toGitMinerUser(bitbucketIssue.getReporter()));

        // Comentarios
        List<MinerComment> comments = IssueService.getCommentsFromIssue(bitbucketIssue);
        result.setComments(comments);



        return result;
    }



}

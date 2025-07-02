package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Comments;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerComment;
import aiss.BitbucketMiner.BitbucketMiner.transformer.CommentTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentServiceTest {

    @Value("2")
    private int maxPages;

    private final int issueId = 1;

    @Autowired
    CommentService commentService;

    @Test
    public void testToGitMinerComment() {
        // Simulación de un comentario mínimo
        Comments comment = new Comments();
        comment.setId(1);
        comment.setContent(new aiss.BitbucketMiner.BitbucketMiner.model.issue.Content());
        comment.getContent().setRaw("Comentario de prueba");
        comment.setCreatedOn("2024-01-01T12:00:00Z");
        comment.setUpdatedOn("2024-01-01T13:00:00Z");

        MinerComment transformed = CommentTransformer.toGitMinerComment(comment);

        assertNotNull(transformed);
        assertEquals("1", transformed.getId());
        assertEquals("Comentario de prueba", transformed.getBody());
        assertEquals("2024-01-01T12:00:00Z", transformed.getCreatedAt());
        assertEquals("2024-01-01T13:00:00Z", transformed.getUpdatedAt());
    }

    @Test
    @DisplayName("Get comments from Bitbucket, transform them to GitMiner format and print them on the console")
    public void getComments() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";

        List<MinerComment> comments = commentService.getComments(workspace, repoSlug, issueId, maxPages);

        assertNotNull(comments);
        assertFalse(comments.isEmpty());

        // Imprimir los comentarios transformados
        comments.forEach(commentService::printComment);
    }

    @Test
    @DisplayName("Enviar comentarios desde Bitbucket a GitMiner")
    public void sendCommentsToGitMiner_test() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";

        commentService.sendCommentsToGitMiner(workspace, repoSlug, issueId, maxPages);
    }
}

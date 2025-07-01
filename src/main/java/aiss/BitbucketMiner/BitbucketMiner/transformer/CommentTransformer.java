package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.Comments;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerComment;

public class CommentTransformer {

    public static MinerComment toGitMinerComment(Comments bitbucketComment) {
        if (bitbucketComment == null) return null;

        MinerComment result = new MinerComment();

        result.setBody(
                bitbucketComment.getContent() != null ? bitbucketComment.getContent().getRaw() : null
        );

        result.setAuthor(UserTransformer.toGitMinerUser(bitbucketComment.getUser()));

        result.setCreatedAt(bitbucketComment.getCreatedOn());
        result.setUpdatedAt(
                bitbucketComment.getUpdatedOn() != null ? bitbucketComment.getUpdatedOn().toString() : null
        );

        return result;
    }
}
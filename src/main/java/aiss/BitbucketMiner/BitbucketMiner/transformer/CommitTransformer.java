package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;


public class CommitTransformer {

    public static MinerCommit toGitMinerCommit(BitBucketCommit source) {
        MinerCommit target = new MinerCommit();
        
        target.setId(source.getHash());

        if (source.getSummary() != null && source.getSummary().getRaw() != null) {
            target.setTitle(source.getSummary().getRaw().split("\n")[0]);
        }
        target.setMessage(source.getMessage());
        if (source.getAuthor() != null && source.getAuthor().getUser() != null) {
            target.setAuthorName(source.getAuthor().getUser().getDisplayName());
        }

        if (source.getAuthor() != null && source.getAuthor().getRaw() != null) {
            target.setAuthorEmail(extractEmail(source.getAuthor().getRaw()));
        }

        target.setAuthoredDate(source.getDate());

        if (source.getLinks() != null && source.getLinks().getHtml() != null) {
            target.setWebUrl(source.getLinks().getHtml().getHref());
        }

        return target;
    }

    private static String extractEmail(String raw) {
        int start = raw.indexOf("<");
        int end = raw.indexOf(">");
        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start + 1, end);
        }
        return null;
    }

}

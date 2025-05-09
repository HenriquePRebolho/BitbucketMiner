package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.Project;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.model.issue.Issue;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectTransformer {

    public static MinerProject toGitMinerProject(Project source,
                                                 List<MinerCommit> bitbucketCommits,
                                                 List<MinerIssue> bitbucketIssues) {
        MinerProject target = new MinerProject();
        // Usa el UUID del proyecto como ID
        target.setName(source.getName());

        if (source.getLinks() != null && source.getLinks().getHtml() != null) {
            target.setWebUrl(source.getLinks().getHtml().getHref());
        }

        if (bitbucketCommits != null) {
            target.setCommits(bitbucketCommits);  // ya están en formato MinerCommit
        }

        if (bitbucketIssues != null) {
            target.setIssues(bitbucketIssues);  // ya están en formato MinerIssue
        }

        return target;
    }
}

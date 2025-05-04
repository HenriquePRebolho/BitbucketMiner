package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.Project;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;

public class ProjectTransformer {

    public static MinerProject toGitMinerProject(Project source) {
        MinerProject target = new MinerProject();

        target.setId(source.getUuid());
        target.setName(source.getName());

        if (source.getLinks() != null && source.getLinks().getHtml() != null) {
            target.setWebUrl(source.getLinks().getHtml().getHref());
        }

        return target;
    }
}

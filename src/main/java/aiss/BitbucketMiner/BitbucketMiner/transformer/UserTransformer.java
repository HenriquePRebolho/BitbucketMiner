package aiss.BitbucketMiner.BitbucketMiner.transformer;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.User;
import aiss.BitbucketMiner.BitbucketMiner.model.Users;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerUser;
import aiss.BitbucketMiner.BitbucketMiner.model.issue.Reporter;

public class UserTransformer {

    public static MinerUser toGitMinerUser(Users bitbucketUser) {
        if (bitbucketUser == null) return null;

        MinerUser result = new MinerUser();

        // ID del usuario: usamos el UUID directamente
       // result.setId(bitbucketUser.getUuid());

        // Username
        result.setUsername(bitbucketUser.getUsername());

        // Name o display_name (puedes elegir cuál prefieres, aquí se usa nickname)
        result.setName(bitbucketUser.getNickname());

        // Avatar URL
        result.setAvatarUrl(bitbucketUser.getLinks() != null &&
                bitbucketUser.getLinks().getAvatar() != null
                ? bitbucketUser.getLinks().getAvatar().getHref()
                : null);

        // Web URL
        result.setWebUrl(bitbucketUser.getLinks() != null &&
                bitbucketUser.getLinks().getHtml() != null
                ? bitbucketUser.getLinks().getHtml().getHref()
                : null);


        return result;
    }

    public static MinerUser toGitMinerUser(Reporter reporter) {
        if (reporter == null) return null;

        MinerUser result = new MinerUser();
        result.setUsername(reporter.getNickname());
        result.setName(reporter.getDisplayName());
        result.setAvatarUrl(reporter.getLinks() != null && reporter.getLinks().getAvatar() != null
                ? reporter.getLinks().getAvatar().getHref()
                : null);
        result.setWebUrl(reporter.getLinks() != null && reporter.getLinks().getHtml() != null
                ? reporter.getLinks().getHtml().getHref()
                : null);

        return result;
    }
    public static MinerUser toGitMinerUser(User commentUser) {
        if (commentUser == null) return null;

        MinerUser result = new MinerUser();

        result.setUsername(commentUser.getNickname());
        result.setName(commentUser.getDisplayName());

        result.setAvatarUrl(
                commentUser.getLinks() != null && commentUser.getLinks().getAvatar() != null
                        ? commentUser.getLinks().getAvatar().getHref()
                        : null
        );

        result.setWebUrl(
                commentUser.getLinks() != null && commentUser.getLinks().getHtml() != null
                        ? commentUser.getLinks().getHtml().getHref()
                        : null
        );

        return result;
    }



}
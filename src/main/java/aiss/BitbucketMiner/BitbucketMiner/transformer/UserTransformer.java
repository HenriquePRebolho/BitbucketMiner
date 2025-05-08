package aiss.BitbucketMiner.BitbucketMiner.transformer;

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

}
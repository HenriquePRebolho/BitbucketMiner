package aiss.BitbucketMiner.BitbucketMiner.transformer;


import aiss.BitbucketMiner.BitbucketMiner.model.issue.Issue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;

public class IssueTransformer {

    public static MinerIssue toGitMinerIssue(Issue bitbucketIssue) {
        if (bitbucketIssue == null) return null;

        MinerIssue result = new MinerIssue();

        // ID: lo convertimos a String para GitMiner
        result.setId(bitbucketIssue.getId() != null ? String.valueOf(bitbucketIssue.getId()) : null);

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

        // Etiquetas (Bitbucket no devuelve labels directamente, esto queda como lista vacía por ahora)
        result.setLabels(new java.util.ArrayList<>());

        // Votos (si no está presente en el JSON puede ser null, se controla con ternario)
        result.setVotes(bitbucketIssue.getVotes() != null ? bitbucketIssue.getVotes() : 0);

        return result;
    }
}
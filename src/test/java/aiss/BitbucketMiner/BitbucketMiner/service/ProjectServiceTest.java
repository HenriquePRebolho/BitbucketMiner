package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Project;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.transformer.ProjectTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ProjectServiceTest {

    @Value("${bitbucketminer.maxpages}")
    private int maxPages;

    @Value("${bitbucketminer.ncommits}")
    private int nCommits;

    @Autowired
    ProjectService projectService;

    @Autowired
    CommitService commitService;

    @Autowired
    IssueService issueService;

    @Test
    public void testToGitMinerProject() {
        Project project = new Project();
        project.setUuid("proj-1234");
        project.setName("Proyecto de prueba");

        aiss.BitbucketMiner.BitbucketMiner.model.issue.Html__2 html = new aiss.BitbucketMiner.BitbucketMiner.model.issue.Html__2();
        html.setHref("https://bitbucket.org/test/project");

        aiss.BitbucketMiner.BitbucketMiner.model.issue.Links__2 links = new aiss.BitbucketMiner.BitbucketMiner.model.issue.Links__2();
        links.setHtml(html);
        project.setLinks(links);

        MinerProject result = ProjectTransformer.toGitMinerProject(project, List.of(), List.of());

        assertNotNull(result, "El MinerProject no puede ser nulo");
        assertEquals("proj-1234", result.getId());
        assertEquals("Proyecto de prueba", result.getName());
        assertEquals("https://bitbucket.org/test/project", result.getWebUrl());
    }

    @Test
    @DisplayName("Obtener proyectos desde Bitbucket y mostrarlos")
    public void getProjects() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";

        String projectUuid = commitService.getProjectUuidFromRepo(workspace, repoSlug);

        List<MinerCommit> commits = commitService.getCommits(workspace, repoSlug,projectUuid, nCommits, maxPages);
        List<MinerIssue> issues = issueService.getIssues(workspace, repoSlug,projectUuid, nCommits, maxPages);

        List<MinerProject> projects = projectService.getProjects(workspace, commits, issues);

        assertNotNull(projects, "La lista de projects no puede ser nula");
        assertFalse(projects.isEmpty(), "La lista de projects no puede estar vacía");

        projects.forEach(projectService::printProject);
    }

    @Test
    @DisplayName("Enviar proyectos desde Bitbucket a GitMiner")
    public void sendProjectsToGitMiner_test() {
        String workspace = "gentlero";
        String repoSlug = "bitbucket-api";

        String projectUuid = commitService.getProjectUuidFromRepo(workspace, repoSlug);

        List<MinerCommit> commits = commitService.getCommits(workspace, repoSlug, projectUuid, nCommits, maxPages);
        List<MinerIssue> issues = issueService.getIssues(workspace, repoSlug, projectUuid, nCommits, maxPages);

        int enviados = projectService.sendProjectsToGitMiner(workspace, commits, issues);
        assertTrue(enviados > 0, "El número de proyectos enviados debe de ser mayor que 0");
    }


}

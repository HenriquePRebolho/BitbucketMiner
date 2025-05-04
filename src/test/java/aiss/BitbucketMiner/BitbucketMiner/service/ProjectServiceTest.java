package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Project;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.transformer.ProjectTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectServiceTest {

    @Autowired
    ProjectService projectService;

    @Test
    public void testToGitMinerProject() {
        // Simula un Project básico
        Project project = new Project();
        project.setUuid("proj-1234");
        project.setName("Proyecto de prueba");

        // Simula los enlaces HTML del proyecto
        aiss.BitbucketMiner.BitbucketMiner.model.issue.Html__2 html = new aiss.BitbucketMiner.BitbucketMiner.model.issue.Html__2();
        html.setHref("https://bitbucket.org/test/project");

        aiss.BitbucketMiner.BitbucketMiner.model.issue.Links__2 links = new aiss.BitbucketMiner.BitbucketMiner.model.issue.Links__2();
        links.setHtml(html);
        project.setLinks(links);

        MinerProject result = ProjectTransformer.toGitMinerProject(project);

        assertNotNull(result);
        assertEquals("proj-1234", result.getId());
        assertEquals("Proyecto de prueba", result.getName());
        assertEquals("https://bitbucket.org/test/project", result.getWebUrl());
    }

    @Test
    @DisplayName("Obtener proyectos desde Bitbucket y mostrarlos")
    public void getProjects() {
        String workspace = "gentlero";

        List<MinerProject> projects = projectService.getProjects(workspace);

        assertNotNull(projects);
        assertFalse(projects.isEmpty());

        projects.forEach(projectService::printProject);
    }

    @Test
    @DisplayName("Enviar proyectos desde Bitbucket a GitMiner")
    public void sendProjectsToGitMiner_test() {
        String workspace = "gentlero";

        int enviados = projectService.sendProjectsToGitMiner(workspace);
        assertTrue(enviados > 0);
    }
}

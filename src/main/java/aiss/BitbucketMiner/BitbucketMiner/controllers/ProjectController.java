package aiss.BitbucketMiner.BitbucketMiner.controllers;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @GetMapping("/{workspace}/projects")
    public List<MinerProject> getProjects(
            @PathVariable String workspace
    ) {
        return projectService.getProjects(workspace);
    }

    @PostMapping("/{workspace}/projects")
    public String sendProjectsToGitMiner(
            @PathVariable String workspace
    ) {
        int saved = projectService.sendProjectsToGitMiner(workspace);
        return saved + " proyectos enviados a GitMiner correctamente.";
    }
}

package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Commit.BitBucketCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.Project;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerCommit;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerIssue;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerProject;
import aiss.BitbucketMiner.BitbucketMiner.transformer.ProjectTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    RestTemplate restTemplate;

    public List<MinerProject> getProjects(String workspace,
                                          List<MinerCommit> allCommits,
                                          List<MinerIssue> allIssues) {
        List<MinerProject> result = new ArrayList<>();

        String uri = String.format("https://api.bitbucket.org/2.0/workspaces/%s/projects", workspace);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri, HttpMethod.GET, requestEntity, JsonNode.class
            );

            JsonNode valuesNode = response.getBody().get("values");
            ObjectMapper mapper = new ObjectMapper();
            Project[] projects = mapper.treeToValue(valuesNode, Project[].class);

            if (projects != null) {
                for (Project p : projects) {
                    String projectUuid = p.getUuid(); // UUID desde Bitbucket

                    // Filtra solo los commits que tengan ese projectId
                    List<MinerCommit> projectCommits = allCommits.stream()
                            .filter(c -> projectUuid.equals(c.getProjectId()))
                            .map(c -> { c.setId(null); return c; }) // Limpiar ID
                            .toList();

                    // Filtra solo los issues que tengan ese projectId
                    List<MinerIssue> projectIssues = allIssues.stream()
                            .filter(i -> projectUuid.equals(i.getProjectId()))
                            .map(i -> { i.setId(null); return i; }) // Limpiar ID
                            .toList();

                    result.add(ProjectTransformer.toGitMinerProject(p, projectCommits, projectIssues));
                }
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error al parsear JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" Error en la llamada a Bitbucket: " + e.getMessage());
        }

        return result;
    }




    public int sendProjectsToGitMiner(String workspace ,List<MinerCommit> commits, List<MinerIssue> issues) {
        System.out.println("CONSTRUYENDO PROJECTS...");
        List<MinerProject> projects = getProjects(workspace, commits, issues );
        String gitMinerUrl = "http://localhost:8080/gitminer/projects"; // Ajusta si tu endpoint es diferente

        int sent = 0;
        for (MinerProject project : projects) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                System.out.println("ANTES DEL REQUEST");
                HttpEntity<MinerProject> request = new HttpEntity<>(project, headers);

                System.out.println("Enviando proyecto: " + new ObjectMapper().writeValueAsString(project));


                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Proyecto enviado correctamente: " + project.getName());
                    sent++;
                } else {
                    System.err.println("Error al enviar proyecto " + project.getName() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println("Error al enviar proyecto " + project.getName() + ": " + e.getMessage());
            }
        }

        return sent;
    }

    public void printProject(MinerProject project) {
        if (project != null) {
            System.out.println(" PROJECT [" );
            System.out.println("    - Name: " + project.getName());
            System.out.println("    - Web URL: " + project.getWebUrl());
        } else {
            System.out.println("PROJECT [NULL]");
        }
    }
}

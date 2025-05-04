package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Project;
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

    public List<MinerProject> getProjects(String workspace) {
        List<MinerProject> result = new ArrayList<>();

        String uri = String.format("https://api.bitbucket.org/2.0/workspaces/%s/projects", workspace);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    requestEntity,
                    JsonNode.class
            );

            JsonNode valuesNode = response.getBody().get("values");
            ObjectMapper mapper = new ObjectMapper();
            Project[] projects = mapper.treeToValue(valuesNode, Project[].class);

            if (projects != null) {
                for (Project p : projects) {
                    result.add(ProjectTransformer.toGitMinerProject(p));
                }
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error al parsear JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("⚠ Error en la llamada a Bitbucket: " + e.getMessage());
        }

        return result;
    }

    public int sendProjectsToGitMiner(String workspace) {
        List<MinerProject> projects = getProjects(workspace);
        String gitMinerUrl = "http://localhost:8080/projects"; // Ajusta si tu endpoint es diferente

        int sent = 0;
        for (MinerProject project : projects) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MinerProject> request = new HttpEntity<>(project, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("✔ Proyecto enviado correctamente: " + project.getId());
                    sent++;
                } else {
                    System.err.println("✖ Error al enviar proyecto " + project.getId() + ": " + response.getStatusCode());
                }

            } catch (Exception e) {
                System.err.println("⚠ Error al enviar proyecto " + project.getId() + ": " + e.getMessage());
            }
        }

        return sent;
    }

    public void printProject(MinerProject project) {
        if (project != null) {
            System.out.println(" PROJECT [" + project.getId() + "]");
            System.out.println("    - Name: " + project.getName());
            System.out.println("    - Web URL: " + project.getWebUrl());
        } else {
            System.out.println("PROJECT [NULL]");
        }
    }
}

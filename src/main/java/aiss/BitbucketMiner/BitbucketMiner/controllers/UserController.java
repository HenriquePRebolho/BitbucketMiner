package aiss.BitbucketMiner.BitbucketMiner.controllers;

import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerUser;
import aiss.BitbucketMiner.BitbucketMiner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bitbucket/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users/{username}")
    public MinerUser getUser(
            @PathVariable String username,
            @RequestParam String token
    ) {
        return userService.getAuthenticatedUser(username, token);
    }

    @PostMapping("/users/{username}")
    public String sendUserToGitMiner(
            @PathVariable String username,
            @RequestParam String token
    ) {
        boolean sent = userService.sendUserToGitMiner(username, token);
        return sent ? "Usuario enviado correctamente a GitMiner." : "Error al enviar el usuario.";
    }
}

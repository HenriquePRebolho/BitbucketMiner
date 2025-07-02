package aiss.BitbucketMiner.BitbucketMiner.service;

import aiss.BitbucketMiner.BitbucketMiner.model.Users;
import aiss.BitbucketMiner.BitbucketMiner.model.gitminer.MinerUser;
import aiss.BitbucketMiner.BitbucketMiner.transformer.UserTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    private final String USERNAME = "enriquemagu2004"; // OJOO Aqui hay que crear un usuario en bitbucket altassian
                                                    // y crear un token que se asocia a tu usuario
    private final String APP_PASSWORD = "ATBBsb7kvW7TbUH6EqBHXP9TFnh70FB100CA"; // aqui ponemos el token , en postman (BASIC AUTH)

    @Test
    public void testToGitMinerUser() {
        // Simulación de un usuario básico
        Users bitbucketUser = new Users();
        bitbucketUser.setUuid("{abc-123}");
        bitbucketUser.setUsername("testuser");
        bitbucketUser.setDisplayName("Test User");

        MinerUser result = UserTransformer.toGitMinerUser(bitbucketUser);

        assertNotNull(result, "El MinerUser no puede ser nula");
        assertEquals("{abc-123}", result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getName());
    }

    @Test
    @DisplayName("Obtener usuario autenticado desde Bitbucket")
    public void getAuthenticatedUserTest() {
        MinerUser user = userService.getAuthenticatedUser(USERNAME, APP_PASSWORD);

        assertNotNull(user, "El usuario autenticado no puede ser nulo");
        assertNotNull(user.getId(), "El id del usuario autenticado no puede ser nulo");
        assertNotNull(user.getUsername(), "El username del usuario autenticado no puede ser nulo");

        userService.printUser(user);
    }

    @Test
    @DisplayName("Enviar usuario a GitMiner")
    public void sendUserToGitMinerTest() {
        MinerUser user = userService.getAuthenticatedUser(USERNAME, APP_PASSWORD);
        assertNotNull(user, "El MinerUser no puede ser nulo");
// para ver el usuario que estamos enviando
        System.out.println("Usuario a enviar:");
        System.out.println(user);

        boolean enviado = userService.sendUserToGitMiner(user);
        assertTrue(enviado);
    }
}

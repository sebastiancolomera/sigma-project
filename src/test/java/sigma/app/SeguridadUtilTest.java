package sigma.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeguridadUtilTest {

    @Test
    @DisplayName("hashPassword genera un hash distinto para la misma contraseña (por el salt aleatorio)")
    void testHashPasswordGeneraHashDistintoParaMismaPass() {
        String password = "miPassword123";

        String hash1 = SeguridadUtil.hashPassword(password);
        String hash2 = SeguridadUtil.hashPassword(password);

        assertNotEquals(hash1, hash2);
        assertTrue(hash1.contains(":"));
        assertTrue(hash2.contains(":"));
    }

    @Test
    @DisplayName("verificarPassword retorna true con la contraseña correcta")
    void testVerificarPasswordCorrecto() {
        String password = "miPassword123";
        String hash = SeguridadUtil.hashPassword(password);

        assertTrue(SeguridadUtil.verificarPassword(password, hash));
    }

    @Test
    @DisplayName("verificarPassword retorna false con la contraseña incorrecta")
    void testVerificarPasswordIncorrecto() {
        String password = "miPassword123";
        String hash = SeguridadUtil.hashPassword(password);

        assertFalse(SeguridadUtil.verificarPassword("otraPassword", hash));
    }

    @Test
    @DisplayName("verificarPassword retorna false con hash nulo")
    void testVerificarPasswordHashNulo() {
        assertFalse(SeguridadUtil.verificarPassword("password", null));
    }

    @Test
    @DisplayName("verificarPassword retorna false con hash en formato incorrecto")
    void testVerificarPasswordHashIncorrecto() {
        assertFalse(SeguridadUtil.verificarPassword("password", "formato-incorrecto"));
    }

    @Test
    @DisplayName("hashPassword lanza excepción con contraseña nula")
    void testHashPasswordNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            SeguridadUtil.hashPassword(null);
        });
    }

    @Test
    @DisplayName("hashPassword lanza excepción con contraseña vacía")
    void testHashPasswordVacia() {
        assertThrows(IllegalArgumentException.class, () -> {
            SeguridadUtil.hashPassword("");
        });
    }
}
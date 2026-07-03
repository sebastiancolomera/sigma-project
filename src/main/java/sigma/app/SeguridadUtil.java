package sigma.app;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class SeguridadUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int LARGO_LLAVE = 256;
    private static final int LARGO_SALT = 16;

    private SeguridadUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[LARGO_SALT];
            random.nextBytes(salt);

            byte[] hash = pbkdf2(password.toCharArray(), salt);

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("No se pudo generar el hash de contraseña", e);
        }
    }

    public static boolean verificarPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        try {
            String[] partes = storedHash.split(":");
            if (partes.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);

            byte[] hashCalculado = pbkdf2(password.toCharArray(), salt);

            return java.security.MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERACIONES, LARGO_LLAVE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
        return factory.generateSecret(spec).getEncoded();
    }
}

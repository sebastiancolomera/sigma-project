package sigma.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SeguridadUtil {

    private SeguridadUtil() {}

    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_LLAVE_BITS = 256;
    private static final int LONGITUD_SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String hashPassword(String contrasena) {
        if (contrasena == null) {
            throw new IllegalArgumentException("Ingrese una contraseña");
        }
        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(contrasena.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificarPassword(String contrasena, String hashAlmacenado) {
        if (contrasena == null || hashAlmacenado == null) return false;

        String[] partes = hashAlmacenado.split(":");
        if (partes.length != 2) return false;

        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
            byte[] hashCalculado = pbkdf2(contrasena.toCharArray(), salt);
            return MessageDigest.isEqual(hashEsperado, hashCalculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] contrasena, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena, salt, ITERACIONES, LONGITUD_LLAVE_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error al calcular el hash de la contraseña", e);
        }
    }
}
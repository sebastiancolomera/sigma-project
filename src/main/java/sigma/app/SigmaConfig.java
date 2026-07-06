package sigma.app;

public final class SigmaConfig {
    private SigmaConfig() {}

    public static final String RUTA_USUARIOS = "data/usuarios.json";
    public static final String RUTA_METAS = "data/metas.json";
    public static final String ADMIN_NOMBRE = "admin";

    private static final String ADMIN_PASSWORD_FALLBACK_DEV = "changeit-dev-only";

    public static final String ADMIN_PASSWORD = obtenerPasswordAdmin();

    private static String obtenerPasswordAdmin() {
        String desdeEntorno = System.getenv("SIGMA_ADMIN_PASSWORD");
        if (desdeEntorno != null && !desdeEntorno.isBlank()) {
            return desdeEntorno;
        }
        return ADMIN_PASSWORD_FALLBACK_DEV;
    }
}
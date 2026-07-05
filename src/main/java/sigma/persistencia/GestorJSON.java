package sigma.persistencia;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sigma.modelo.Meta;
import sigma.modelo.Usuario;
import sigma.modelo.EstadoTarea;
import sigma.modelo.EstadoEntrega;
import sigma.modelo.RolUsuario;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestorJSON {

    private final Gson gson;

    public GestorJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        builder.registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, type, ctx) ->
                        new JsonPrimitive(src.toString()));

        builder.registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                        LocalDate.parse(json.getAsString()));

        builder.registerTypeAdapter(RolUsuario.class,
                (JsonDeserializer<RolUsuario>) (json, type, ctx) -> {
                    String val = json.getAsString().toUpperCase().trim();
                    return RolUsuario.valueOf(val);
                });

        builder.registerTypeAdapter(EstadoTarea.class,
                (JsonDeserializer<EstadoTarea>) (json, type, ctx) -> {
                    String val = json.getAsString().toUpperCase().trim();
                    return EstadoTarea.valueOf(val);
                });

        builder.registerTypeAdapter(EstadoTarea.class,
                (JsonDeserializer<EstadoTarea>) (json, type, ctx) -> {
                    String val = json.getAsString().toUpperCase().trim();
                    switch (val) {
                        case "POSTERGADA":
                            return EstadoTarea.PENDIENTE;
                        case "FUERA_DE_PLAZO":
                            return EstadoTarea.EN_PROCESO;
                        default:
                            return EstadoTarea.valueOf(val);
                    }
                });

        builder.registerTypeAdapter(EstadoEntrega.class,
                (JsonDeserializer<EstadoEntrega>) (json, type, ctx) -> {
                    String val = json.getAsString().toUpperCase().trim();
                    return EstadoEntrega.valueOf(val);
                });
        this.gson = builder.create();
    }

    public boolean guardarUsuarios(ArrayList<Usuario> usuarios, String rutaArchivo) {
        try {
            crearDirectorioSiNoExiste(rutaArchivo);
            try (Writer writer = new FileWriter(rutaArchivo)) {
                gson.toJson(usuarios, writer);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Usuario> cargarUsuarios(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("Archivo de usuarios no encontrado, se inicia con lista vacía.");
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(archivo)) {
            Type listType = new TypeToken<ArrayList<Usuario>>() {}.getType();
            ArrayList<Usuario> resultado = gson.fromJson(reader, listType);
            return resultado != null ? resultado : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean guardarMetas(ArrayList<Meta> metas, String rutaArchivo) {
        try {
            crearDirectorioSiNoExiste(rutaArchivo);
            try (Writer writer = new FileWriter(rutaArchivo)) {
                gson.toJson(metas, writer);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[GestorJSON] Error al guardar metas: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Meta> cargarMetas(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("Archivo de metas no encontrado, se inicia con lista vacía.");
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(archivo)) {
            Type listType = new TypeToken<ArrayList<Meta>>() {}.getType();
            ArrayList<Meta> resultado = gson.fromJson(reader, listType);
            return resultado != null ? resultado : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error al cargar metas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void crearDirectorioSiNoExiste(String rutaArchivo) {
        File dir = new File(rutaArchivo).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
    }
}
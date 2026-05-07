package persistencia;

import modelo.Meta;
import modelo.Usuario;
import modelo.Tarea;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestorJSON {

    private Gson gson;

    public GestorJSON() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(date.toString());
                    }
                })
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDate.parse(json.getAsString());
                    }
                })
                .setPrettyPrinting()
                .create();
    }

    public void guardarMetas(ArrayList<Meta> metas, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gson.toJson(metas, writer);
            System.out.println("Las Metas y Tareas guardadas correctamente en " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo de metas: " + e.getMessage());
        }
    }

    public ArrayList<Meta> cargarMetas(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Type tipoListaMetas = new TypeToken<ArrayList<Meta>>() {}.getType();
            ArrayList<Meta> metasCargadas = gson.fromJson(reader, tipoListaMetas);

            if (metasCargadas == null) {
                return new ArrayList<>();
            }
            return metasCargadas;

        } catch (IOException e) {
            System.out.println("[Aviso] No se encontró el archivo de metas o está vacío. Se creará uno nuevo al guardar.");
            return new ArrayList<>();
        }
    }
}

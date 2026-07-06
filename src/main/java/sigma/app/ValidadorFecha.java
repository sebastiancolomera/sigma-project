package sigma.app;

import java.time.LocalDate;
import java.time.DateTimeException;

public final class ValidadorFecha {

    private ValidadorFecha() {}

    public static boolean esFechaValida(int dia, int mes, int anio) {
        try {
            LocalDate.of(anio, mes, dia);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public static boolean esFechaNoAnteriorAHoy(LocalDate fecha) {
        return !fecha.isBefore(LocalDate.now());
    }
}
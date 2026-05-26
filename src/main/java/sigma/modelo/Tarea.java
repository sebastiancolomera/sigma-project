package sigma.modelo;

import java.time.LocalDate;

public class Tarea {
    private String titulo;
    private String descripcion;
    private Usuario asignado;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;
    private String estado;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, Usuario asignado, LocalDate fechaInicio, LocalDate fechaTermino, String estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.asignado = asignado;
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getAsignado() {
        return asignado;
    }

    public void setAsignado(Usuario asistente) {
        this.asignado = asistente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(LocalDate fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
package sigma.modelo;

import java.time.LocalDate;

public class Tarea {
    private String titulo;
    private String descripcion;
    private Usuario asignado;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;
    private EstadoTarea estado;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, Usuario asignado, LocalDate fechaInicio, LocalDate fechaTermino, EstadoTarea estado) {
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

    public void setAsignado(Usuario asignado) {
        this.asignado = asignado;
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

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }
}
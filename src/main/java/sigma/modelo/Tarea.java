package sigma.modelo;

import java.time.LocalDate;

public class Tarea {
    private String titulo;
    private String descripcion;
    private Usuario asignado;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;
    private EstadoTarea estado;
    private EstadoEntrega estadoEntrega;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, Usuario asignado, LocalDate fechaInicio, LocalDate fechaTermino, EstadoTarea estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.asignado = asignado;
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.estado = estado;
        this.estadoEntrega = calcularEstadoEntregaInicial(fechaInicio);
    }

    private static EstadoEntrega calcularEstadoEntregaInicial(LocalDate fechaInicio) {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && fechaInicio.isAfter(hoy)) {
            return EstadoEntrega.POSTERGADA;
        }
        return EstadoEntrega.EN_PLAZO;
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

    public EstadoEntrega getEstadoEntrega() {
        return estadoEntrega;
    }

    public void recalcularEstadoEntregaPorFecha(LocalDate hoy) {
        if (this.estado == EstadoTarea.COMPLETADA) {
            return;
        }
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) {
            this.estadoEntrega = EstadoEntrega.POSTERGADA;
        } else if (fechaTermino != null && hoy.isAfter(fechaTermino)) {
            this.estadoEntrega = EstadoEntrega.FUERA_DE_PLAZO;
        } else {
            this.estadoEntrega = EstadoEntrega.EN_PLAZO;
        }
    }

    public void marcarEntregada(LocalDate hoy) {
        if (fechaTermino != null && hoy.isAfter(fechaTermino)) {
            this.estadoEntrega = EstadoEntrega.ENTREGADA_FUERA_DE_PLAZO;
        } else {
            this.estadoEntrega = EstadoEntrega.ENTREGADA;
        }
    }
}
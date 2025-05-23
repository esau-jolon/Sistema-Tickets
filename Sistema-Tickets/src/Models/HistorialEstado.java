/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
import java.sql.Timestamp;

public class HistorialEstado {

    private int id;
    private int ticketId;
    private int estadoAnterior;
    private int nuevoEstado;
    private String comentario;
    private Timestamp fechaCambio;
    private int cambiadoPor;

    private String nombreEstadoAnterior;
    private String nombreNuevoEstado;
    private String nombreCambiadoPor;

    public HistorialEstado() {
    }

    public HistorialEstado(int id, int ticketId, int estadoAnterior, int nuevoEstado, String comentario,
            Timestamp fechaCambio, int cambiadoPor, String nombreEstadoAnterior,
            String nombreNuevoEstado, String nombreCambiadoPor) {
        this.id = id;
        this.ticketId = ticketId;
        this.estadoAnterior = estadoAnterior;
        this.nuevoEstado = nuevoEstado;
        this.comentario = comentario;
        this.fechaCambio = fechaCambio;
        this.cambiadoPor = cambiadoPor;
        this.nombreEstadoAnterior = nombreEstadoAnterior;
        this.nombreNuevoEstado = nombreNuevoEstado;
        this.nombreCambiadoPor = nombreCambiadoPor;
    }

    public String getNombreEstadoAnterior() {
        return nombreEstadoAnterior;
    }

    public void setNombreEstadoAnterior(String nombreEstadoAnterior) {
        this.nombreEstadoAnterior = nombreEstadoAnterior;
    }

    public String getNombreNuevoEstado() {
        return nombreNuevoEstado;
    }

    public void setNombreNuevoEstado(String nombreNuevoEstado) {
        this.nombreNuevoEstado = nombreNuevoEstado;
    }

    public String getNombreCambiadoPor() {
        return nombreCambiadoPor;
    }

    public void setNombreCambiadoPor(String nombreCambiadoPor) {
        this.nombreCambiadoPor = nombreCambiadoPor;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(int estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public int getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(int nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public int getCambiadoPor() {
        return cambiadoPor;
    }

    public void setCambiadoPor(int cambiadoPor) {
        this.cambiadoPor = cambiadoPor;
    }
}

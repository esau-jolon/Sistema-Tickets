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
import java.time.LocalDateTime;

public class Adjunto {

    private int id;
    private int ticketId;
    private String nombreArchivo;
    private String tipoMime;
    private Timestamp fechaSubida;
    private byte[] archivo;

    // Constructor
    public Adjunto(int ticketId, String nombreArchivo, String tipoMime, byte[] archivo) {
        this.ticketId = ticketId;
        this.nombreArchivo = nombreArchivo;
        this.tipoMime = tipoMime;
        this.archivo = archivo;
        this.fechaSubida = new Timestamp(System.currentTimeMillis());
    }

    public Adjunto(int id, String nombreArchivo, Timestamp fechaSubida) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.fechaSubida = fechaSubida;
    }

    public Adjunto() {
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Timestamp getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Timestamp fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getFechaSubidaFormatted() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaSubida);
    }
}

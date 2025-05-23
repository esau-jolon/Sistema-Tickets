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

public class NotaTicket {

    private int id;
    private int ticketId;
    private String descripcion;
    private byte[] archivo;
    private String tipoMime;
    private Timestamp fecha;
    private int autorId;

    private String nombreArchivo;
    private String nombreAutor;

    public NotaTicket() {
    }

    public NotaTicket(int ticketId, String descripcion, byte[] archivo, String tipoMime, Timestamp fecha, int autorId) {
        this.ticketId = ticketId;
        this.descripcion = descripcion;
        this.archivo = archivo;
        this.tipoMime = tipoMime;
        this.fecha = fecha;
        this.autorId = autorId;
    }

    public NotaTicket(int id, int ticketId, String descripcion, byte[] archivo, String tipoMime, Timestamp fecha, int autorId) {
        this.id = id;
        this.ticketId = ticketId;
        this.descripcion = descripcion;
        this.archivo = archivo;
        this.tipoMime = tipoMime;
        this.fecha = fecha;
        this.autorId = autorId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public int getAutorId() {
        return autorId;
    }

    public void setAutorId(int autorId) {
        this.autorId = autorId;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public class Transicion {

    private int id;
    private int estado_origen_id;
    private int estado_destino_id;
    private String nombreEstadoDestino;

    public Transicion() {
    }

    public String getNombreEstadoDestino() {
        return nombreEstadoDestino;
    }

    public Transicion(int id, String nombreEstadoDestino) {
        this.id = id;
        this.nombreEstadoDestino = nombreEstadoDestino;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstado_origen_id() {
        return estado_origen_id;
    }

    public void setEstado_origen_id(int estado_origen_id) {
        this.estado_origen_id = estado_origen_id;
    }

    public int getEstado_destino_id() {
        return estado_destino_id;
    }

    public void setEstado_destino_id(int estado_destino_id) {
        this.estado_destino_id = estado_destino_id;
    }

}

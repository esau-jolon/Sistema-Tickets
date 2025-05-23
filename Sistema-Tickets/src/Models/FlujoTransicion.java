/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public class FlujoTransicion {

    private int id;
    private int flujo_trabajo_id;
    private int estado_Origen_id;
    private int estado_destino_id;
    private String estadoOrigen;
    private String estadoDestino;

    public String getEstadoOrigen() {
        return estadoOrigen;
    }

    public void setEstadoOrigen(String estadoOrigen) {
        this.estadoOrigen = estadoOrigen;
    }

    public String getEstadoDestino() {
        return estadoDestino;
    }

    public void setEstadoDestino(String estadoDestino) {
        this.estadoDestino = estadoDestino;
    }

    public FlujoTransicion() {
    }

    public FlujoTransicion(int id, int flujo_trabajo_id, int estado_Origen_id, int estado_destino_id) {
        this.id = id;
        this.flujo_trabajo_id = flujo_trabajo_id;
        this.estado_Origen_id = estado_Origen_id;
        this.estado_destino_id = estado_destino_id;
    }

    public FlujoTransicion(int id, String estadoOrigen, String estadoDestino) {
        this.id = id;
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlujo_trabajo_id() {
        return flujo_trabajo_id;
    }

    public void setFlujo_trabajo_id(int flujo_trabajo_id) {
        this.flujo_trabajo_id = flujo_trabajo_id;
    }

    public int getEstado_Origen_id() {
        return estado_Origen_id;
    }

    public void setEstado_Origen_id(int estado_Origen_id) {
        this.estado_Origen_id = estado_Origen_id;
    }

    public int getEstado_destino_id() {
        return estado_destino_id;
    }

    public void setEstado_destino_id(int estado_destino_id) {
        this.estado_destino_id = estado_destino_id;
    }

}

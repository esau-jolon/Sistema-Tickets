/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public class FlujoEstado {

    private int id;
    private int flujo_trabajo_id;
    private int estado_id;
    private String nombreEstado;

    public FlujoEstado() {
    }

    public FlujoEstado(int id, int flujo_trabajo_id, int estado_id) {
        this.id = id;
        this.flujo_trabajo_id = flujo_trabajo_id;
        this.estado_id = estado_id;
    }

    public FlujoEstado(int id, String nombreEstado) {
        this.id = id;
        this.nombreEstado = nombreEstado;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
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

    public int getEstado_id() {
        return estado_id;
    }

    public void setEstado_id(int estado_id) {
        this.estado_id = estado_id;
    }

}

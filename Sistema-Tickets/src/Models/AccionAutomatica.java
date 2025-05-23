/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public class AccionAutomatica {
    private int id;
    private int flujo_id;
    private int estado_id;
    private String tipo_accion;
    private String parametros;
    private boolean activo;

    public AccionAutomatica() {
    }

    public AccionAutomatica(int id, int flujo_id, int estado_id, String tipo_accion, String parametros, boolean activo) {
        this.id = id;
        this.flujo_id = flujo_id;
        this.estado_id = estado_id;
        this.tipo_accion = tipo_accion;
        this.parametros = parametros;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlujo_id() {
        return flujo_id;
    }

    public void setFlujo_id(int flujo_id) {
        this.flujo_id = flujo_id;
    }

    public int getEstado_id() {
        return estado_id;
    }

    public void setEstado_id(int estado_id) {
        this.estado_id = estado_id;
    }

    public String getTipo_accion() {
        return tipo_accion;
    }

    public void setTipo_accion(String tipo_accion) {
        this.tipo_accion = tipo_accion;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author esauj
 */
public class Permiso {

    private int id;
    private String nombre;
    private String descripcion;

    private BooleanProperty asignado = new SimpleBooleanProperty(false);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BooleanProperty asignadoProperty() {
        return asignado;
    }

    public boolean isAsignado() {
        return asignado.get();
    }

    public void setAsignado(boolean value) {
        asignado.set(value);
    }

}

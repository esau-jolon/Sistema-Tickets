/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 *
 * @author esauj
 */
import java.sql.Timestamp;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String titulo;
    private String descripcion;
    private Timestamp fechaCreacion;
    private Integer id_tecnico_asignado;
    private Integer id_usuario;
    private Integer id_departamento;
    private Integer id_prioridad;
    private Integer id_estado;

    private String tecnico;
    private String estado;
    private String prioridad;
    private String departamento;

    public Ticket() {
    }

    public Ticket(int id, String titulo, String descripcion, Timestamp fechaCreacion,
            Integer id_tecnico_asignado, Integer id_usuario, Integer id_departamento,
            Integer id_prioridad, Integer id_estado, String tecnico, String estado,
            String prioridad, String departamento) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.id_tecnico_asignado = id_tecnico_asignado;
        this.id_usuario = id_usuario;
        this.id_departamento = id_departamento;
        this.id_prioridad = id_prioridad;
        this.id_estado = id_estado;
        this.tecnico = tecnico;
        this.estado = estado;
        this.prioridad = prioridad;
        this.departamento = departamento;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getId_tecnico_asignado() {
        return id_tecnico_asignado;
    }

    public void setId_tecnico_asignado(Integer id_tecnico_asignado) {
        this.id_tecnico_asignado = id_tecnico_asignado;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_departamento() {
        return id_departamento;
    }

    public void setId_departamento(Integer id_departamento) {
        this.id_departamento = id_departamento;
    }

    public Integer getId_prioridad() {
        return id_prioridad;
    }

    public void setId_prioridad(Integer id_prioridad) {
        this.id_prioridad = id_prioridad;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }
}

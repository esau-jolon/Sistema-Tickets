/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import com.sun.jdi.connect.spi.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author esauj
 */
public class Tecnico extends Persona {

    private int IdDepartamento;
    private String nombreDepartamento;

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public int getIdDepartamento() {
        return IdDepartamento;
    }

    public void setIdDepartamento(int IdDepartamento) {
        this.IdDepartamento = IdDepartamento;
    }

    @Override
    public void mostrarPerfil() {
        System.out.println("Perfil Técnico: " + getNombre() + ", Departamento: " + IdDepartamento);
    }

    @Override
    public void agregarUsuario() {
        System.out.println("Técnico agregado: " + getNombre());
    }

    public void atenderTickets() {

    }

    public void cambiarEstado() {

    }

    public void agregarNota() {

    }

    public void reAsignarTicket() {

    }

    /*
    @Override
    public void guardar(java.sql.Connection conexion) throws SQLException {
        String sql = """
        INSERT INTO persona 
        (nombre, correo, id_rol, id_empresa, id_departamento, contrasenia,"user") 
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setString(1, this.getNombre());
            stmt.setString(2, this.getCorreo());
            stmt.setInt(3, this.getIdRol());
            stmt.setInt(4, this.getIdEmpresa());
            stmt.setInt(5, this.getIdDepartamento());
            stmt.setString(6, this.getContraseña());
            stmt.setString(7, this.getUser());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar técnico: " + e.getMessage());
            throw e;
        }
    }*/
    @Override
    public void guardar(java.sql.Connection conexion) throws SQLException {
        if (getId() == 0) {
            // INSERT
            String sql = """
        INSERT INTO persona 
        (nombre, correo, id_rol, id_empresa, id_departamento, contrasenia, "user") 
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, this.getNombre());
                stmt.setString(2, this.getCorreo());
                stmt.setInt(3, this.getIdRol());
                stmt.setInt(4, this.getIdEmpresa());
                stmt.setInt(5, this.getIdDepartamento());
                stmt.setString(6, this.getContraseña());
                stmt.setString(7, this.getUser());
                stmt.executeUpdate();
            }

        } else {
            // UPDATE
            String sql = """
        UPDATE persona SET 
            nombre = ?, correo = ?, id_rol = ?, id_empresa = ?, 
            id_departamento = ?, contrasenia = ?, "user" = ?
        WHERE id = ?
        """;

            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, this.getNombre());
                stmt.setString(2, this.getCorreo());
                stmt.setInt(3, this.getIdRol());
                stmt.setInt(4, this.getIdEmpresa());
                stmt.setInt(5, this.getIdDepartamento());
                stmt.setString(6, this.getContraseña());
                stmt.setString(7, this.getUser());
                stmt.setInt(8, this.getId());
                stmt.executeUpdate();
            }
        }
    }

}

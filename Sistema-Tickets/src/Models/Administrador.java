/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author esauj
 */
public class Administrador extends Persona {

    @Override
    public void mostrarPerfil() {
        System.out.println("Administrador: " + getNombre());
    }

    @Override
    public void agregarUsuario() {
        System.out.println("Administrador agregando usuario...");
    }

    public void gestionarUsuarios() {

    }

    public void configurarFlujos() {

    }

    public void agregarDepartamentos() {

    }

    public void asignarUsuarios() {

    }

    public void agregarEmpresa() {

    }

    public void crearRol() {

    }

    public void crearPermisos() {

    }

    /*
    @Override
    public void guardar(java.sql.Connection conexion) throws SQLException {
        String sql = """
        INSERT INTO persona 
        (nombre, correo, id_rol, id_empresa,  contrasenia, "user" ) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setString(1, this.getNombre());
            stmt.setString(2, this.getCorreo());
            stmt.setInt(3, this.getIdRol());
            stmt.setInt(4, this.getIdEmpresa());
            stmt.setString(5, this.getUser());
            stmt.setString(6, this.getContraseña());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar técnico: " + e.getMessage());
            throw e;
        }
    }
     */
    @Override
    public void guardar(java.sql.Connection conexion) throws SQLException {
        if (getId() == 0) {
            String sql = """
        INSERT INTO persona 
        (nombre, correo, id_rol, id_empresa, contrasenia, "user") 
        VALUES (?, ?, ?, ?, ?, ?)
        """;
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, this.getNombre());
                stmt.setString(2, this.getCorreo());
                stmt.setInt(3, this.getIdRol());
                stmt.setInt(4, this.getIdEmpresa());
                stmt.setString(5, this.getContraseña());
                stmt.setString(6, this.getUser());
                stmt.executeUpdate();
            }

        } else {
            String sql = """
        UPDATE persona SET 
            nombre = ?, correo = ?, id_rol = ?, id_empresa = ?, 
            contrasenia = ?, "user" = ?
        WHERE id = ?
        """;
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, this.getNombre());
                stmt.setString(2, this.getCorreo());
                stmt.setInt(3, this.getIdRol());
                stmt.setInt(4, this.getIdEmpresa());
                stmt.setString(5, this.getContraseña());
                stmt.setString(6, this.getUser());
                stmt.setInt(7, this.getId());
                stmt.executeUpdate();
            }
        }
    }
}

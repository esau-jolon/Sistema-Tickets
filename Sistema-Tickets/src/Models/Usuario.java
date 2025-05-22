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
public class Usuario extends Persona {

    @Override
    public void mostrarPerfil() {
        System.out.println("Perfil Usuario: " + getNombre());
    }

    @Override
    public void agregarUsuario() {
        System.out.println("Usuario agregado: " + getNombre());
    }

    public void crearTicket() {
        System.out.println("Ticket creado por: " + getNombre());
    }

    public void consultarTickets() {
        System.out.println("Consultando tickets de: " + getNombre());
    }

  
    @Override
    public void guardar(java.sql.Connection conexion) throws SQLException {
        if (getId() == 0) {
            String sql = """
        INSERT INTO persona 
        (nombre, correo, id_rol, id_empresa, contrasenia, "user", id_departamento) 
        VALUES (?, ?, ?, ?, ?, ?, NULL)
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
            id_departamento = NULL, contrasenia = ?, "user" = ?
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

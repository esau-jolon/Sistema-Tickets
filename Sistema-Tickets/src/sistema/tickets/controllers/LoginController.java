/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
import Models.PersonaSistema;
import Models.Sesion;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import sistema.tickets.PermisosRequeridos;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtContrasenia;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void btnSesionAction(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText().trim();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Usuario y contraseña obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id, \"user\", contrasenia, id_rol, stat FROM persona WHERE \"user\" = ? AND contrasenia = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasenia);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean activo = rs.getBoolean("stat");
                if (!activo) {
                    JOptionPane.showMessageDialog(null, "El usuario no está activo. Solicite activación al administrador.",
                            "Usuario inactivo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Crear el objeto usuario
                PersonaSistema persona = new PersonaSistema();
                persona.setId(rs.getInt("id"));
                persona.setUser(rs.getString("user"));
                persona.setContraseña(rs.getString("contrasenia"));
                persona.setIdRol(rs.getInt("id_rol"));

                // Obtener y guardar en sesión
                List<Permiso> permisos = obtenerPermisosPorRol(conn, persona.getIdRol());
                Sesion.setUsuario(persona);
                Sesion.setPermisos(permisos);

                // Imprimir los permisos en consola
                System.out.println("Permisos del usuario:");
                for (Permiso p : permisos) {
                    System.out.println("- ID: " + p.getId() + ", Nombre: " + p.getNombre());
                }

                // Mostrar la vista principal
                PermisosRequeridos controlador = Navegador.mostrarVistaCentralConControlador("/sistema/tickets/views/Menu.fxml");
                if (controlador != null) {
                    controlador.aplicarPermisos(permisos);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.err.println("Error en el login: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Permiso> obtenerPermisosPorRol(Connection conn, int idRol) throws SQLException {
        List<Permiso> permisos = new ArrayList<>();

        String sqlPermisos = "SELECT p.id, p.nombre FROM permiso p "
                + "JOIN rol_permiso rp ON p.id = rp.id_permiso "
                + "WHERE rp.id_rol = ? AND rp.stat = true";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlPermisos)) {
            pstmt.setInt(1, idRol);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                permisos.add(new Permiso(id, nombre));
            }
        }

        return permisos;
    }

}

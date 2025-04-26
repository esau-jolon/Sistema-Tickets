/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Rol;
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
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javax.swing.JOptionPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class RolesController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView<Rol> tblRoles;

    @FXML
    private TableColumn<Rol, Integer> colId;
    @FXML
    private TableColumn<Rol, String> colNombre;
    @FXML
    private TableColumn<Rol, String> colDescripcion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Cargar los datos
        cargarDatosRoles();
    }

    @FXML
    private void btnPermissionsAction(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddPermissions.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMouseEntered(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        sourceButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-border-color: rgba(255, 255, 255, 0.5); -fx-cursor: hand;");
    }

    @FXML
    private void handleMouseExited(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        sourceButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    public static boolean guardarRol(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;


        } catch (SQLException e) {
            System.err.println("Error al guardar rol: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();

        boolean guardado = RolesController.guardarRol(nombre, descripcion);

        if (guardado) {
            JOptionPane.showMessageDialog(null, "Rol guardado correctamente");
            refrescarTablaRoles();
        } else {
            JOptionPane.showMessageDialog(null, "Error al guardar el rol",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosRoles() {
        ObservableList<Rol> listaRoles = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, descripcion FROM rol";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Rol rol = new Rol();
                rol.setId(id);
                rol.setNombre(nombre);
                rol.setDescripcion(descripcion);

                listaRoles.add(rol);
            }

            // Asignar la lista de roles al TableView
            tblRoles.setItems(listaRoles);

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    public void refrescarTablaRoles() {
        cargarDatosRoles();
    }
}

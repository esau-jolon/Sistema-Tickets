/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Prioridad;
import Models.Rol;
import conexion.ConexionDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class PriorityLevelsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdEmpresa.setVisible(false);
        txtIdPrioridad.setVisible(false);

    }

    @FXML
    private TextField txtIdEmpresa;

    @FXML
    private TextField txtIdPrioridad;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView<Prioridad> tblPrioridades;

    @FXML
    private TableColumn<Prioridad, Integer> colId;
    @FXML
    private TableColumn<Prioridad, String> colNombre;
    @FXML
    private TableColumn<Prioridad, String> colDescripcion;

    public void setIdEmpresa(String idEmpresa) {
        txtIdEmpresa.setText(idEmpresa);
        cargarPrioridades();
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Parameters.fxml");
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        Prioridad seleccionada = tblPrioridades.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar una prioridad de la tabla.");
            alert.showAndWait();

            return;
        }

        txtIdPrioridad.setText(String.valueOf(seleccionada.getId()));
        txtNombre.setText(seleccionada.getNombre());
        txtDescripcion.setText(seleccionada.getDescripcion());
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String idPrioridad = txtIdPrioridad.getText();
        String nombre = txtNombre.getText();
        String descripcion = txtDescripcion.getText();
        String idEmpresa = txtIdEmpresa.getText();

        if (nombre.isEmpty() || idEmpresa.isEmpty() || descripcion.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("El nombre y descripcion son requeridos.");
            alert.showAndWait();

            return;
        }

        String sql;
        boolean esEdicion = idPrioridad != null && !idPrioridad.trim().isEmpty();

        if (esEdicion) {
            // Actualizar
            sql = "UPDATE prioridades SET nombre = ?, descripcion = ?, id_empresa = ? WHERE id = ?";
        } else {
            // Insertar
            sql = "INSERT INTO prioridades (nombre, descripcion, id_empresa) VALUES (?, ?, ?)";
        }

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion);
            pstmt.setInt(3, Integer.parseInt(idEmpresa));

            if (esEdicion) {
                pstmt.setInt(4, Integer.parseInt(idPrioridad));
            }

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println(esEdicion ? "Prioridad actualizada con éxito." : "Prioridad creada con éxito.");
                limpiarCampos();
                cargarPrioridades();
            }

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error al guardar prioridad: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtIdPrioridad.clear();
        txtNombre.clear();
        txtDescripcion.clear();
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

    private void cargarPrioridades() {
        String idEmpresa = txtIdEmpresa.getText(); // Obtener el ID de empresa del campo de texto

        String sql = "SELECT id, nombre, descripcion FROM prioridades WHERE id_empresa = ?";

        ObservableList<Prioridad> prioridades = FXCollections.observableArrayList();

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(idEmpresa));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Prioridad prioridad = new Prioridad(id, nombre, descripcion);
                prioridades.add(prioridad);
            }

            // Configurar las columnas si aún no están configuradas
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            tblPrioridades.setItems(prioridades);

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error al cargar prioridades: " + e.getMessage());
        }
    }

}

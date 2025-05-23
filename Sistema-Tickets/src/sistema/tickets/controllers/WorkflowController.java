/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Estado;
import Models.Flujo;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class WorkflowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setVisible(false);
        cargarFlujos();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colReglas.setCellValueFactory(new PropertyValueFactory<>("reglas"));

    }

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtId;

    @FXML
    private TableView<Flujo> tblFlujos;

    @FXML
    private TableColumn<Flujo, Integer> colId;
    @FXML
    private TableColumn<Flujo, String> colNombre;
    @FXML
    private TableColumn<Flujo, String> colDescripcion;
    @FXML
    private TableColumn<Flujo, String> colReglas;

    @FXML
    private TextArea txaReglas;

    @FXML
    private void btnAgregarFlujoAction(ActionEvent event) {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String reglas = txaReglas.getText().trim();
            String idTexto = txtId.getText().trim();
            Integer id = 0;

            try {
                id = idTexto.isEmpty() ? 0 : Integer.parseInt(idTexto);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "ID inválido", Alert.AlertType.ERROR);
                return;
            }

            if (nombre.isEmpty()) {
                mostrarAlerta("Validación", "El nombre del flujo es obligatorio.", Alert.AlertType.WARNING);
                return;
            }

            if (nombre.length() < 3 || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                mostrarAlerta("Validación", "El nombre debe tener al menos 3 letras y solo contener caracteres alfabéticos.", Alert.AlertType.WARNING);
                return;
            }

            if (nombreFlujoExiste(nombre, id)) {
                mostrarAlerta("Duplicado", "Ya existe un flujo con ese nombre. Por favor elige otro.", Alert.AlertType.WARNING);
                return;
            }

            boolean guardado = guardarFlujo(id, nombre, descripcion, reglas);

            if (guardado) {
                mostrarAlerta("Éxito", "Flujo guardado correctamente.", Alert.AlertType.INFORMATION);
                cargarFlujos();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Hubo un error al guardar el flujo.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            mostrarAlerta("Error crítico", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean guardarFlujo(int id, String nombre, String descripcion, String reglas) {
        String sqlInsert = "INSERT INTO flujo_trabajo (nombre, descripcion, reglas) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE flujo_trabajo SET nombre = ?, descripcion = ?, reglas = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar()) {
            PreparedStatement stmt;

            if (id == 0) {
                stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setString(3, reglas);
            } else {
                stmt = conn.prepareStatement(sqlUpdate);
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setString(3, reglas);
                stmt.setInt(4, id);
            }

            int filasAfectadas = stmt.executeUpdate();

            if (id == 0 && filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        txtId.setText(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al guardar el flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txaReglas.clear();
    }

    private boolean nombreFlujoExiste(String nombre, int idActual) {
        String sql = "SELECT id FROM flujo_trabajo WHERE LOWER(nombre) = LOWER(?) AND id <> ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setInt(2, idActual);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar duplicado de flujo: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void btnEditarFlujoAction(ActionEvent event) {
        Flujo flujoSeleccionado = tblFlujos.getSelectionModel().getSelectedItem();

        if (flujoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un flujo de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        int id = flujoSeleccionado.getId();
        Flujo flujoBD = buscarFlujoPorId(id);

        if (flujoBD != null) {
            txtId.setText(String.valueOf(flujoBD.getId()));
            txtNombre.setText(flujoBD.getNombre());
            txtDescripcion.setText(flujoBD.getDescripcion());
            txaReglas.setText(flujoBD.getReglas());
        } else {
            mostrarAlerta("Error", "No se encontró el flujo de trabajo en la base de datos.", Alert.AlertType.ERROR);
        }
    }

   

    public static Flujo buscarFlujoPorId(int id) {
        Flujo flujo = null;
        String sql = "SELECT id, nombre, descripcion, reglas FROM flujo_trabajo WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                flujo = new Flujo();
                flujo.setId(rs.getInt("id"));
                flujo.setNombre(rs.getString("nombre"));
                flujo.setDescripcion(rs.getString("descripcion"));
                flujo.setReglas(rs.getString("reglas"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar flujo por ID: " + e.getMessage());
        }

        return flujo;
    }

    @FXML
    private void btnConfigurarAction(ActionEvent event) {
        Flujo flujoSeleccionado = tblFlujos.getSelectionModel().getSelectedItem();

        if (flujoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un flujo de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        int id = flujoSeleccionado.getId();
        String nombre = flujoSeleccionado.getNombre();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddWorkflow.fxml"));
            Parent root = loader.load();
            AddWorkflowController controlador = loader.getController();
            controlador.setDatosFlujo(id, nombre);
            Navegador.mostrarVistaCentral(root);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de configuración.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Flujo flujoSeleccionado = tblFlujos.getSelectionModel().getSelectedItem();

        if (flujoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un flujo de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        int id = flujoSeleccionado.getId();

        if (flujoEnUso(id)) {
            mostrarAlerta("Acción no permitida",
                    "Este flujo de trabajo está asociado a tickets activos. Reasigne los tickets antes de eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar este flujo?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (eliminarFlujo(id)) {
                mostrarAlerta("Éxito", "Flujo eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarFlujos();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el flujo.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean flujoEnUso(int flujoId) {
        String sql = """
        SELECT COUNT(*) 
        FROM ticket t
        JOIN estado e ON t.id_estado = e.id
        WHERE t.id_flujo = ? AND e.estado_final = false
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flujoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt(1);
                return total > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar uso del flujo: " + e.getMessage());
        }

        return true;
    }

    private boolean eliminarFlujo(int id) {
        String sql = "DELETE FROM flujo_trabajo WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar flujo: " + e.getMessage());
            return false;
        }
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

    private void cargarFlujos() {
        ObservableList<Flujo> listaFlujos = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, descripcion, reglas FROM flujo_trabajo";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Flujo flujo = new Flujo();
                flujo.setId(rs.getInt("id"));
                flujo.setNombre(rs.getString("nombre"));
                flujo.setDescripcion(rs.getString("descripcion"));
                flujo.setReglas(rs.getString("reglas"));

                listaFlujos.add(flujo);
            }

            tblFlujos.setItems(listaFlujos);

        } catch (SQLException e) {
            System.err.println("Error al cargar flujos de trabajo: " + e.getMessage());
        }
    }

}

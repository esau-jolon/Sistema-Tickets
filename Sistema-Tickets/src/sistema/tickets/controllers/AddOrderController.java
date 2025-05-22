/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Departamento;
import Models.ItemComboBox;
import Models.Transicion;
import conexion.ConexionDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddOrderController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdEstado.setVisible(false);
        txtEstado.setEditable(false);
        cargarEstadosComboBox();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoDestino"));

    }

    @FXML
    private TextField txtEstado;

    @FXML
    private ComboBox<ItemComboBox> cmbEstados;

    @FXML
    private TextField txtIdEstado;

    @FXML
    private TableView<Transicion> tblOrden;

    @FXML
    private TableColumn<Transicion, Integer> colId;
    @FXML
    private TableColumn<Transicion, String> colNombre;

    @FXML
    private void btnCloseAction(ActionEvent event) {

        Navegador.mostrarVistaCentral("/sistema/tickets/views/TicketStatus.fxml");
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Transicion transicionSeleccionada = tblOrden.getSelectionModel().getSelectedItem();

        if (transicionSeleccionada == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona una transición para eliminar.", Alert.AlertType.WARNING);
            return;
        }

   
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmación");
        alertaConfirmacion.setHeaderText("¿Estás seguro de que deseas eliminar esta transición?");

   
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminada = eliminarTransicion(transicionSeleccionada.getId());
            if (eliminada) {
                mostrarAlerta("Éxito", "Transición eliminada correctamente.", Alert.AlertType.INFORMATION);
          
                int idOrigen = Integer.parseInt(txtIdEstado.getText());
                cargarTransiciones(idOrigen);
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la transición.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean eliminarTransicion(int idTransicion) {
        String sql = "DELETE FROM estado_transicion WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTransicion);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al eliminar la transición: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        try {
            String idTexto = txtIdEstado.getText().trim();
            ItemComboBox itemDestino = (ItemComboBox) cmbEstados.getSelectionModel().getSelectedItem();

            if (idTexto.isEmpty() || itemDestino == null) {
                mostrarAlerta("Validación", "Debe seleccionar un estado de destino y asegurarse de tener un estado de origen válido.", Alert.AlertType.WARNING);
                return;
            }

            int estadoOrigenId = Integer.parseInt(idTexto);
            int estadoDestinoId = itemDestino.getId();

            if (estadoOrigenId == estadoDestinoId) {
                mostrarAlerta("Validación", "Un estado no puede transicionar a sí mismo.", Alert.AlertType.WARNING);
                return;
            }

            if (existeTransicion(estadoOrigenId, estadoDestinoId)) {
                mostrarAlerta("Validación", "Ya existe una transición definida entre estos estados.", Alert.AlertType.WARNING);
                return;
            }

            boolean guardado = guardarTransicionEstado(estadoOrigenId, estadoDestinoId);

            if (guardado) {
                mostrarAlerta("Éxito", "Transición de estado guardada correctamente.", Alert.AlertType.INFORMATION);
                cargarTransiciones(Integer.parseInt(txtIdEstado.getText()));

            } else {
                mostrarAlerta("Error", "No se pudo guardar la transición.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de estado de origen no válido.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean guardarTransicionEstado(int estadoOrigenId, int estadoDestinoId) {
        String sqlInsert = "INSERT INTO estado_transicion (estado_origen_id, estado_destino_id) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, estadoOrigenId);
            stmt.setInt(2, estadoDestinoId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al guardar la transición: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeTransicion(int estadoOrigenId, int estadoDestinoId) {
        String sql = "SELECT COUNT(*) FROM estado_transicion WHERE estado_origen_id = ? AND estado_destino_id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estadoOrigenId);
            stmt.setInt(2, estadoDestinoId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al verificar la transición: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return false;
    }

    public void setEstado(int id, String nombre) {
        txtIdEstado.setText(String.valueOf(id));
        txtEstado.setText(nombre);
        cargarTransiciones(Integer.parseInt(txtIdEstado.getText()));
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

    private void cargarEstadosComboBox() {
        String sql = "SELECT id, nombre FROM estado ORDER BY nombre";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            ObservableList<ItemComboBox> listaEstados = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                listaEstados.add(new ItemComboBox(id, nombre));
            }

            cmbEstados.setItems(listaEstados);

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al cargar estados: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void cargarTransiciones(int estadoOrigenId) {
        ObservableList<Transicion> transiciones = FXCollections.observableArrayList();
        String sql = """
        SELECT et.id, e.nombre 
        FROM estado_transicion et 
        JOIN estado e ON et.estado_destino_id = e.id 
        WHERE et.estado_origen_id = ?
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estadoOrigenId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombreEstadoDestino = rs.getString("nombre");
                transiciones.add(new Transicion(id, nombreEstadoDestino));
            }

            tblOrden.setItems(transiciones);

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al cargar transiciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

}

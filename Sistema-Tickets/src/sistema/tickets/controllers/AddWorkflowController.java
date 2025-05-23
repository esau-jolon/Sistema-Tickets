/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Flujo;
import Models.FlujoEstado;
import Models.FlujoTransicion;
import Models.ItemComboBox;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddWorkflowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdFlujo.setVisible(false);
        txtNombreFlujo.setEditable(false);
        cargarEstadosComboBox();
        colIdEstado.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));
        colIdTransicion.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEstadoInicial.setCellValueFactory(new PropertyValueFactory<>("estadoOrigen"));
        colEstadoFinal.setCellValueFactory(new PropertyValueFactory<>("estadoDestino"));

    }

    @FXML
    private TextField txtNombreFlujo;
    @FXML
    private TextField txtIdFlujo;

    @FXML
    private ComboBox<ItemComboBox> cmbEstados;

    @FXML
    private ComboBox<ItemComboBox> cmbInicial;

    @FXML
    private ComboBox<ItemComboBox> cmbFinal;

// TBL DE FLUJO DE FLUJO
    @FXML
    private TableView<FlujoEstado> tblEstados;

    @FXML
    private TableColumn<FlujoEstado, Integer> colIdEstado;
    @FXML
    private TableColumn<FlujoEstado, String> colEstado;

    // TBL DE FLUJO DE TRANSICION
    @FXML
    private TableView<FlujoTransicion> tblTransicion;

    @FXML
    private TableColumn<FlujoTransicion, Integer> colIdTransicion;
    @FXML
    private TableColumn<Flujo, String> colEstadoInicial;
    @FXML
    private TableColumn<Flujo, String> colEstadoFinal;

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Workflow.fxml");
    }

    @FXML
    private void btnAutomaticasAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/Actions.fxml"));
            Parent root = loader.load();

            // Pasar datos al controlador
            ActionsController controller = loader.getController();
            controller.inicializarDatos(txtIdFlujo.getText(), txtNombreFlujo.getText());

            // Mostrar en el centro del BorderPane
            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAgregarFlujoAction(ActionEvent event) {
        try {
            String idFlujoTexto = txtIdFlujo.getText().trim();
            ItemComboBox itemEstado = (ItemComboBox) cmbEstados.getSelectionModel().getSelectedItem();

            if (idFlujoTexto.isEmpty() || itemEstado == null) {
                mostrarAlerta("Validación", "Debe seleccionar un estado y proporcionar un ID de flujo válido.", Alert.AlertType.WARNING);
                return;
            }

            int flujoTrabajoId = Integer.parseInt(idFlujoTexto);
            int estadoId = itemEstado.getId();

            if (existeFlujoEstado(flujoTrabajoId, estadoId)) {
                mostrarAlerta("Validación", "El estado ya está asociado a este flujo.", Alert.AlertType.WARNING);
                return;
            }

            if (guardarFlujoEstado(flujoTrabajoId, estadoId)) {
                mostrarAlerta("Éxito", "Asociación de flujo y estado guardada correctamente.", Alert.AlertType.INFORMATION);
                cargarEstadosPorFlujo(flujoTrabajoId);
                cargarEstadosInicialYFinal();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la asociación de flujo y estado.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de flujo no válido.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean guardarFlujoEstado(int flujoTrabajoId, int estadoId) {
        String sqlInsert = "INSERT INTO flujo_estado (flujo_trabajo_id, estado_id) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, flujoTrabajoId);
            stmt.setInt(2, estadoId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al guardar en flujo_estado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeFlujoEstado(int flujoTrabajoId, int estadoId) {
        String sql = "SELECT COUNT(*) FROM flujo_estado WHERE flujo_trabajo_id = ? AND estado_id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flujoTrabajoId);
            stmt.setInt(2, estadoId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al verificar existencia: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    public void setDatosFlujo(int id, String nombre) {
        txtIdFlujo.setText(String.valueOf(id));
        txtNombreFlujo.setText(nombre);
        cargarEstadosPorFlujo(id);
        cargarEstadosInicialYFinal();
        cargarTransicionesPorFlujo(id);

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

    private void cargarEstadosPorFlujo(int flujoTrabajoId) {
        ObservableList<FlujoEstado> listaFlujos = FXCollections.observableArrayList();

        String sql = """
        SELECT fe.id, e.nombre
        FROM flujo_estado fe
        JOIN estado e ON fe.estado_id = e.id
        WHERE fe.flujo_trabajo_id = ?
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flujoTrabajoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idFlujoEstado = rs.getInt("id");
                String nombreEstado = rs.getString("nombre");

                listaFlujos.add(new FlujoEstado(idFlujoEstado, nombreEstado));
            }

            tblEstados.setItems(listaFlujos);

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al cargar los estados del flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarEstadosInicialYFinal() {
        String idFlujoTexto = txtIdFlujo.getText().trim();
        if (idFlujoTexto.isEmpty()) {
            mostrarAlerta("Validación", "Debe proporcionar un ID de flujo válido.", Alert.AlertType.WARNING);
            return;
        }

        int flujoId = Integer.parseInt(idFlujoTexto);
        String sql = """
        SELECT fe.estado_id, e.nombre 
        FROM flujo_estado fe
        JOIN estado e ON fe.estado_id = e.id
        WHERE fe.flujo_trabajo_id = ?
        ORDER BY e.nombre
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flujoId);
            ResultSet rs = stmt.executeQuery();

            ObservableList<ItemComboBox> listaEstados = FXCollections.observableArrayList();

            while (rs.next()) {
                int estadoId = rs.getInt("estado_id");
                String nombreEstado = rs.getString("nombre");

                listaEstados.add(new ItemComboBox(estadoId, nombreEstado));
            }

            cmbInicial.setItems(listaEstados);
            cmbFinal.setItems(listaEstados);

            cmbInicial.getSelectionModel().clearSelection();
            cmbFinal.getSelectionModel().clearSelection();

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al cargar estados del flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // TRANSICION 
    @FXML
    private void btnAgregarTransicionAction(ActionEvent event) {
        try {
            String idFlujoTexto = txtIdFlujo.getText().trim();
            ItemComboBox estadoInicial = (ItemComboBox) cmbInicial.getSelectionModel().getSelectedItem();
            ItemComboBox estadoFinal = (ItemComboBox) cmbFinal.getSelectionModel().getSelectedItem();

            if (idFlujoTexto.isEmpty() || estadoInicial == null || estadoFinal == null) {
                mostrarAlerta("Validación", "Debe seleccionar un flujo y ambos estados (inicial y final).", Alert.AlertType.WARNING);
                return;
            }

            int idFlujo = Integer.parseInt(idFlujoTexto);
            int idEstadoInicial = estadoInicial.getId();
            int idEstadoFinal = estadoFinal.getId();

            if (idEstadoInicial == idEstadoFinal) {
                mostrarAlerta("Validación", "El estado inicial y final no pueden ser iguales.", Alert.AlertType.WARNING);
                return;
            }

            if (existeTransicionFlujo(idFlujo, idEstadoInicial, idEstadoFinal)) {
                mostrarAlerta("Validación", "Ya existe una transición en este flujo con esos estados.", Alert.AlertType.WARNING);
                return;
            }

            boolean guardado = guardarTransicionFlujo(idFlujo, idEstadoInicial, idEstadoFinal);

            if (guardado) {
                mostrarAlerta("Éxito", "Transición del flujo guardada correctamente.", Alert.AlertType.INFORMATION);

                cargarTransicionesPorFlujo(idFlujo);
            } else {
                mostrarAlerta("Error", "No se pudo guardar la transición del flujo.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de flujo no válido.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean guardarTransicionFlujo(int flujoId, int estadoOrigenId, int estadoDestinoId) {
        String sqlInsert = "INSERT INTO flujo_transicion (flujo_trabajo_id, estado_origen_id, estado_destino_id) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setInt(1, flujoId);
            stmt.setInt(2, estadoOrigenId);
            stmt.setInt(3, estadoDestinoId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al guardar la transición del flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeTransicionFlujo(int flujoId, int estadoOrigenId, int estadoDestinoId) {
        String sql = "SELECT COUNT(*) FROM flujo_transicion WHERE flujo_trabajo_id = ? AND estado_origen_id = ? AND estado_destino_id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flujoId);
            stmt.setInt(2, estadoOrigenId);
            stmt.setInt(3, estadoDestinoId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al verificar la transición del flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return false;
    }

    private void cargarTransicionesPorFlujo(int idFlujo) {
        ObservableList<FlujoTransicion> lista = FXCollections.observableArrayList();

        String sql = """
        SELECT ft.id, eo.nombre AS estado_origen, ed.nombre AS estado_destino
        FROM flujo_transicion ft
        JOIN estado eo ON ft.estado_origen_id = eo.id
        JOIN estado ed ON ft.estado_destino_id = ed.id
        WHERE ft.flujo_trabajo_id = ?
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFlujo);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String origen = rs.getString("estado_origen");
                String destino = rs.getString("estado_destino");

                lista.add(new FlujoTransicion(id, origen, destino));
            }

            tblTransicion.setItems(lista);

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "No se pudieron cargar las transiciones del flujo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

}

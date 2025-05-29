/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.FlujoEstado;
import Models.HistorialEstado;
import Models.ItemComboBox;
import Models.Sesion;
import conexion.ConexionDB;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import sistema.tickets.Navegador;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Stack;
import javafx.scene.control.ButtonType;
import sistema.tickets.EnvioCorreo;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class ResolverController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdTIcket.setVisible(false);
        cargarEstadosComboBox();

    }

    @FXML
    private TableView<HistorialEstado> tblHistorial;

    @FXML
    private TableColumn<HistorialEstado, Integer> colId;
    @FXML
    private TableColumn<HistorialEstado, String> colNuevo;
    @FXML
    private TableColumn<HistorialEstado, String> colAnterior;
    @FXML
    private TableColumn<HistorialEstado, String> colComentario;
    @FXML
    private TableColumn<HistorialEstado, String> colFecha;
    @FXML
    private TableColumn<HistorialEstado, String> colCambiado;

    @FXML
    private TextField txtIdTIcket;
    @FXML
    private TextField txtComentario;

    @FXML
    private ComboBox<ItemComboBox> cmbAnterior;

    @FXML
    private ComboBox<ItemComboBox> cmbNuevo;

    @FXML
    private void btnAgregarTransicionAction(ActionEvent event) {

        if (txtIdTIcket.getText().trim().isEmpty()
                || txtComentario.getText().trim().isEmpty()
                || cmbAnterior.getValue() == null
                || cmbNuevo.getValue() == null) {

            mostrarAlerta("Campos requeridos", "Todos los campos deben estar completos.", Alert.AlertType.WARNING);
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(txtIdTIcket.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de ticket inválido.", Alert.AlertType.ERROR);
            return;
        }

        int estadoAnteriorId = cmbAnterior.getValue().getId();
        int nuevoEstadoId = cmbNuevo.getValue().getId();
        String comentario = txtComentario.getText().trim();
        int cambiadoPor = Sesion.getUsuario().getId();

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);

            // Insertar historial de transición
            String sqlInsert = "INSERT INTO historial_estado (ticket_id, estado_anterior, nuevo_estado, comentario, cambiado_por) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                stmt.setInt(1, ticketId);
                stmt.setInt(2, estadoAnteriorId);
                stmt.setInt(3, nuevoEstadoId);
                stmt.setString(4, comentario);
                stmt.setInt(5, cambiadoPor);
                stmt.executeUpdate();
            }

            // Actualizar estado del ticket
            String sqlUpdate = "UPDATE ticket SET id_estado = ? WHERE id = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setInt(1, nuevoEstadoId);
                stmtUpdate.setInt(2, ticketId);
                stmtUpdate.executeUpdate();
            }

            // Obtener correo del usuario dueño del ticket y nombre del nuevo estado
            String sqlCorreo = """
            SELECT p.correo, e.nombre AS nuevo_estado
            FROM ticket t
            JOIN persona p ON t.id_usuario = p.id
            JOIN estado e ON e.id = ?
            WHERE t.id = ?
        """;
            try (PreparedStatement stmtCorreo = conn.prepareStatement(sqlCorreo)) {
                stmtCorreo.setInt(1, nuevoEstadoId);
                stmtCorreo.setInt(2, ticketId);

                try (ResultSet rs = stmtCorreo.executeQuery()) {
                    if (rs.next()) {
                        String correoDestino = rs.getString("correo");
                        String nombreNuevoEstado = rs.getString("nuevo_estado");

                        // Enviar correo
                        EnvioCorreo.enviarNotificacionCambioEstado(correoDestino, ticketId, nombreNuevoEstado);
                    }
                }
            }

            conn.commit();

            mostrarAlerta("Éxito", "Transición de estado guardada correctamente.", Alert.AlertType.INFORMATION);
            cargarHistorialEstado();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar la transición: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /*
    @FXML
    private void btnEliminarAction(ActionEvent event) {
        HistorialEstado seleccionado = tblHistorial.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor seleccione un registro del historial para eliminar.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar este registro?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String sql = "DELETE FROM historial_estado WHERE id = ?";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, seleccionado.getId());
                stmt.executeUpdate();

                // Recargar historial después de eliminar
                cargarHistorialEstado();

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText(null);
                exito.setContentText("Registro eliminado correctamente.");
                exito.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("No se pudo eliminar el registro.");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }
     */
    @FXML
    private Stack<HistorialEstado> pilaEliminados = new Stack<>();

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        HistorialEstado seleccionado = tblHistorial.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Advertencia");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor seleccione un registro del historial para eliminar.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar este registro?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Guardar objeto sin id en la pila para poder restaurar después
            HistorialEstado historialSinId = new HistorialEstado(
                    0,
                    seleccionado.getTicketId(),
                    seleccionado.getEstadoAnterior(),
                    seleccionado.getNuevoEstado(),
                    seleccionado.getComentario(),
                    seleccionado.getFechaCambio(),
                    seleccionado.getCambiadoPor(),
                    seleccionado.getNombreEstadoAnterior(),
                    seleccionado.getNombreNuevoEstado(),
                    seleccionado.getNombreCambiadoPor()
            );

            pilaEliminados.push(historialSinId);

            String sql = "DELETE FROM historial_estado WHERE id = ?";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, seleccionado.getId());
                stmt.executeUpdate();

                cargarHistorialEstado();

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText(null);
                exito.setContentText("Registro eliminado correctamente.");
                exito.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("No se pudo eliminar el registro.");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }

    @FXML
    private void btnDeshacerAction(ActionEvent event) {
        if (pilaEliminados.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Sin cambios");
            alerta.setHeaderText(null);
            alerta.setContentText("No hay registros eliminados para restaurar.");
            alerta.showAndWait();
            return;
        }

        HistorialEstado ultimoEliminado = pilaEliminados.pop();

        String sql = """
        INSERT INTO historial_estado (ticket_id, estado_anterior, nuevo_estado, comentario, fecha_cambio, cambiado_por)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ultimoEliminado.getTicketId());
            stmt.setInt(2, ultimoEliminado.getEstadoAnterior());
            stmt.setInt(3, ultimoEliminado.getNuevoEstado());
            stmt.setString(4, ultimoEliminado.getComentario());
            stmt.setTimestamp(5, ultimoEliminado.getFechaCambio());
            stmt.setInt(6, ultimoEliminado.getCambiadoPor());

            stmt.executeUpdate();

            cargarHistorialEstado();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Restaurado");
            exito.setHeaderText(null);
            exito.setContentText("Registro restaurado correctamente.");
            exito.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo restaurar el registro.");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/ServicingTicket.fxml");

    }

    public void setIdTicket(int id) {
        txtIdTIcket.setText(String.valueOf(id));
        cargarHistorialEstado();
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

            cmbAnterior.setItems(listaEstados);
            cmbNuevo.setItems(listaEstados);

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

    @FXML
    private void cargarHistorialEstado() {
        ObservableList<HistorialEstado> listaHistorial = FXCollections.observableArrayList();

        String sql = """
        SELECT h.id, h.ticket_id, h.estado_anterior, h.nuevo_estado, h.comentario, h.fecha_cambio, h.cambiado_por,
               ea.nombre AS estado_anterior_nombre,
               en.nombre AS nuevo_estado_nombre,
               p.nombre AS cambiado_por_nombre
        FROM historial_estado h
        JOIN estado ea ON h.estado_anterior = ea.id
        JOIN estado en ON h.nuevo_estado = en.id
        JOIN persona p ON h.cambiado_por = p.id
        WHERE h.ticket_id = ?
        ORDER BY h.fecha_cambio DESC
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(txtIdTIcket.getText()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistorialEstado historial = new HistorialEstado(
                            rs.getInt("id"),
                            rs.getInt("ticket_id"),
                            rs.getInt("estado_anterior"),
                            rs.getInt("nuevo_estado"),
                            rs.getString("comentario"),
                            rs.getTimestamp("fecha_cambio"),
                            rs.getInt("cambiado_por"),
                            rs.getString("estado_anterior_nombre"),
                            rs.getString("nuevo_estado_nombre"),
                            rs.getString("cambiado_por_nombre")
                    );
                    listaHistorial.add(historial);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAnterior.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoAnterior"));
        colNuevo.setCellValueFactory(new PropertyValueFactory<>("nombreNuevoEstado"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        colFecha.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HistorialEstado, String> param) {
                Timestamp ts = param.getValue().getFechaCambio();
                return new SimpleStringProperty(ts.toLocalDateTime().toString());
            }
        });
        colCambiado.setCellValueFactory(new PropertyValueFactory<>("nombreCambiadoPor"));

        tblHistorial.setItems(listaHistorial);
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Estado;
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
import javafx.scene.control.CheckBox;
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
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class TicketStatusController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setVisible(false);
        cargarEstados();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcion.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        colEstadoFinal.setCellValueFactory(cellData -> {
            boolean estado = cellData.getValue().isEstadoFinal();
            return new SimpleStringProperty(estado ? "SÍ" : "NO");
        });

    }

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtId;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TableView<Estado> tblEstados;

    @FXML
    private TableColumn<Estado, Integer> colId;
    @FXML
    private TableColumn<Estado, String> colNombre;
    @FXML
    private TableColumn<Estado, String> colDescripcion;
    @FXML
    private TableColumn<Estado, String> colEstadoFinal;

    @FXML
    private void btnAddStatAction(ActionEvent event) {
        Estado estadoSeleccionado = tblEstados.getSelectionModel().getSelectedItem();

        if (estadoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un estado de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        if (estadoSeleccionado.isEstadoFinal()) {
            mostrarAlerta("Acción no permitida", "No se pueden asignar órdenes a un estado final.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddOrder.fxml"));
            Parent root = loader.load();

            AddOrderController controller = loader.getController();
            controller.setEstado(estadoSeleccionado.getId(), estadoSeleccionado.getNombre());

            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista AddOrder: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            boolean estadoFinal = chkActivo.isSelected();
            String idTexto = txtId.getText().trim();
            Integer id = 0;

            try {
                id = idTexto.isEmpty() ? 0 : Integer.parseInt(idTexto);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "ID inválido", Alert.AlertType.ERROR);
                return;
            }

            // Validación de nombre vacío
            if (nombre.isEmpty()) {
                mostrarAlerta("Validación", "El nombre del estado es obligatorio.", Alert.AlertType.WARNING);
                return;
            }

            // Validación de formato (opcional)
            if (nombre.length() < 3 || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                mostrarAlerta("Validación", "El nombre debe tener al menos 3 letras y solo contener caracteres alfabéticos.", Alert.AlertType.WARNING);
                return;
            }

            // Validación de existencia
            if (nombreEstadoExiste(nombre, id)) {
                mostrarAlerta("Duplicado", "Ya existe un estado con ese nombre. Por favor elige otro.", Alert.AlertType.WARNING);
                return;
            }

            boolean guardado = guardarEstado(id, nombre, descripcion, estadoFinal);

            if (guardado) {
                mostrarAlerta("Éxito", "Estado guardado correctamente.", Alert.AlertType.INFORMATION);
                cargarEstados();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Hubo un error al guardar el estado.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            mostrarAlerta("Error crítico", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean guardarEstado(int id, String nombre, String descripcion, boolean estadoFinal) {
        String sqlInsert = "INSERT INTO estado (nombre, descripcion, estado_final) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE estado SET nombre = ?, descripcion = ?, estado_final = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar()) {
            PreparedStatement stmt;

            if (id == 0) {
                stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            } else {
                stmt = conn.prepareStatement(sqlUpdate);
            }

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setBoolean(3, estadoFinal);

            if (id != 0) {
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
            mostrarAlerta("Error de BD", "Error al guardar el estado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    private boolean nombreEstadoExiste(String nombre, int idActual) {
        String sql = "SELECT COUNT(*) FROM estado WHERE LOWER(nombre) = LOWER(?) AND id <> ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setInt(2, idActual);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al verificar el nombre del estado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return false;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        Estado estadoSeleccionado = tblEstados.getSelectionModel().getSelectedItem();

        if (estadoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un estado de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        int id = estadoSeleccionado.getId();

        Estado estadoBD = buscarEstadoPorId(id);

        if (estadoBD != null) {
            txtId.setText(String.valueOf(estadoBD.getId()));
            txtNombre.setText(estadoBD.getNombre());
            txtDescripcion.setText(estadoBD.getDescripcion());
            chkActivo.setSelected(estadoBD.isEstadoFinal());
        } else {
            mostrarAlerta("Error", "No se encontró el estado en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    public static Estado buscarEstadoPorId(int id) {
        Estado estado = null;
        String sql = "SELECT id, nombre, descripcion, estado_final FROM estado WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estado = new Estado();
                estado.setId(rs.getInt("id"));
                estado.setNombre(rs.getString("nombre"));
                estado.setDescripcion(rs.getString("descripcion"));
                estado.setEstadoFinal(rs.getBoolean("estado_final"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar estado por ID: " + e.getMessage());
        }

        return estado;
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Estado estadoSeleccionado = tblEstados.getSelectionModel().getSelectedItem();

        if (estadoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona un estado de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        int idEstado = estadoSeleccionado.getId();

        // Verificar si el estado está en uso (tickets activos)
        if (estadoEnUso(idEstado)) {
            mostrarAlerta("No se puede eliminar", "Este estado está asociado a tickets activos. Reasigne los tickets antes de eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar este estado?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (eliminarEstado(idEstado)) {
                mostrarAlerta("Éxito", "Estado eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarEstados(); // Refrescar tabla
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el estado.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean estadoEnUso(int idEstado) {
        String sql = "SELECT COUNT(*) FROM ticket WHERE estado_id = ? AND estado_ticket = 'activo'";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al verificar uso del estado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        return false;
    }

    private boolean eliminarEstado(int idEstado) {
        String sql = "DELETE FROM estado WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEstado);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al eliminar el estado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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

    private void cargarEstados() {
        ObservableList<Estado> listaEstados = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, descripcion, estado_final FROM estado";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Estado estado = new Estado();
                estado.setId(rs.getInt("id"));
                estado.setNombre(rs.getString("nombre"));
                estado.setDescripcion(rs.getString("descripcion"));
                estado.setEstadoFinal(rs.getBoolean("estado_final"));

                listaEstados.add(estado);
            }

            tblEstados.setItems(listaEstados);

        } catch (SQLException e) {
            System.err.println("Error al cargar estados: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        chkActivo.setSelected(false);
    }

}

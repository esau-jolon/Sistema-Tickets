/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Adjunto;
import Models.ItemComboBox;
import Models.Ticket;
import conexion.ConexionDB;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddTicketController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdTicket.setVisible(false);
        cargarPrioridadesComboBox();
        cargarDepartamentosComboBox();

    }

    @FXML
    private TextField txtIdTicket;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView<Adjunto> tblAdjunto;
    @FXML
    private TableColumn<Adjunto, Integer> colId;
    @FXML
    private TableColumn<Adjunto, String> colArchivo;
    @FXML
    private TableColumn<Adjunto, String> colFecha;

    public void setIdTicket(int id) {
        txtIdTicket.setText(String.valueOf(id));
        cargarAdjuntos();
    }

    @FXML
    private ComboBox<ItemComboBox> cmbPrioridad;

    @FXML
    private ComboBox<ItemComboBox> cmbDepartamentos;

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        guardarTicket();
    }

    @FXML
    private void btnAdjuntoAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                byte[] datosArchivo = Files.readAllBytes(archivoSeleccionado.toPath());
                String tipoMime = Files.probeContentType(archivoSeleccionado.toPath());
                String nombreArchivo = archivoSeleccionado.getName();

                int ticketId = Integer.parseInt(txtIdTicket.getText().trim());

                Adjunto adjunto = new Adjunto(ticketId, nombreArchivo, tipoMime, datosArchivo);

                // Guardar en la base de datos
                guardarAdjunto(adjunto);

                JOptionPane.showMessageDialog(null, "Archivo adjuntado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarAdjuntos();

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID del ticket inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarAdjunto(Adjunto adjunto) {
        String sql = "INSERT INTO adjunto (ticket_id, nombre_archivo, tipo_mime, fecha_subida, archivo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adjunto.getTicketId());
            stmt.setString(2, adjunto.getNombreArchivo());
            stmt.setString(3, adjunto.getTipoMime());
            stmt.setTimestamp(4, adjunto.getFechaSubida());
            stmt.setBytes(5, adjunto.getArchivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar el archivo adjunto: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al guardar el archivo adjunto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarTicket() {
        // Validaciones
        if (txtTitulo.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Título y descripción son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cmbDepartamentos.getValue() == null || cmbPrioridad.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar departamento y prioridad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idTicket = Integer.parseInt(txtIdTicket.getText().trim());
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        int idDepartamento = cmbDepartamentos.getValue().getId();
        int idPrioridad = cmbPrioridad.getValue().getId();

        try (Connection conn = ConexionDB.conectar()) {

            // 1. Actualizar el ticket
            String updateSQL = "UPDATE ticket SET titulo = ?, descripcion = ?, id_departamento = ?, id_prioridad = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setString(1, titulo);
                updateStmt.setString(2, descripcion);
                updateStmt.setInt(3, idDepartamento);
                updateStmt.setInt(4, idPrioridad);
                updateStmt.setInt(5, idTicket);
                updateStmt.executeUpdate();
            }

            // 2. Obtener la cola correspondiente al nuevo departamento
            int idCola = -1;
            String consultaCola = "SELECT id FROM cola_atencion WHERE id_departamento = ?";
            try (PreparedStatement colaStmt = conn.prepareStatement(consultaCola)) {
                colaStmt.setInt(1, idDepartamento);
                ResultSet rsCola = colaStmt.executeQuery();
                if (rsCola.next()) {
                    idCola = rsCola.getInt("id");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró una cola de atención para el departamento seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 3. Verificar si ya está asignado en cola_ticket
            boolean yaAsignado = false;
            String verificarSQL = "SELECT 1 FROM cola_ticket WHERE id_ticket = ? LIMIT 1";
            try (PreparedStatement verificarStmt = conn.prepareStatement(verificarSQL)) {
                verificarStmt.setInt(1, idTicket);
                ResultSet rsVerificar = verificarStmt.executeQuery();
                if (rsVerificar.next()) {
                    yaAsignado = true;
                }
            }

            // 4. Insertar o actualizar en cola_ticket
            if (yaAsignado) {
                // Actualizar la cola existente
                String updateColaTicket = "UPDATE cola_ticket SET id_cola = ?, fecha_asignacion = NOW() WHERE id_ticket = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateColaTicket)) {
                    updateStmt.setInt(1, idCola);
                    updateStmt.setInt(2, idTicket);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insertar nueva entrada
                String insertColaTicket = "INSERT INTO cola_ticket (id_ticket, id_cola, fecha_asignacion) VALUES (?, ?, NOW())";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertColaTicket)) {
                    insertStmt.setInt(1, idTicket);
                    insertStmt.setInt(2, idCola);
                    insertStmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null, "Ticket actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            System.err.println("Error al guardar el ticket: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al guardar el ticket.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/MisTickets.fxml");
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

    private void cargarPrioridadesComboBox() {
        String sql = "SELECT id, nombre FROM prioridades ORDER BY nombre";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            ObservableList<ItemComboBox> listaEstados = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                listaEstados.add(new ItemComboBox(id, nombre));
            }

            cmbPrioridad.setItems(listaEstados);

        } catch (SQLException e) {
            mostrarAlerta("Error de BD", "Error al cargar estados: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cargarDepartamentosComboBox() {
        String sql = "SELECT id, nombre FROM departamento ORDER BY nombre";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            ObservableList<ItemComboBox> listaEstados = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                listaEstados.add(new ItemComboBox(id, nombre));
            }

            cmbDepartamentos.setItems(listaEstados);

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

    public void setDatosParaEditar(Ticket ticket) {
        txtIdTicket.setText(String.valueOf(ticket.getId()));
        txtTitulo.setText(ticket.getTitulo());
        txtDescripcion.setText(ticket.getDescripcion());

        seleccionarItemEnComboBox(cmbDepartamentos, ticket.getId_departamento());
        seleccionarItemEnComboBox(cmbPrioridad, ticket.getId_prioridad());
    }

    private void seleccionarItemEnComboBox(ComboBox<ItemComboBox> comboBox, int idSeleccionado) {
        for (ItemComboBox item : comboBox.getItems()) {
            if (item.getId() == idSeleccionado) {
                comboBox.setValue(item);
                break;
            }
        }
    }

    @FXML
    private void cargarAdjuntos() {
        ObservableList<Adjunto> listaAdjuntos = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre_archivo, fecha_subida FROM adjunto WHERE ticket_id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            int ticketId = Integer.parseInt(txtIdTicket.getText().trim());
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Adjunto adjunto = new Adjunto();
                    adjunto.setId(rs.getInt("id"));
                    adjunto.setNombreArchivo(rs.getString("nombre_archivo"));
                    adjunto.setFechaSubida(rs.getTimestamp("fecha_subida"));

                    listaAdjuntos.add(adjunto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar adjuntos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de ticket inválido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        // Asignación de columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSubidaFormatted"));

        tblAdjunto.setItems(listaAdjuntos);
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Adjunto adjuntoSeleccionado = tblAdjunto.getSelectionModel().getSelectedItem();

        if (adjuntoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un adjunto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar el adjunto \"" + adjuntoSeleccionado.getNombreArchivo() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM adjunto WHERE id = ?";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, adjuntoSeleccionado.getId());
                int filasAfectadas = stmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Adjunto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarAdjuntos(); // Vuelve a cargar la tabla
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo eliminar el adjunto.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al eliminar el adjunto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

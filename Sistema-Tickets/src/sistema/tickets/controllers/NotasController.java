/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.HistorialEstado;
import Models.NotaTicket;
import Models.Sesion;
import conexion.ConexionDB;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;
import sistema.tickets.Navegador;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Timestamp;
import java.sql.ResultSet;
/**
 * FXML Controller class
 *
 * @author esauj
 */
public class NotasController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField txtIdTIcket;
    @FXML
    private TextField txtDescripcion;

    private byte[] archivoAdjunto;
    private String tipoMime;

    @FXML
    private TableView<NotaTicket> tblNotas;

    @FXML
    private TableColumn<NotaTicket, Integer> colId;
    @FXML
    private TableColumn<NotaTicket, String> colDescripcion;
    @FXML
    private TableColumn<NotaTicket, String> colArchivo;
    @FXML
    private TableColumn<NotaTicket, String> colFecha;
    @FXML
    private TableColumn<NotaTicket, String> colAutor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdTIcket.setVisible(false);
    }

    @FXML
    private void btnAgregarArchivoAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            try {
                archivoAdjunto = Files.readAllBytes(archivo.toPath());
                tipoMime = Files.probeContentType(archivo.toPath());

                JOptionPane.showMessageDialog(null, "Archivo cargado correctamente: " + archivo.getName());

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String descripcion = txtDescripcion.getText().trim();
        String ticketIdTexto = txtIdTIcket.getText().trim();

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La descripción es obligatoria.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (archivoAdjunto == null || tipoMime == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un archivo antes de guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(ticketIdTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID de ticket inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int autorId = Sesion.getUsuario().getId();

        String sql = "INSERT INTO nota_ticket (ticket_id, descripcion, archivo, tipo_mime, fecha, autor_id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setString(2, descripcion);
            stmt.setBytes(3, archivoAdjunto);
            stmt.setString(4, tipoMime);
            stmt.setInt(5, autorId);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Nota guardada correctamente.");
            cargarNotas();

            // Limpiar campos después de guardar
            txtDescripcion.clear();
            archivoAdjunto = null;
            tipoMime = null;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la nota.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/ServicingTicket.fxml");

    }

    public void setIdTicket(int idTicket) {
        txtIdTIcket.setText(String.valueOf(idTicket));
        cargarNotas();
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
    private void cargarNotas() {
        ObservableList<NotaTicket> listaNotas = FXCollections.observableArrayList();

        String sql = """
        SELECT n.id, n.ticket_id, n.descripcion, n.archivo, n.tipo_mime, n.fecha, n.autor_id,
               p.nombre AS nombre_autor
        FROM nota_ticket n
        JOIN persona p ON n.autor_id = p.id
        WHERE n.ticket_id = ?
        ORDER BY n.fecha DESC
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int ticketId = Integer.parseInt(txtIdTIcket.getText());
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NotaTicket nota = new NotaTicket(
                            rs.getInt("id"),
                            rs.getInt("ticket_id"),
                            rs.getString("descripcion"),
                            rs.getBytes("archivo"),
                            rs.getString("tipo_mime"),
                            rs.getTimestamp("fecha"),
                            rs.getInt("autor_id")
                    );

                    // Campos opcionales
                    nota.setNombreArchivo("Archivo adjunto");
                    nota.setNombreAutor(rs.getString("nombre_autor"));

                    listaNotas.add(nota);
                }
            }

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar notas.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Configurar columnas (¡corrige los tipos! estás usando tipos de `HistorialEstado`)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colFecha.setCellValueFactory(cell -> {
            Timestamp ts = cell.getValue().getFecha();
            return new SimpleStringProperty(ts != null ? ts.toLocalDateTime().toString() : "");
        });
        colAutor.setCellValueFactory(new PropertyValueFactory<>("nombreAutor"));

        tblNotas.setItems(listaNotas);
    }

}

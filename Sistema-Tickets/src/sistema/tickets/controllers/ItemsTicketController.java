/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.HistorialEstado;
import Models.NotaTicket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import sistema.tickets.Navegador;

import conexion.ConexionDB;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class ItemsTicketController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (idTicket != 0 && txtIdTIcket != null) {
            txtIdTIcket.setText(String.valueOf(idTicket));
        }
        txtIdTIcket.setEditable(false);
    }
    private int idTicket;
    @FXML
    private TextField txtIdTIcket;

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

    @FXML
    private TableView<HistorialEstado> tblHistorial;

    @FXML
    private TableColumn<HistorialEstado, Integer> colIdC;
    @FXML
    private TableColumn<HistorialEstado, String> colNuevo;
    @FXML
    private TableColumn<HistorialEstado, String> colAnterior;
    @FXML
    private TableColumn<HistorialEstado, String> colComentario;
    @FXML
    private TableColumn<HistorialEstado, String> colFechaC;
    @FXML
    private TableColumn<HistorialEstado, String> colCambiado;

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

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;

        if (txtIdTIcket != null) {
            txtIdTIcket.setText(String.valueOf(idTicket));
        }

        cargarNotas();
        cargarHistorialEstado();
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

        colIdC.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAnterior.setCellValueFactory(new PropertyValueFactory<>("nombreEstadoAnterior"));
        colNuevo.setCellValueFactory(new PropertyValueFactory<>("nombreNuevoEstado"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        colFechaC.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<HistorialEstado, String> param) {
                Timestamp ts = param.getValue().getFechaCambio();
                return new SimpleStringProperty(ts.toLocalDateTime().toString());
            }
        });
        colCambiado.setCellValueFactory(new PropertyValueFactory<>("nombreCambiadoPor"));

        tblHistorial.setItems(listaHistorial);
    }

    /*
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
     */
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

        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
        colFecha.setCellValueFactory(cell -> {
            Timestamp ts = cell.getValue().getFecha();
            return new SimpleStringProperty(ts != null ? ts.toLocalDateTime().toString() : "");
        });
        colAutor.setCellValueFactory(new PropertyValueFactory<>("nombreAutor"));

        tblNotas.setItems(listaNotas);

        // Evento doble clic para abrir archivo adjunto
        tblNotas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                NotaTicket notaSeleccionada = tblNotas.getSelectionModel().getSelectedItem();
                if (notaSeleccionada != null && notaSeleccionada.getArchivo() != null) {
                    try {
                        String extension = obtenerExtensionDesdeMime(notaSeleccionada.getTipoMime());
                        File tempFile = File.createTempFile("adjunto_", extension);
                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            fos.write(notaSeleccionada.getArchivo());
                        }

                        // Abrir el archivo con la aplicación predeterminada del sistema
                        Desktop.getDesktop().open(tempFile);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo abrir el archivo adjunto.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    // Método auxiliar para obtener extensión del tipo MIME
    private String obtenerExtensionDesdeMime(String mimeType) {
        switch (mimeType) {
            case "application/pdf":
                return ".pdf";
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            case "text/plain":
                return ".txt";
            case "application/msword":
                return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            default:
                return ".bin";
        }
    }

}



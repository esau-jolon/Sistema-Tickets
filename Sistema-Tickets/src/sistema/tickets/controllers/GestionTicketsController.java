/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Ticket;
import conexion.ConexionDB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class GestionTicketsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTodosLosTickets();
    }

    @FXML
    private TableView<Ticket> tblTickets;
    @FXML
    private TableColumn<Ticket, Integer> colId;
    @FXML
    private TableColumn<Ticket, String> colTitulo;
    @FXML
    private TableColumn<Ticket, String> colDescripcion;

    @FXML
    private TableColumn<Ticket, String> colFecha;
    @FXML
    private TableColumn<Ticket, String> colDepartamento;

    @FXML
    private TableColumn<Ticket, String> colEstado;

    @FXML
    private TableColumn<Ticket, String> colPrioridad;
    @FXML
    private TableColumn<Ticket, String> colTecnico;

    private void cargarTodosLosTickets() {
        Queue<Ticket> colaTickets = new LinkedList<>();

        String sqlTickets = "SELECT t.id, t.titulo, t.descripcion, t.fecha_creacion, "
                + "t.id_tecnico_asignado, t.id_usuario, t.id_departamento, t.id_prioridad, t.id_estado, "
                + "p.nombre AS tecnico, e.nombre AS estado, pr.nombre AS prioridad, d.nombre AS departamento "
                + "FROM ticket t "
                + "LEFT JOIN persona p ON t.id_tecnico_asignado = p.id "
                + "LEFT JOIN estado e ON t.id_estado = e.id "
                + "LEFT JOIN prioridades pr ON t.id_prioridad = pr.id "
                + "LEFT JOIN departamento d ON t.id_departamento = d.id "
                + "ORDER BY t.fecha_creacion DESC";  // Ordena por fecha de creación, puedes cambiar según prefieras

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmtTickets = conn.prepareStatement(sqlTickets); ResultSet rs = stmtTickets.executeQuery()) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getTimestamp("fecha_creacion"),
                        rs.getInt("id_tecnico_asignado"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_departamento"),
                        rs.getInt("id_prioridad"),
                        rs.getInt("id_estado"),
                        Optional.ofNullable(rs.getString("tecnico")).orElse("N/A"),
                        Optional.ofNullable(rs.getString("estado")).orElse("N/A"),
                        rs.getString("prioridad"),
                        rs.getString("departamento")
                );
                colaTickets.offer(ticket);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error BD", "Error al cargar tickets: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        // Convertir la cola a ObservableList para la tabla
        ObservableList<Ticket> listaTickets = FXCollections.observableArrayList(colaTickets);

        // Configurar columnas si no están configuradas
        if (colId.getCellValueFactory() == null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
            colTecnico.setCellValueFactory(new PropertyValueFactory<>("tecnico"));
            colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
            colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        }

        tblTickets.setItems(listaTickets);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    @FXML
    private void btnExportarAction(ActionEvent event) {
        // Exporta los tickets actuales en la tabla a un archivo binario
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo de tickets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo binario", "*.bin"));
        File archivo = fileChooser.showSaveDialog(null);

        if (archivo != null) {
            exportarTickets(tblTickets.getItems(), archivo.getAbsolutePath());
            mostrarAlerta("Exportar", "Tickets exportados correctamente.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void btnImportarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo de tickets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo binario", "*.bin"));
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            List<Ticket> ticketsImportados = importarTickets(archivo.getAbsolutePath());
            if (ticketsImportados != null && !ticketsImportados.isEmpty()) {
                insertarTicketsEnBD(ticketsImportados); // Insertar en BD sin IDs para crear registros nuevos
                // Luego recargar tabla desde BD para mostrar datos actualizados con IDs generados
                cargarTodosLosTickets(); // o el método que uses para cargar tickets
                mostrarAlerta("Importar", "Tickets importados e insertados correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Importar", "No se importaron tickets.", Alert.AlertType.WARNING);
            }
        }
    }

    public void insertarTicketsEnBD(List<Ticket> tickets) {
        String sqlInsert = """
        INSERT INTO ticket (titulo, descripcion, fecha_creacion, id_tecnico_asignado, id_usuario, id_departamento, id_prioridad, id_estado)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                for (Ticket t : tickets) {
                    stmt.setString(1, t.getTitulo());
                    stmt.setString(2, t.getDescripcion());
                    stmt.setTimestamp(3, t.getFechaCreacion());

                    // Para id_tecnico_asignado, controlar null
                    if (t.getId_tecnico_asignado() != null && t.getId_tecnico_asignado() > 0) {
                        stmt.setInt(4, t.getId_tecnico_asignado());
                    } else {
                        stmt.setNull(4, java.sql.Types.INTEGER);
                    }

                    // id_usuario (no debería ser null)
                    if (t.getId_usuario() != null) {
                        stmt.setInt(5, t.getId_usuario());
                    } else {
                        stmt.setNull(5, java.sql.Types.INTEGER);
                    }

                    // id_departamento
                    // Ejemplo para id_departamento
                    if (t.getId_departamento() != null && t.getId_departamento() > 0) {
                        stmt.setInt(6, t.getId_departamento());
                    } else {
                        stmt.setNull(6, java.sql.Types.INTEGER);
                    }

                    // id_prioridad
                    if (t.getId_prioridad() != null) {
                        stmt.setInt(7, t.getId_prioridad());
                    } else {
                        stmt.setNull(7, java.sql.Types.INTEGER);
                    }

                    // id_estado
                    if (t.getId_estado() != null) {
                        stmt.setInt(8, t.getId_estado());
                    } else {
                        stmt.setNull(8, java.sql.Types.INTEGER);
                    }

                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                mostrarAlerta("Error BD", "Error al insertar tickets: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error BD", "Error en conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void exportarTickets(List<Ticket> tickets, String rutaArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(new ArrayList<>(tickets));
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error exportar", "No se pudo exportar tickets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Ticket> importarTickets(String rutaArchivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<Ticket>) obj;
            } else {
                mostrarAlerta("Error importar", "El archivo no contiene una lista de tickets válida.", Alert.AlertType.ERROR);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            mostrarAlerta("Error importar", "No se pudo importar tickets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return Collections.emptyList();
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
}

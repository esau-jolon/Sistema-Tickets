/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Sesion;
import Models.Ticket;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class ServicingTicketController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTicketsPorDepartamento();
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

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    @FXML
    private void btnNotasAction(ActionEvent event) {
        Ticket ticketSeleccionado = tblTickets.getSelectionModel().getSelectedItem();

        if (ticketSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un ticket para continuar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTicket = ticketSeleccionado.getId();
        int idTecnicoActual = Sesion.getUsuario().getId();

        try (Connection conn = ConexionDB.conectar()) {
            String verificarSQL = "SELECT id_tecnico_asignado FROM ticket WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(verificarSQL)) {
                stmt.setInt(1, idTicket);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int tecnicoAsignado = rs.getInt("id_tecnico_asignado");

                    // Si ya hay técnico asignado
                    if (tecnicoAsignado != 0) {
                        if (tecnicoAsignado != idTecnicoActual) {
                            JOptionPane.showMessageDialog(null, "Este ticket ya fue asignado a otro técnico.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        // Si el técnico es el mismo, se permite continuar sin reasignar
                    } else {
                        // Si no hay técnico asignado, asignarlo
                        String asignarSQL = "UPDATE ticket SET id_tecnico_asignado = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(asignarSQL)) {
                            updateStmt.setInt(1, idTecnicoActual);
                            updateStmt.setInt(2, idTicket);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }

            // Abrir vista de Notas.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/Notas.fxml"));
            Parent root = loader.load();

            // Enviar el ID al controlador de notas
            NotasController controller = loader.getController();
            controller.setIdTicket(idTicket); // Este método debes tenerlo en NotasController

            Navegador.mostrarVistaCentral(root);

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error al procesar la solicitud de notas.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @FXML
    private void btnChangeAction(ActionEvent event) {
        Ticket ticketSeleccionado = tblTickets.getSelectionModel().getSelectedItem();

        if (ticketSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un ticket para continuar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTicket = ticketSeleccionado.getId();
        int idTecnico = Sesion.getUsuario().getId();

        // 1. Verificar si ya tiene técnico asignado
        try (Connection conn = ConexionDB.conectar()) {
            String verificarSQL = "SELECT id_tecnico_asignado FROM ticket WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(verificarSQL)) {
                stmt.setInt(1, idTicket);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int asignado = rs.getInt("id_tecnico_asignado");

                    // Permitir si no hay técnico asignado o si es el mismo técnico
                    if (asignado != 0 && asignado != idTecnico) {
                        JOptionPane.showMessageDialog(null, "Este ticket ya fue asignado a otro técnico.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // 2. Asignar el técnico solo si no está asignado aún
            String sql = "UPDATE ticket SET id_tecnico_asignado = ? WHERE id = ? AND (id_tecnico_asignado IS NULL OR id_tecnico_asignado = 0)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idTecnico);
                stmt.setInt(2, idTicket);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar/asignar técnico al ticket.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // 3. Cargar vista de resolución
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/Resolver.fxml"));
            Parent root = loader.load();

            ResolverController controller = loader.getController();
            controller.setIdTicket(idTicket);

            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la vista Resolver.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    private void cargarTicketsPorDepartamento() {

        Queue<Ticket> colaTickets = new LinkedList<>();

        int idUsuario = Sesion.getUsuario().getId();

        String sqlDepartamento = "SELECT id_departamento FROM persona WHERE id = ?";
        String sqlTickets = "SELECT t.id, t.titulo, t.descripcion, t.fecha_creacion, "
                + "t.id_tecnico_asignado, t.id_usuario, t.id_departamento, t.id_prioridad, t.id_estado, "
                + "p.nombre AS tecnico, e.nombre AS estado, pr.nombre AS prioridad, d.nombre AS departamento "
                + "FROM ticket t "
                + "LEFT JOIN persona p ON t.id_tecnico_asignado = p.id "
                + "LEFT JOIN estado e ON t.id_estado = e.id "
                + "LEFT JOIN prioridades pr ON t.id_prioridad = pr.id "
                + "LEFT JOIN departamento d ON t.id_departamento = d.id "
                + "INNER JOIN cola_ticket ct ON t.id = ct.id_ticket "
                + "INNER JOIN cola_atencion ca ON ct.id_cola = ca.id "
                + "WHERE ca.id_departamento = ? "
                + "ORDER BY ct.fecha_asignacion ASC"; // Ordenado por fecha para FIFO

        try (Connection conn = ConexionDB.conectar()) {

            int idDepartamento = -1;
            try (PreparedStatement stmtDept = conn.prepareStatement(sqlDepartamento)) {
                stmtDept.setInt(1, idUsuario);
                try (ResultSet rsDept = stmtDept.executeQuery()) {
                    if (rsDept.next()) {
                        idDepartamento = rsDept.getInt("id_departamento");
                    } else {
                        mostrarAlerta("Error", "Usuario no asignado a departamento", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }

            // 2. Consultar tickets y agregarlos a la cola
            try (PreparedStatement stmtTickets = conn.prepareStatement(sqlTickets)) {
                stmtTickets.setInt(1, idDepartamento);

                try (ResultSet rs = stmtTickets.executeQuery()) {
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
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error BD", "Error al cargar tickets: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        // 3. Convertir la cola a ObservableList para la tabla
        ObservableList<Ticket> listaTickets = FXCollections.observableArrayList(colaTickets);

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

}

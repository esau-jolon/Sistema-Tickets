/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
import Models.Sesion;
import Models.Ticket;
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
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class MisTicketsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTickets();
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
    private void btnNuevoAction(ActionEvent event) {
        int nuevoId = crearTicketVacio();
        if (nuevoId > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddTicket.fxml"));
                Parent root = loader.load();

                // Obtener el controlador y pasarle el ID
                AddTicketController controller = loader.getController();
                controller.setIdTicket(nuevoId);

                // Mostrar la vista (esto depende de cómo manejes las vistas en tu app)
                Navegador.mostrarVistaCentral(root); // Asumiendo que acepta un Node/Parent

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("No se pudo crear el ticket.");
        }
    }

    private int crearTicketVacio() {
        String insertSQL = "INSERT INTO ticket (titulo, descripcion, id_usuario, id_tecnico_asignado, id_departamento, id_prioridad, id_estado) "
                + "VALUES (NULL, NULL, ?, NULL, NULL, NULL, NULL) RETURNING id";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            int idUsuario = Sesion.getUsuario().getId();  // <- aquí obtienes el ID del usuario desde la sesión

            stmt.setInt(1, idUsuario);  // <- aquí lo pasas al SQL

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("Error al crear ticket vacío: " + e.getMessage());
            e.printStackTrace();
        }

        return -1; // error
    }

    @FXML
    private void cargarTickets() {
        ObservableList<Ticket> listaTickets = FXCollections.observableArrayList();

        String sql = "SELECT t.id, t.titulo, t.descripcion, t.fecha_creacion, t.id_tecnico_asignado, "
                + "t.id_usuario, t.id_departamento, t.id_prioridad, t.id_estado, "
                + "p.nombre AS tecnico, e.nombre AS estado, pr.nombre AS prioridad, d.nombre AS departamento "
                + "FROM ticket t "
                + "LEFT JOIN persona p ON t.id_tecnico_asignado = p.id "
                + "LEFT JOIN estado e ON t.id_estado = e.id "
                + "LEFT JOIN prioridades pr ON t.id_prioridad = pr.id "
                + "LEFT JOIN departamento d ON t.id_departamento = d.id "
                + "WHERE t.id_usuario = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Sesion.getUsuario().getId()); // Asumiendo que tienes una clase Sesion con el método getUsuario()

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tecnico = rs.getString("tecnico");
                    if (tecnico == null || tecnico.trim().isEmpty()) {
                        tecnico = "N/A";
                    }

                    String estado = rs.getString("estado");
                    if (estado == null || estado.trim().isEmpty()) {
                        estado = "N/A";
                    }

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
                            tecnico,
                            estado,
                            rs.getString("prioridad"),
                            rs.getString("departamento")
                    );
                    listaTickets.add(ticket);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        colTecnico.setCellValueFactory(new PropertyValueFactory<>("tecnico"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("departamento"));

        tblTickets.setItems(listaTickets);
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        Ticket ticketSeleccionado = tblTickets.getSelectionModel().getSelectedItem();

        if (ticketSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un ticket para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddTicket.fxml"));
            Parent root = loader.load();

            AddTicketController controller = loader.getController();
            controller.setIdTicket(ticketSeleccionado.getId());
            controller.setDatosParaEditar(ticketSeleccionado);

            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar la vista de edición.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Departamento;
import Models.Rol;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class DepartmentsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        txtId.setEditable(false);
        cargarDatos();
    }

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtId;

    @FXML
    private TableView<Departamento> tblDepartamentos;

    @FXML
    private TableColumn<Departamento, Integer> colId;
    @FXML
    private TableColumn<Departamento, String> colNombre;
    @FXML
    private TableColumn<Departamento, String> colDescripcion;

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String idTexto = txtId.getText().trim();
        int idEmpresa = 1;

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor completa todos los campos.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!idTexto.isEmpty()) {

            int id = Integer.parseInt(idTexto);
            String sqlUpdate = "UPDATE departamento SET nombre = ?, descripcion = ?, id_empresa = ? WHERE id = ?";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

                pstmt.setString(1, nombre);
                pstmt.setString(2, descripcion);
                pstmt.setInt(3, idEmpresa);
                pstmt.setInt(4, id);

                int filasActualizadas = pstmt.executeUpdate();
                if (filasActualizadas > 0) {
                    JOptionPane.showMessageDialog(null, "Departamento actualizado correctamente.");
                    limpiarCampos();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo actualizar el departamento.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                System.err.println("Error al actualizar: " + e.getMessage());
            }

        } else {

            String sqlInsert = "INSERT INTO departamento (nombre, descripcion, id_empresa) VALUES (?, ?, ?) RETURNING id";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

                pstmt.setString(1, nombre);
                pstmt.setString(2, descripcion);
                pstmt.setInt(3, idEmpresa);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int idDepartamento = rs.getInt("id");

                    String nombreCola = nombre + " - cola de atención";
                    String sqlInsertCola = "INSERT INTO cola_atencion (id_departamento, nombre) VALUES (?, ?)";

                    try (PreparedStatement pstmtCola = conn.prepareStatement(sqlInsertCola)) {
                        pstmtCola.setInt(1, idDepartamento);
                        pstmtCola.setString(2, nombreCola);
                        pstmtCola.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Departamento y cola de atención creados correctamente.");
                    limpiarCampos();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo crear el departamento.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                System.err.println("Error al insertar departamento o cola: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        tblDepartamentos.getSelectionModel().clearSelection();
    }

    @FXML
    private void btnEliminarAction(ActionEvent event) {
        Departamento deptoSeleccionado = tblDepartamentos.getSelectionModel().getSelectedItem();

        if (deptoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Por favor selecciona un departamento de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idDepartamento = deptoSeleccionado.getId();

        String sqlVerificarTickets = "SELECT COUNT(*) FROM ticket WHERE id_departamento = ?";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmtTickets = conn.prepareStatement(sqlVerificarTickets)) {

            pstmtTickets.setInt(1, idDepartamento);
            ResultSet rsTickets = pstmtTickets.executeQuery();

            if (rsTickets.next() && rsTickets.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "No se puede eliminar el departamento porque tiene tickets en cola.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar tickets: " + e.getMessage());
            return;
        }

        String sqlVerificarPersonas = "SELECT COUNT(*) FROM persona WHERE id_departamento = ?";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmtVerificar = conn.prepareStatement(sqlVerificarPersonas)) {

            pstmtVerificar.setInt(1, idDepartamento);
            ResultSet rs = pstmtVerificar.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "No se puede eliminar el departamento porque tiene usuarios asignados.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar personas asignadas: " + e.getMessage());
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de que deseas eliminar este departamento?",
                "Confirmación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sqlEliminarCola = "DELETE FROM cola_atencion WHERE id_departamento = ?";
            String sqlEliminarDepartamento = "DELETE FROM departamento WHERE id = ?";

            try (Connection conn = ConexionDB.conectar()) {
                conn.setAutoCommit(false);

                try (PreparedStatement pstmtCola = conn.prepareStatement(sqlEliminarCola)) {
                    pstmtCola.setInt(1, idDepartamento);
                    pstmtCola.executeUpdate();
                }

                try (PreparedStatement pstmtEliminar = conn.prepareStatement(sqlEliminarDepartamento)) {
                    pstmtEliminar.setInt(1, idDepartamento);
                    int filasEliminadas = pstmtEliminar.executeUpdate();

                    if (filasEliminadas > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(null, "Departamento eliminado correctamente.");
                        limpiarCampos();
                        cargarDatos();
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(null, "No se pudo eliminar el departamento.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {

        Departamento deptoSeleccionado = tblDepartamentos.getSelectionModel().getSelectedItem();

        if (deptoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Por favor selecciona un departamento de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = deptoSeleccionado.getId();

        Departamento deptoBD = buscarDepartamentoPorId(id);

        if (deptoBD != null) {

            txtId.setText(String.valueOf(deptoBD.getId()));
            txtNombre.setText(deptoBD.getNombre());
            txtDescripcion.setText(deptoBD.getDescripcion());
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el departamento en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Departamento buscarDepartamentoPorId(int idBuscado) {
        Departamento departamento = null;
        String sql = "SELECT id, nombre, descripcion FROM departamento WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idBuscado);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                departamento = new Departamento();
                departamento.setId(rs.getInt("id"));
                departamento.setNombre(rs.getString("nombre"));
                departamento.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar el departamento: " + e.getMessage());
        }

        return departamento;
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
    private void btnAddTecnicoAction(ActionEvent event) throws IOException {
        Departamento departamentoSeleccionado = tblDepartamentos.getSelectionModel().getSelectedItem();

        if (departamentoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un departamento para continuar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idDepartamento = departamentoSeleccionado.getId();
        String nombreDepartamento = departamentoSeleccionado.getNombre();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddTechnical.fxml"));
        Parent root = loader.load();

        AddTechnicalController controller = loader.getController();
        controller.setDatosDepartamento(idDepartamento, nombreDepartamento);

        Navegador.mostrarVistaCentral(root);
    }

    private void cargarDatos() {
        ObservableList<Departamento> listaDepartamentos = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, descripcion FROM departamento";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Departamento depart = new Departamento();
                depart.setId(id);
                depart.setNombre(nombre);
                depart.setDescripcion(descripcion);

                listaDepartamentos.add(depart);
            }

            tblDepartamentos.setItems(listaDepartamentos);

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

}

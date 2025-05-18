/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
import conexion.ConexionDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class PermissionsController implements Initializable {

    @FXML
    private TableView<Permiso> tblPermisos;
    @FXML
    private TableColumn<Permiso, String> colNombre;
    @FXML
    private TableColumn<Permiso, String> colDescripcion;
    @FXML
    private TableColumn<Permiso, Integer> colId;
    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        txtId.setEditable(false);
        cargarTodosLosPermisos();
        
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Menu.fxml");
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {

        Permiso permisoSeleccionado = tblPermisos.getSelectionModel().getSelectedItem();

        if (permisoSeleccionado == null) {
            JOptionPane.showMessageDialog(null,
                    "Por favor selecciona un permiso de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Permiso permisoBD = buscarPermisoPorId(permisoSeleccionado.getId());

        if (permisoBD != null) {

            txtId.setText(String.valueOf(permisoBD.getId()));
            txtNombre.setText(permisoBD.getNombre());
            txtDescripcion.setText(permisoBD.getDescripcion());
        } else {
            JOptionPane.showMessageDialog(null,
                    "No se encontró el permiso en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Permiso buscarPermisoPorId(int id) {
        Permiso permiso = null;
        String sql = """
            SELECT
                id,
                nombre,
                descripcion
            FROM
                permiso
            WHERE
                id = ?
        """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    permiso = new Permiso();
                    permiso.setId(rs.getInt("id"));
                    permiso.setNombre(rs.getString("nombre"));
                    permiso.setDescripcion(rs.getString("descripcion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar permiso por ID: " + e.getMessage());
        }

        return permiso;
    }

   @FXML
    private void btnEliminarAction(ActionEvent event) {
        
        Permiso permisoSeleccionado = tblPermisos.getSelectionModel().getSelectedItem();
        if (permisoSeleccionado == null) {
            JOptionPane.showMessageDialog(null,
                "Por favor selecciona un permiso de la tabla.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        int respuesta = JOptionPane.showConfirmDialog(null,
            "¿Estás seguro de que deseas eliminar el permiso \"" 
            + permisoSeleccionado.getNombre() + "\"?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        boolean eliminado = eliminarPermisoPorId(permisoSeleccionado.getId());
        if (eliminado) {
            JOptionPane.showMessageDialog(null,
                "Permiso eliminado correctamente.");
           
            refrescarTablaPermisos();
        } else {
            JOptionPane.showMessageDialog(null,
                "Hubo un error al eliminar el permiso.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private boolean eliminarPermisoPorId(int idPermiso) {
        String sqlDeleteRolPermiso = 
            "DELETE FROM rol_permiso WHERE id_permiso = ?";
        String sqlDeletePermiso = 
            "DELETE FROM permiso WHERE id = ?";

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);

         
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteRolPermiso)) {
                stmt.setInt(1, idPermiso);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlDeletePermiso)) {
                stmt.setInt(1, idPermiso);
                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error al eliminar permiso: " + ex.getMessage());
            return false;
        }
    }
    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String idTexto = txtId.getText().trim();
        Integer id = 0;
        try {
            id = idTexto.isEmpty() ? 0 : Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            id = 0;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "El nombre del permiso es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = guardarPermiso(id, nombre, descripcion);
        if (ok) {
            String accion = (id == null || id == 0) ? "creado" : "actualizado";
            JOptionPane.showMessageDialog(null,
                    "Permiso " + accion + " correctamente.");
            
            refrescarTablaPermisos();
            limpiarCamposPermiso();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Hubo un error al guardar el permiso.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean guardarPermiso(Integer id, String nombre, String descripcion) {
        
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String sqlInsertPermiso
                = "INSERT INTO permiso (nombre, descripcion) VALUES (?, ?) RETURNING id";
        String sqlSelectRoles
                = "SELECT id FROM rol";
        String sqlInsertRolPermiso
                = "INSERT INTO rol_permiso (id_rol, id_permiso, stat) VALUES (?, ?, FALSE)";
        String sqlUpdatePermiso
                = "UPDATE permiso SET nombre = ?, descripcion = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);

            if (id == null || id == 0) {
                
                int newPermisoId;
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsertPermiso)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, descripcion);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            newPermisoId = rs.getInt("id");
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                List<Integer> rolIds = new ArrayList<>();
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlSelectRoles)) {
                    while (rs.next()) {
                        rolIds.add(rs.getInt("id"));
                    }
                }

                
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsertRolPermiso)) {
                    for (Integer rolId : rolIds) {
                        stmt.setInt(1, rolId);
                        stmt.setInt(2, newPermisoId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

            } else {
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdatePermiso)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, descripcion);
                    stmt.setInt(3, id);
                    int updated = stmt.executeUpdate();
                    if (updated == 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            System.err.println("Error al guardar o actualizar Permiso: " + ex.getMessage());
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

    private void cargarTodosLosPermisos() {
        ObservableList<Permiso> listaPermisos = FXCollections.observableArrayList();
        String sql = """
        SELECT
            id,
            nombre,
            descripcion
        FROM
            permiso
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Permiso permiso = new Permiso();
                permiso.setId(rs.getInt("id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                listaPermisos.add(permiso);
            }
            tblPermisos.setItems(listaPermisos);

        } catch (SQLException e) {
            System.err.println("Error al cargar permisos: " + e.getMessage());
        }
    }

    private void limpiarCamposPermiso() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        tblPermisos.getSelectionModel().clearSelection();
    }

    private void refrescarTablaPermisos() {
        cargarTodosLosPermisos();
    }

}

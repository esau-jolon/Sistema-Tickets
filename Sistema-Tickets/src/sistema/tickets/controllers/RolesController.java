/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javax.swing.JOptionPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class RolesController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView<Rol> tblRoles;

    @FXML
    private TableColumn<Rol, Integer> colId;
    @FXML
    private TableColumn<Rol, String> colNombre;
    @FXML
    private TableColumn<Rol, String> colDescripcion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Cargar los datos
        cargarDatosRoles();
    }

    @FXML
    private void btnPermissionsAction(ActionEvent event) {
        Rol selectedRol = tblRoles.getSelectionModel().getSelectedItem();

        if (selectedRol == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecciona un rol primero.");
            alert.show();
            return;
        }

        AddPermissionsController controller = Navegador.mostrarVistaCentralConControlador("/sistema/tickets/views/AddPermissions.fxml");

        if (controller != null) {
            controller.setIdRol(selectedRol.getId());
            controller.setNombreRol(selectedRol.getNombre());
        }
    }

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
    private void btnEliminarAction(ActionEvent event) {
        Rol selectedRol = tblRoles.getSelectionModel().getSelectedItem();

        if (selectedRol == null) {
            JOptionPane.showMessageDialog(null, "Selecciona un rol para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (RolesController.tienePermisosAsociados(selectedRol.getId())) {
            JOptionPane.showMessageDialog(null, "No se puede eliminar este rol porque tiene permisos asociados.",
                    "Restricción", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de que deseas eliminar el rol \"" + selectedRol.getNombre() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = RolesController.eliminarRolPorId(selectedRol.getId());
            if (eliminado) {
                JOptionPane.showMessageDialog(null, "Rol eliminado correctamente.");
                refrescarTablaRoles();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el rol.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static boolean eliminarRolPorId(int idRol) {
        String sql = "DELETE FROM rol WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRol);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean tienePermisosAsociados(int idRol) {
        String sql = "SELECT COUNT(*) FROM rol_Permiso WHERE id_rol = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRol);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt(1);
                    return total > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @FXML
    private void handleMouseExited(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        sourceButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    /*
    public static boolean guardarRol(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar rol: " + e.getMessage());
            return false;
        }
    }
     */
    public static boolean guardarRol(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String sqlInsertRol = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?) RETURNING id";
        String sqlSelectPermisos = "SELECT id FROM permiso";
        String sqlInsertRolPermiso = "INSERT INTO rol_permiso (id_rol, id_permiso, stat) VALUES (?, ?, FALSE)";

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false); // Inicia la transacción

            // 1. Insertar nuevo rol y obtener el id generado
            int idRol;
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertRol)) {
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idRol = rs.getInt("id");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Obtener todos los permisos existentes
            List<Integer> idsPermisos = new ArrayList<>();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlSelectPermisos)) {

                while (rs.next()) {
                    idsPermisos.add(rs.getInt("id"));
                }
            }

            // 3. Insertar en rol_permiso
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertRolPermiso)) {
                for (int idPermiso : idsPermisos) {
                    stmt.setInt(1, idRol);
                    stmt.setInt(2, idPermiso);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit(); 
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar rol con permisos: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ambos campos son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean guardado = RolesController.guardarRol(nombre, descripcion);

        if (guardado) {
            JOptionPane.showMessageDialog(null, "Rol guardado correctamente");
            refrescarTablaRoles();
        } else {
            JOptionPane.showMessageDialog(null, "Hubo un error al guardar el rol",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosRoles() {
        ObservableList<Rol> listaRoles = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, descripcion FROM rol";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Rol rol = new Rol();
                rol.setId(id);
                rol.setNombre(nombre);
                rol.setDescripcion(descripcion);

                listaRoles.add(rol);
            }

            tblRoles.setItems(listaRoles);

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    public void refrescarTablaRoles() {
        cargarDatosRoles();
    }

    public Rol obtenerRolConPermisos(int idRol) {
        Rol rol = null;

        try (Connection conn = ConexionDB.conectar()) {
            // 1. Obtener los datos del rol
            String sqlRol = "SELECT nombre, descripcion FROM rol WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRol)) {
                ps.setInt(1, idRol);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    rol = new Rol();
                    rol.setId(idRol);
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));
                }
            }

            if (rol != null) {
                List<Permiso> permisos = new ArrayList<>();
                String sqlPermisos = """
                    SELECT p.id, p.nombre, p.descripcion
                    FROM permiso p
                    INNER JOIN rol_permiso rp ON p.id = rp.id_permiso
                    WHERE rp.id_rol = ?
                """;
                try (PreparedStatement ps = conn.prepareStatement(sqlPermisos)) {
                    ps.setInt(1, idRol);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Permiso permiso = new Permiso();
                        permiso.setId(rs.getInt("id"));
                        permiso.setNombre(rs.getString("nombre"));
                        permiso.setDescripcion(rs.getString("descripcion"));
                        permisos.add(permiso);
                    }
                }
                rol.setPermisos(permisos);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el rol con permisos: " + e.getMessage());
        }

        return rol;
    }
}

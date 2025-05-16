/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
import Models.Rol;
import conexion.ConexionDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
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
    private TextField txtId;

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
        cargarDatosRoles();
        txtId.setEditable(false);
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

    public static boolean guardarRol(Integer id, String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = ConexionDB.conectar()) {
            conn.setAutoCommit(false);

            if (id == null || id == 0) {

                String sqlInsertRol = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?) RETURNING id";
                String sqlSelectPermisos = "SELECT id FROM permiso";
                String sqlInsertRolPermiso = "INSERT INTO rol_permiso (id_rol, id_permiso, stat) VALUES (?, ?, FALSE)";

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

                List<Integer> permisos = new ArrayList<>();
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlSelectPermisos)) {
                    while (rs.next()) {
                        permisos.add(rs.getInt("id"));
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(sqlInsertRolPermiso)) {
                    for (int permisoId : permisos) {
                        stmt.setInt(1, idRol);
                        stmt.setInt(2, permisoId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

            } else {

                String sqlUpdateRol = "UPDATE rol SET nombre = ?, descripcion = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateRol)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, descripcion);
                    stmt.setInt(3, id);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar o actualizar el rol: " + e.getMessage());
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

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ambos campos son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean guardado = RolesController.guardarRol(id, nombre, descripcion);

        if (guardado) {
            JOptionPane.showMessageDialog(null, "Rol guardado correctamente");
            refrescarTablaRoles();
            limpiarCampos(); 
        } else {
            JOptionPane.showMessageDialog(null, "Hubo un error al guardar el rol",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
    }

    @FXML
    private void btnEditarAction(ActionEvent event) {
        // Obtener el rol seleccionado de la tabla
        Rol rolSeleccionado = tblRoles.getSelectionModel().getSelectedItem();

        if (rolSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Por favor selecciona un rol de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = rolSeleccionado.getId(); // Asegúrate de tener un método getId() en Rol

        // Aquí llamas a tu función que busca en la BD por ID
        Rol rolBD = RolesController.buscarRolPorId(id); // Debes tener este método

        if (rolBD != null) {
            // Cargar los datos a los campos de texto
            txtId.setText(String.valueOf(rolBD.getId()));
            txtNombre.setText(rolBD.getNombre());
            txtDescripcion.setText(rolBD.getDescripcion());
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el rol en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Rol buscarRolPorId(int id) {
        Rol rol = null;
        String sql = "SELECT id, nombre, descripcion FROM rol WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rol = new Rol(); // Usamos constructor vacío
                rol.setId(rs.getInt("id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar rol por ID: " + e.getMessage());
        }

        return rol;
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

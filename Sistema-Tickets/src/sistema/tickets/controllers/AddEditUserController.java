/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Administrador;
import Models.ItemComboBox;
import Models.Persona;
import Models.Rol;
import Models.Tecnico;
import Models.Usuario;
import conexion.ConexionDB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddEditUserController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cargarRoles();
        cargarEmpresas();
        cargarDepartamentos();
        txtId.setEditable(false);
        cmbDepartamento.setVisible(false);
        lblDepartamento.setVisible(false);

        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getId() == 9) { // ID del rol Técnico
                cmbDepartamento.setVisible(true);
                lblDepartamento.setVisible(true);
            } else {
                cmbDepartamento.setVisible(false);
                lblDepartamento.setVisible(false);
            }
        });

        // Si cmbRol ya tiene un valor seleccionado al cargar:
        if (cmbRol.getValue() != null && cmbRol.getValue().getId() == 9) {
            cmbDepartamento.setVisible(true);
            lblDepartamento.setVisible(true);
        }
    }

 

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCorreo;

    @FXML
    private ComboBox<ItemComboBox> cmbEmpresa;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<ItemComboBox> cmbRol;

    @FXML
    private ComboBox<ItemComboBox> cmbDepartamento;

    @FXML
    private Text lblDepartamento;

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Users.fxml");
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
    private void btnActionSave(ActionEvent event) {
        try {
            String idTexto = txtId.getText().trim();
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String user = txtUsuario.getText();
            String password = txtPassword.getText();

            ItemComboBox itemEmpresa = cmbEmpresa.getValue();
            ItemComboBox itemRol = cmbRol.getValue();
            ItemComboBox itemDepartamento = cmbDepartamento.getValue();

            if (itemEmpresa == null || itemRol == null) {
                mostrarAlerta("Validación", "Debe seleccionar empresa y rol");
                return;
            }

            int idEmpresa = itemEmpresa.getId();
            int idRol = itemRol.getId();

            if (nombre == null || nombre.trim().length() < 3 || nombre.length() > 100) {
                mostrarAlerta("Validación", "El nombre debe contener entre 3 y 100 caracteres.");
                return;
            }

            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                mostrarAlerta("Validación", "El correo electrónico no es válido.");
                return;
            }

            if (!idTexto.isEmpty() && !esCorreoUnico(correo, Integer.parseInt(idTexto))) {
                mostrarAlerta("Validación", "El correo electrónico ya está en uso.");
                return;
            } else if (idTexto.isEmpty() && !esCorreoUnico(correo, null)) {
                mostrarAlerta("Validación", "El correo electrónico ya está en uso.");
                return;
            }

            if (user == null || user.length() < 5 || user.length() > 30) {
                mostrarAlerta("Validación", "El nombre de usuario debe tener entre 5 y 30 caracteres.");
                return;
            }

            if (!idTexto.isEmpty() && !esUsuarioUnico(user, Integer.parseInt(idTexto))) {
                mostrarAlerta("Validación", "El nombre de usuario ya está en uso.");
                return;
            } else if (idTexto.isEmpty() && !esUsuarioUnico(user, null)) {
                mostrarAlerta("Validación", "El nombre de usuario ya está en uso.");
                return;
            }

            if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                mostrarAlerta("Validación", "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial.");
                return;
            }

            Integer idDepartamento = null;
            if (idRol == 9) {
                if (itemDepartamento == null) {
                    mostrarAlerta("Validación", "Debe seleccionar un departamento para el técnico");
                    return;
                }
                idDepartamento = itemDepartamento.getId();
            }

            Rol rol = new Rol();
            rol.setId(idRol);

            Persona persona;

            if (!idTexto.isEmpty()) {
                int id = Integer.parseInt(idTexto);
                persona = Persona.buscarPorId(id);
                if (persona == null) {
                    mostrarAlerta("Error", "No se encontró el usuario con ID: " + id);
                    return;
                }
            } else {
                persona = PersonaBuilder.crearPersona(rol, idDepartamento);
            }

            persona.setNombre(nombre);
            persona.setCorreo(correo);
            persona.setUser(user);
            persona.setContraseña(password);
            persona.setIdEmpresa(idEmpresa);
            persona.setRol(rol);

            if (persona instanceof Tecnico) {
                ((Tecnico) persona).setIdDepartamento(idDepartamento);
            }

            if (!idTexto.isEmpty()) {
                persona.setId(Integer.parseInt(idTexto));
            }

            persona.guardar();

            mostrarAlerta("Éxito", idTexto.isEmpty() ? "Usuario creado correctamente" : "Usuario actualizado correctamente");
            limpiarCampos();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Error al guardar el usuario: " + ex.getMessage());
        }
    }

    private boolean esCorreoUnico(String correo, Integer idActual) {
        String sql = "SELECT COUNT(*) FROM persona WHERE correo = ? " + (idActual != null ? "AND id != ?" : "");
        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            if (idActual != null) {
                stmt.setInt(2, idActual);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean esUsuarioUnico(String user, Integer idActual) {
        String sql = "SELECT COUNT(*) FROM persona WHERE \"user\" = ? " + (idActual != null ? "AND id != ?" : "");
        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user);
            if (idActual != null) {
                stmt.setInt(2, idActual);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtUsuario.clear();
        txtPassword.clear();
        cmbEmpresa.getSelectionModel().clearSelection();
        cmbRol.getSelectionModel().clearSelection();
        cmbDepartamento.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarRoles() {
        String sql = "SELECT id, nombre FROM rol";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                cmbRol.getItems().add(new ItemComboBox(id, nombre));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    private void cargarEmpresas() {
        String sql = "SELECT id, nombre FROM empresa";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                cmbEmpresa.getItems().add(new ItemComboBox(id, nombre));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    private void cargarDepartamentos() {
        String sql = "SELECT id, nombre FROM departamento";
        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                cmbDepartamento.getItems().add(new ItemComboBox(id, nombre));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar roles: " + e.getMessage());
        }
    }

    private ItemComboBox buscarItemPorTexto(ComboBox<ItemComboBox> comboBox, String texto) {
        for (ItemComboBox item : comboBox.getItems()) {
            if (item.getTexto().equalsIgnoreCase(texto)) {
                return item;
            }
        }
        return null;
    }

    private Persona usuarioActual;

    public void setUsuario(Persona usuario) {
        this.usuarioActual = usuario;

        txtId.setText(String.valueOf(usuario.getId()));
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtUsuario.setText(usuario.getUser());
        txtPassword.setText(usuario.getContraseña());

        cmbRol.setValue(buscarItemPorTexto(cmbRol, usuario.getRol().getNombre()));
        cmbEmpresa.setValue(buscarItemPorTexto(cmbEmpresa, usuario.getEmpresa().getNombre()));

        if (usuario instanceof Tecnico tecnico) {
            if (tecnico.getIdDepartamento() > 0) {
                cmbDepartamento.setValue(
                        buscarItemPorTexto(cmbDepartamento, tecnico.getNombreDepartamento())
                );
            } else {
                cmbDepartamento.setValue(null);
            }
        } else {
            cmbDepartamento.setValue(null);
        }
    }

    private ItemComboBox buscarItemPorId(ComboBox<ItemComboBox> comboBox, int id) {
        for (ItemComboBox item : comboBox.getItems()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

}

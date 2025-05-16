/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Administrador;
import Models.Empresa;
import Models.Persona;
import Models.Rol;
import Models.Tecnico;
import Models.Usuario;
import conexion.ConexionDB;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
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
public class UsersController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTodosUsuarios();
        configurarColumnas();

    }

    @FXML
    private TableView<Persona> tblUsuarios;

    @FXML
    private TableColumn<Persona, Integer> colId;
    @FXML
    private TableColumn<Persona, String> colNombre;
    @FXML
    private TableColumn<Persona, String> colCorreo;
    @FXML
    private TableColumn<Persona, String> colUsuario;
    @FXML
    private TableColumn<Persona, String> colPassword;
    @FXML
    private TableColumn<Persona, String> colRol;
    @FXML
    private TableColumn<Persona, String> colDepartamento;

    @FXML
    private TableColumn<Persona, String> colStatus;

    @FXML
    private void btnAddAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/AddEditUser.fxml");

    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.volverAlMenu();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("user"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("contraseña"));
        colStatus.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getEstadoTexto()));

        colRol.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getRol().getNombre()));

        colDepartamento.setCellValueFactory(cellData -> {
            Persona persona = cellData.getValue();
            if (persona instanceof Tecnico) {
                return new SimpleStringProperty(((Tecnico) persona).getNombreDepartamento());
            }
            return new SimpleStringProperty("N/A");
        });
    }

    private void cargarTodosUsuarios() {
        ObservableList<Persona> listaUsuarios = FXCollections.observableArrayList();
        // Modificar la consulta SQL para incluir la tabla empresa
        String sql = "SELECT u.id AS idPersona, u.id_rol, u.nombre, u.correo, u.user, u.contrasenia, u.stat AS estado, "
                + "r.id AS idRol, r.nombre AS nombreRol, "
                + "d.id AS idDepartamento, d.nombre AS nombreDepartamento, "
                + "e.id AS idEmpresa, e.nombre AS nombreEmpresa "
                + "FROM persona u "
                + "JOIN rol r ON u.id_rol = r.id "
                + "LEFT JOIN departamento d ON u.id_departamento = d.id "
                + "LEFT JOIN empresa e ON u.id_empresa = e.id";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Determinar el tipo de persona basado en el rol
                Persona usuario;
                int idRol = rs.getInt("idRol");
                if (idRol == 8) {
                    usuario = new Administrador();
                } else if (idRol == 9) {
                    Tecnico tecnico = new Tecnico();
                    tecnico.setIdDepartamento(rs.getInt("idDepartamento"));
                    tecnico.setNombreDepartamento(rs.getString("nombreDepartamento"));
                    usuario = tecnico;
                } else {
                    usuario = new Usuario();
                }

                usuario.setId(rs.getInt("idPersona"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setUser(rs.getString("user"));
                usuario.setContraseña(rs.getString("contrasenia"));
                usuario.setIdRol(idRol);
                usuario.setStat(rs.getBoolean("estado"));

                Rol rolUsuario = new Rol();
                rolUsuario.setId(idRol);
                rolUsuario.setNombre(rs.getString("nombreRol"));
                usuario.setRol(rolUsuario);

                int idEmpresa = rs.getInt("idEmpresa");
                if (!rs.wasNull()) {
                    Empresa empresa = new Empresa();
                    empresa.setId(idEmpresa);
                    empresa.setNombre(rs.getString("nombreEmpresa"));
                    usuario.setEmpresa(empresa);
                    System.out.println("Usuario " + usuario.getId() + " asignado a empresa: " + empresa.getNombre());
                } else {
                    System.out.println("Usuario " + usuario.getId() + " no tiene empresa asignada");
                }

                listaUsuarios.add(usuario);
            }
            tblUsuarios.setItems(listaUsuarios);
        } catch (SQLException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btnActionDeleteUser(ActionEvent event) {
        Persona seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro de que desea desactivar al usuario seleccionado?");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                desactivarUsuario(seleccionado.getId());
                cargarTodosUsuarios();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Por favor seleccione un usuario de la tabla.");
            alert.showAndWait();
        }
    }

    private void desactivarUsuario(int idUsuario) {
        String sql = "UPDATE persona SET stat = false WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            int filasActualizadas = pstmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Usuario desactivado exitosamente.");
            } else {
                System.out.println("No se encontró el usuario a desactivar.");
            }

        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
      @FXML
    private void btnActionActivarUsuario(ActionEvent event) {
        Persona seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro de que desea activar al usuario seleccionado?");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                activarUsuario(seleccionado.getId());
                cargarTodosUsuarios();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Por favor seleccione un usuario de la tabla.");
            alert.showAndWait();
        }
    }
    
    private void activarUsuario(int idUsuario) {
        String sql = "UPDATE persona SET stat = true WHERE id = ?";

        try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            int filasActualizadas = pstmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Usuario activado exitosamente.");
            } else {
                System.out.println("No se encontró el usuario a desactivar.");
            }

        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
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

    // EDITAR USUARIOS
    @FXML
    private void btnEditUserAction(ActionEvent event) {
        Persona seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarVistaEditarUsuario(seleccionado);
        } else {
            // Opcional: mostrar una alerta si no hay selección
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Por favor seleccione un usuario de la tabla.");
            alert.showAndWait();
        }
    }

    private void mostrarVistaEditarUsuario(Persona usuario) {
        AddEditUserController controller = Navegador.mostrarVistaCentralConControlador("/sistema/tickets/views/AddEditUser.fxml");
        if (controller != null) {
            controller.setUsuario(usuario);
        }
    }

}

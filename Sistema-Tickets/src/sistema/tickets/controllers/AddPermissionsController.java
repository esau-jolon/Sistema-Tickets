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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddPermissionsController implements Initializable {

       @FXML private TableView<Permiso> tblPermisos;
    @FXML private TableColumn<Permiso, Boolean> colSeleccion;
    @FXML private TableColumn<Permiso, String> colPermiso;
    @FXML private TableColumn<Permiso, String> colDescripcion;
    @FXML private TableColumn<Permiso, Integer> colId;
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración completamente personalizada para el CheckBox
        colSeleccion.setCellValueFactory(new PropertyValueFactory<>("asignado"));
        colSeleccion.setCellFactory(createCheckBoxCellFactory());
        
        // Columnas normales
        colPermiso.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Mejorar el rendimiento de la tabla
        tblPermisos.setRowFactory(tv -> {
            TableRow<Permiso> row = new TableRow<>();
            row.setCache(true);
            return row;
        });
        
        // Añadir un manejador específico para el evento de scroll
        tblPermisos.addEventFilter(ScrollEvent.ANY, event -> {
            // Permitir que el evento continúe, pero asegurar que no haya conflictos
            tblPermisos.requestFocus();
        });
        
        // Esto puede ayudar a resolver problemas de repintado
        tblPermisos.setFixedCellSize(30); // Ajustar al tamaño adecuado
    }

  

    public void setIdRol(int id) {
        txtId.setText(String.valueOf(id));
        cargarPermisosDeRolDesdeTexto();
    }

    public void setNombreRol(String nombre) {
        txtNombre.setText(nombre);
    }

    @FXML
    private void btnActionGuardar(ActionEvent event) {

    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Roles.fxml");
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


    
    // Método personalizado para crear un CellFactory de CheckBox que evite problemas
    private Callback<TableColumn<Permiso, Boolean>, TableCell<Permiso, Boolean>> createCheckBoxCellFactory() {
        return column -> new TableCell<Permiso, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            {
                // Configurar el CheckBox
                checkBox.setAlignment(Pos.CENTER);
                setAlignment(Pos.CENTER);
                
                // Manejar el clic directamente
                checkBox.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        Permiso permiso = getTableRow().getItem();
                        permiso.setAsignado(checkBox.isSelected());
                        event.consume(); // Importante para evitar propagación
                    }
                });
            }
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Permiso permiso = getTableRow().getItem();
                    checkBox.setSelected(permiso.isAsignado());
                    setGraphic(checkBox);
                }
            }
        };
    }
    

    
    private void cargarPermisosDeRolDesdeTexto() {
        if (txtId.getText() == null || txtId.getText().trim().isEmpty()) {
            System.err.println("El campo ID está vacío.");
            return;
        }
        
        int idRol;
        try {
            idRol = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException e) {
            System.err.println("ID no válido: " + txtId.getText());
            return;
        }
        
        ObservableList<Permiso> listaPermisos = FXCollections.observableArrayList();
        String sql = """
        SELECT
            rp.id_rol,
            rp.id_permiso,
            rp.stat,
            p.nombre,
            p.descripcion
        FROM
            rol_permiso rp
        JOIN
            permiso p ON rp.id_permiso = p.id
        WHERE
            rp.id_rol = ?
        """;
        
        try (Connection conn = ConexionDB.conectar(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRol);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permiso permiso = new Permiso();
                permiso.setId(rs.getInt("id_permiso"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                permiso.setAsignado(rs.getBoolean("stat"));
                listaPermisos.add(permiso);
            }
            
            // Configurar los items en el hilo de la UI
            Platform.runLater(() -> {
                // Limpiar primero para evitar conflictos
                tblPermisos.getItems().clear();
                tblPermisos.refresh();
                
                // Establecer los nuevos items
                tblPermisos.setItems(listaPermisos);
                
                // Ajustar el ScrollBar después de cargar datos
                configurarScrollBar();
            });
            
        } catch (SQLException e) {
            System.err.println("Error al cargar permisos del rol: " + e.getMessage());
        }
    }
    
    private void configurarScrollBar() {
        Platform.runLater(() -> {
            ScrollBar verticalBar = null;
            for (Node node : tblPermisos.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL) {
                    verticalBar = (ScrollBar) node;
                }
            }
            
            if (verticalBar != null) {
                verticalBar.setUnitIncrement(10);
                verticalBar.setBlockIncrement(50);
                
                // Añadir listener para debugging
                verticalBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                    System.out.println("ScrollBar value: " + newVal);
                });
                
                // Forzar repintado del ScrollBar
                verticalBar.setVisible(false);
                verticalBar.setVisible(true);
            } else {
                System.out.println("ScrollBar no encontrado");
            }
        });
    }
}

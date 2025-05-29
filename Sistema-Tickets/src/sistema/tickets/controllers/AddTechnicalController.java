/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Departamento;
import Models.Persona;
import Models.Tecnico;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import java.sql.Connection;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class AddTechnicalController implements Initializable {

    @FXML
    private TableView<Persona> tblTecnicos;

    @FXML
    private TableColumn<Persona, Integer> colId;
    @FXML
    private TableColumn<Persona, String> colNombre;

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField txtDepartamento;
    @FXML
    private TextField txtIdDepartamento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIdDepartamento.setVisible(false);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Departments.fxml");
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

    public void setDatosDepartamento(int id, String nombre) {
        txtIdDepartamento.setText(String.valueOf(id));
        txtDepartamento.setText(nombre);
        cargarTecnicosPorDepartamento();
    }

    private void cargarTecnicosPorDepartamento() {
        ObservableList<Persona> listaTecnicos = FXCollections.observableArrayList();
        String idTexto = txtIdDepartamento.getText();

        if (idTexto == null || idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID de departamento.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idDepartamento = Integer.parseInt(idTexto);

            String sql = "SELECT id, nombre FROM persona WHERE id_departamento = ? AND id_rol = 9";

            try (Connection conn = ConexionDB.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idDepartamento);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Persona tecnico = new Tecnico(); // Puedes usar `Tecnico` si lo prefieres
                        tecnico.setId(rs.getInt("id"));
                        tecnico.setNombre(rs.getString("nombre"));

                        listaTecnicos.add(tecnico);
                    }
                }
            }

            tblTecnicos.setItems(listaTecnicos);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar técnicos.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}

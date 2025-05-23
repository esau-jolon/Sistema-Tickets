/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.ItemComboBox;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class ActionsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private TextField txtNombreFlujo;
    @FXML
    private TextField txtIdFlujo;

    public void inicializarDatos(String idFlujo, String nombreFlujo) {
        txtIdFlujo.setText(idFlujo);
        txtNombreFlujo.setText(nombreFlujo);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/AddWorkflow.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar los datos
            AddWorkflowController controller = loader.getController();
            int id = Integer.parseInt(txtIdFlujo.getText());
            controller.setDatosFlujo(id, txtNombreFlujo.getText());

            // Mostrar en el centro del BorderPane
            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("ID de flujo inválido: " + txtIdFlujo.getText());
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
}

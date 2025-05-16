/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

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
import javafx.scene.control.Button; // Keep only this Button import
import javafx.scene.input.MouseEvent; // 
import javafx.stage.Stage;
import sistema.tickets.Navegador;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class MenuController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnParameters;

    @FXML
    private Button btnRolesAction;

    @FXML
    private Button btnPermisos;

    @FXML
    private Button btnDepartamentos;

    @FXML
    private Button btnStatus;

    @FXML
    private Button btnMisTIckets;

    @FXML
    private Button btnResolverTicket;

    @FXML
    private Button btnFlujos;

    @FXML
    private void btnUsersAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Users.fxml");
    }

    @FXML
    private void btnRolesAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Roles.fxml");
    }

    @FXML
    private void btnDepartamentosAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Departments.fxml");
    }

    @FXML
    private void btnParametersAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Parameters.fxml");
    }

    @FXML
    private void btnPermisosAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Permissions.fxml");
    }

    @FXML
    private void btnStatusAction(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/TicketStatus.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnMisTIcketsAction(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/MisTickets.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnResolverTicketAction(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/ServicingTicket.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnFlujosAction(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/Workflow.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnLogOutAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Login.fxml");
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

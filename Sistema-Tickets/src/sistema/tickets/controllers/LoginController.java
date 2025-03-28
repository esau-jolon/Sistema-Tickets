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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    

@FXML
private void btnSesionAction(ActionEvent event) {
    try {
        // Cargar la nueva vista
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/Menu.fxml"));
        Parent root = loader.load();

        // Crear una nueva ventana (Stage)
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}



}

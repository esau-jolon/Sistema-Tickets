/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class MainLayoutController {

    @FXML
    private AnchorPane contenidoCentral;

    public void cargarVista(String rutaFXML) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource(rutaFXML));
            contenidoCentral.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T cargarVistaConControlador(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Node vista = loader.load();
            contenidoCentral.getChildren().setAll(vista);
            return loader.getController(); // Retorna el controlador
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

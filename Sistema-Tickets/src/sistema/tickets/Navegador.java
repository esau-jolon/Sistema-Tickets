/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.tickets;

/**
 *
 * @author esauj
 */
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sistema.tickets.controllers.MainLayoutController;

public class Navegador {

    private static MainLayoutController controlador;

    public static void setControlador(MainLayoutController ctrl) {
        controlador = ctrl;
    }

    public static void mostrarVistaCentral(String rutaFXML) {
        if (controlador != null) {
            controlador.cargarVista(rutaFXML);
        }
    }

    public static void volverAlMenu() {
        mostrarVistaCentral("/sistema/tickets/views/Menu.fxml");
    }

    public static <T> T mostrarVistaCentralConControlador(String rutaFXML) {
        if (controlador != null) {
            return controlador.cargarVistaConControlador(rutaFXML);
        }
        return null;
    }
}

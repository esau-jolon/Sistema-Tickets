package sistema.tickets;

import static conexion.ConexionDB.conectar;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import sistema.tickets.controllers.MainLayoutController;

/*
public class SistemaTickets extends Application {

    public static void main(String[] args) {
        conectar(); // Conexión DB
        launch(args); // Inicia JavaFX
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Navegador.setPrimaryStage(primaryStage); // Guardamos el Stage principal
        Navegador.mostrarVista("/sistema/tickets/views/Login.fxml", "Login");
    }
}
 */

public class SistemaTickets extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/MainLayout.fxml"));
        Parent root = loader.load();

        // Obtener el controlador principal
        MainLayoutController controlador = loader.getController();
        
        // 👉 ESTO ES LO QUE FALTABA:
        Navegador.setControlador(controlador);

        // Cargar la vista inicial (login)
        controlador.cargarVista("/sistema/tickets/views/Login.fxml");

        primaryStage.setTitle("Sistema de Tickets");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        conectar(); // Conexión a base de datos
        launch(args);
    }
}

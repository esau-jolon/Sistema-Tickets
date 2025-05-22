package sistema.tickets;

import static conexion.ConexionDB.conectar;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
 import javafx.scene.image.Image ;
import javafx.stage.Stage;
import sistema.tickets.controllers.MainLayoutController;

public class SistemaTickets extends Application {

    

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/MainLayout.fxml"));
        Parent root = loader.load();

      
        MainLayoutController controlador = loader.getController();

     
        Navegador.setControlador(controlador);

 
        controlador.cargarVista("/sistema/tickets/views/Login.fxml");


        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/sistema/tickets/images/taskflow.png")));

        primaryStage.setTitle("Sistema de Tickets");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        conectar();
        launch(args);
    }
}

package sistema.tickets;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SistemaTickets extends Application {

    public static void main(String[] args) {
        launch(args); // Inicia la aplicación JavaFX
    }

@Override
public void start(Stage primaryStage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/sistema/tickets/views/Login.fxml"));

    primaryStage.setTitle("Login");
    Scene scene = new Scene(root, 900, 600); // Ajusta el tamaño (ancho x alto)
    primaryStage.setScene(scene);
    primaryStage.show();
}

}

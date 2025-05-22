/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import conexion.ConexionDB;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sistema.tickets.Navegador;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Base64;
import javafx.scene.image.Image;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.time.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class ParametersController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setEditable(false);
        txtId.setVisible(false);
        cmbIdioma.getItems().addAll("es", "en"); // idiomas posibles
        cmbHorario.getItems().addAll("America/Guatemala", "America/Mexico_City", "UTC");
        spinnerDias.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 30));
        cargarDatosEmpresa();

    }

    private String logoBase64;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtId;
    @FXML
    private ComboBox<String> cmbIdioma;

    @FXML
    private ComboBox<String> cmbHorario;

    @FXML
    private TextField txtVencimiento;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Spinner<Integer> spinnerDias;

    @FXML
    private void btnCloseAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Menu.fxml");
    }

    @FXML
    private void btnGuardarAction(ActionEvent event) {
        String nombre = txtNombre.getText();
        String idioma = cmbIdioma.getValue();
        String zonaHoraria = cmbHorario.getValue();
        int dias = spinnerDias.getValue();

        String tiempoExpStr = dias + " days";

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Nombre requerido",
                    "Por favor, ingrese el nombre de la empresa.");
            return;
        }

        String logoData = null;
        if (logoBase64 != null && !logoBase64.isEmpty()) {
            String extension = "";
            String url = imgLogo.getImage().getUrl();
            if (url != null) {
                if (url.toLowerCase().endsWith(".png")) {
                    extension = "png";
                } else if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg")) {
                    extension = "jpeg";
                }
            }
            logoData = "data:image/" + extension + ";base64," + logoBase64;
        }

        try (Connection conn = ConexionDB.conectar()) {
            int id = Integer.parseInt(txtId.getText());
            String checkSql = "SELECT COUNT(*) FROM empresa WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean existe = rs.getInt(1) > 0;

                if (existe) {

                    String updateSql = "UPDATE empresa SET nombre = ?, logo = ?, idioma = ?, zona_horaria = ?, tiempo_expiracion = ?::interval WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setString(1, nombre);
                        pstmt.setString(2, logoData);
                        pstmt.setString(3, idioma);
                        pstmt.setString(4, zonaHoraria);
                        pstmt.setString(5, tiempoExpStr);
                        pstmt.setInt(6, id);

                        int filas = pstmt.executeUpdate();
                        if (filas > 0) {
                            System.out.println("Empresa actualizada correctamente.");
                            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empresa actualizada",
                                    "Los datos de la empresa han sido actualizados correctamente.");
                        }
                    }
                } else {

                    String insertSql = "INSERT INTO empresa (id, nombre, logo, idioma, zona_horaria, tiempo_expiracion) VALUES (?, ?, ?, ?, ?, ?::interval)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setInt(1, id);
                        pstmt.setString(2, nombre);
                        pstmt.setString(3, logoData);
                        pstmt.setString(4, idioma);
                        pstmt.setString(5, zonaHoraria);
                        pstmt.setString(6, tiempoExpStr);

                        int filas = pstmt.executeUpdate();
                        if (filas > 0) {
                            System.out.println("Empresa registrada correctamente.");
                            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empresa registrada",
                                    "Los datos de la empresa han sido registrados correctamente.");
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("ID inválido (no es un número): " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "ID inválido",
                    "El ID proporcionado no es un número válido: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al guardar datos de empresa: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en la base de datos",
                    "Error al guardar los datos de la empresa: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void btnPriorityAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/tickets/views/PriorityLevels.fxml"));
            Node root = loader.load();

            // Obtener el controlador de la vista cargada
            PriorityLevelsController controller = loader.getController();

            // Pasar el ID actual
            String idEmpresa = txtId.getText();
            controller.setIdEmpresa(idEmpresa);

            // Mostrar la vista con datos pasados
            Navegador.mostrarVistaCentral(root);

        } catch (IOException e) {
            e.printStackTrace();
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

    private void cargarDatosEmpresa() {
        String sql = "SELECT id, nombre, logo, idioma, zona_horaria, tiempo_expiracion FROM empresa LIMIT 1";

        try (Connection conn = ConexionDB.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {

                txtId.setText(rs.getString("id"));
                txtNombre.setText(rs.getString("nombre"));

                cmbIdioma.setValue(rs.getString("idioma"));
                cmbHorario.setValue(rs.getString("zona_horaria"));

                // Tiempo de expiración (formato "30 days")
                String tiempoExp = rs.getString("tiempo_expiracion");
                if (tiempoExp != null && !tiempoExp.isEmpty()) {
                    String[] partes = tiempoExp.split(" ");
                    if (partes.length > 0) {
                        try {
                            int dias = Integer.parseInt(partes[0]);
                            spinnerDias.getValueFactory().setValue(dias);
                        } catch (NumberFormatException e) {
                            System.err.println("No se pudo convertir el intervalo a días: " + e.getMessage());
                        }
                    }
                }

                // Logo
                String logoData = rs.getString("logo");
                if (logoData != null && !logoData.isEmpty()) {
                    try {
                        String base64Image;

                        if (logoData.startsWith("data:image")) {
                            // Si tiene encabezado, quitarlo
                            base64Image = logoData.substring(logoData.indexOf(",") + 1);
                        } else {
                            // Asumir que es solo base64
                            base64Image = logoData;
                        }

                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        imgLogo.setImage(image);

                    } catch (IllegalArgumentException e) {
                        System.err.println("Error al decodificar la imagen base64: " + e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar datos de empresa: " + e.getMessage());
        }
    }

    @FXML
    private void btnAgregarImagenAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");

        // Filtrar imágenes JPG y PNG únicamente
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes JPG y PNG", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {

            long fileSizeInBytes = file.length();
            long maxSizeInBytes = 2 * 1024 * 1024;

            if (fileSizeInBytes > maxSizeInBytes) {
                JOptionPane.showMessageDialog(null,
                        "El archivo excede el tamaño máximo permitido de 2MB.",
                        "Tamaño excedido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombreArchivo = file.getName().toLowerCase();
            if (!nombreArchivo.endsWith(".jpg") && !nombreArchivo.endsWith(".jpeg") && !nombreArchivo.endsWith(".png")) {
                JOptionPane.showMessageDialog(null,
                        "Formato no permitido. Solo se aceptan imágenes JPG o PNG.",
                        "Formato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Image image = new Image(file.toURI().toString());
            imgLogo.setImage(image);

            logoBase64 = convertirImagenABase64(file);
        }
    }

    private String convertirImagenABase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            System.err.println("Error al convertir imagen a Base64: " + e.getMessage());
            return null;
        }
    }

}

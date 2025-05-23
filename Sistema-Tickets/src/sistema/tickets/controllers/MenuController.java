/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Permiso;
import Models.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sistema.tickets.Navegador;
import sistema.tickets.PermisosRequeridos;

/**
 * FXML Controller class
 *
 * @author esauj
 */
public class MenuController implements Initializable, PermisosRequeridos {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnUsers.setUserData("19");
        btnParameters.setUserData("22");
        btnRoles.setUserData("30");
        btnPermisos.setUserData("23");
        btnDepartamentos.setUserData("20");
        btnStatus.setUserData("28");
        btnMisTIckets.setUserData("31");
        btnResolverTicket.setUserData("29");
        btnFlujos.setUserData("21");
        btnGestion.setUserData("27");

        lblUsers.setUserData("19");
        lblParametros.setUserData("22");
        lblRoles.setUserData("30");
        lblPermisos.setUserData("23");
        lblDepartamentos.setUserData("20");
        lblEstados.setUserData("28");
        lblMisTickets.setUserData("31");
        lblResolver.setUserData("29");
        lblFlujos.setUserData("21");
        lblGestionTickets.setUserData("27");

        List<Permiso> permisosGuardados = Sesion.getPermisos();
        if (permisosGuardados != null && !permisosGuardados.isEmpty()) {
            aplicarPermisos(permisosGuardados);
        }

    }

    @Override
    public void aplicarPermisos(List<Permiso> permisos) {
        Set<String> idsPermitidos = permisos.stream()
                .map(p -> String.valueOf(p.getId()))
                .collect(Collectors.toSet());

        for (Node node : List.of(
                btnUsers, btnParameters, btnRoles,
                btnPermisos, btnDepartamentos, btnStatus, btnMisTIckets,
                btnResolverTicket, btnFlujos, btnGestion,
                lblUsers, lblParametros, lblRoles, lblPermisos,
                lblDepartamentos, lblGestionTickets, lblEstados, lblFlujos,
                lblResolver, lblMisTickets
        )) {

            Object id = node.getUserData();
            if (id != null && !idsPermitidos.contains(id.toString())) {
                // Eliminar el nodo del layout si no tiene permiso
                if (node.getParent() instanceof Pane pane) {
                    pane.getChildren().remove(node);
                }
            }
        }
    }

    @FXML
    private Text lblUsers;
    @FXML
    private Text lblParametros;
    @FXML
    private Text lblRoles;
    @FXML
    private Text lblPermisos;
    @FXML
    private Text lblDepartamentos;
    @FXML
    private Text lblGestionTickets;
    @FXML
    private Text lblEstados;
    @FXML
    private Text lblFlujos;
    @FXML
    private Text lblResolver;

    @FXML
    private Text lblMisTickets;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnParameters;

    @FXML
    private Button btnRoles;
    @FXML
    private Button btnGestion;

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
    private void btnGestionAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/GestionTickets.fxml");
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

        Navegador.mostrarVistaCentral("/sistema/tickets/views/TicketStatus.fxml");
    }

    @FXML
    private void btnMisTIcketsAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/MisTickets.fxml");
    }

    @FXML
    private void btnResolverTicketAction(ActionEvent event) {

        Navegador.mostrarVistaCentral("/sistema/tickets/views/ServicingTicket.fxml");
    }

    @FXML
    private void btnFlujosAction(ActionEvent event) {
        Navegador.mostrarVistaCentral("/sistema/tickets/views/Workflow.fxml");
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

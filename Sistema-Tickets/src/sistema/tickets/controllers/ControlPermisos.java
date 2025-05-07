/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.tickets.controllers;

/**
 *
 * @author esauj
 */

import javafx.scene.Node;
import javafx.scene.Parent;
import Models.SesionActual;

public class ControlPermisos {

    public static void aplicarPermisos(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            Object permisoId = node.getUserData();

            if (permisoId instanceof Integer) {
                boolean visible = SesionActual.tienePermiso((Integer) permisoId);
                node.setVisible(visible);
                node.setManaged(visible); 
            }

            // Recursividad si es contenedor
            if (node instanceof Parent) {
                aplicarPermisos((Parent) node);
            }
        }
    }
}


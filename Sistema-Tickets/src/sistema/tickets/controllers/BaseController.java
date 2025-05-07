/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.tickets.controllers;

import javafx.scene.Parent;

/**
 *
 * @author esauj
 */
public abstract class BaseController {
    public void aplicarPermisos(Parent root) {
        ControlPermisos.aplicarPermisos(root);
    }
}

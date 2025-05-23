/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author esauj
 */
public class PersonaSistema extends Persona {

    @Override
    public void mostrarPerfil() {
        // implementación básica o vacía
        System.out.println("Mostrando perfil del usuario del sistema.");
    }

    @Override
    public void agregarUsuario() {
        // implementación básica o vacía
        System.out.println("Agregando usuario del sistema.");
    }

    @Override
    public void guardar(Connection conexion) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}


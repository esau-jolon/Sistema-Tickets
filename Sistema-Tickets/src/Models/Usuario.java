/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public abstract class Usuario extends Persona {
    
     
    @Override
    public void mostrarPerfil() {
        System.out.println("Perfil Usuario: " + getNombre());
    }
    @Override
    public void agregarUsuario() {
        System.out.println("Usuario agregado: " + getNombre());
    }

    public void crearTicket() {
        System.out.println("Ticket creado por: " + getNombre());
    }

    public void consultarTickets() {
        System.out.println("Consultando tickets de: " + getNombre());
    }
}

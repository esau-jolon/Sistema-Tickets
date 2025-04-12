/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public abstract class Tecnico extends Persona {
    
    private int IdDepartamento;
    
    
    public int getIdDepartamento() {
        return IdDepartamento;
    }

    public void setIdDepartamento(int IdDepartamento) {
        this.IdDepartamento = IdDepartamento;
    }    
    
    @Override
    public void mostrarPerfil() {
        System.out.println("Perfil Técnico: " + getNombre() + ", Departamento: " + IdDepartamento);
    }

    @Override
    public void agregarUsuario() {
        System.out.println("Técnico agregado: " + getNombre());
    }

    public void atenderTickets() { 
        
    }
    
    public void cambiarEstado() {
        
    }
    public void agregarNota() { 
        
    }
    
    public void reAsignarTicket() { 
        
    }
}

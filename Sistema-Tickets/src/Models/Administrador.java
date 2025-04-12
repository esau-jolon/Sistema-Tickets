/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public abstract class Administrador extends Persona {
    
     @Override
    public void mostrarPerfil() {
        System.out.println("Administrador: " + getNombre());
    }

    @Override
    public void agregarUsuario() {
        System.out.println("Administrador agregando usuario...");
    }

    public void gestionarUsuarios() { 
        
    }
    
    public void configurarFlujos() { 
        
    }
    
    public void agregarDepartamentos() { 
        
    }
    
    public void asignarUsuarios() { 
        
    }
    public void agregarEmpresa() { 
        
    }
    
    public void crearRol() { 
        
    }
    public void crearPermisos() { 
        
    }
}

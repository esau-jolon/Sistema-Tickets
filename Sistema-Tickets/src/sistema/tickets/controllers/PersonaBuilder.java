/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.tickets.controllers;

import Models.Administrador;
import Models.Persona;
import Models.Rol;
import Models.Tecnico;
import Models.Usuario;
import java.util.Objects;

/**
 *
 * @author esauj
 */
public class PersonaBuilder {
    
    
    private static final int ID_ADMINISTRADOR = 8;
    private static final int ID_TECNICO = 9;
    private static final int ID_USUARIO = 10;
    
    public static Persona crearPersona(Rol rol, Integer idDepartamento) {
        Objects.requireNonNull(rol, "El rol no puede ser nulo");
        
        Persona persona;
        
        switch (rol.getId()) {
            case ID_ADMINISTRADOR:
                persona = new Administrador();
                break;
            case ID_TECNICO:
                Tecnico tecnico = new Tecnico();
                if (idDepartamento == null) {
                    throw new IllegalArgumentException("Los técnicos requieren departamento");
                }
                tecnico.setIdDepartamento(idDepartamento);
                persona = tecnico;
                break;
            case ID_USUARIO:
                persona = new Usuario();
                break;
            default: 
                persona = new Usuario();
                break;
        }
        
        persona.setIdRol(rol.getId()); 
        return persona;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.util.List;

/**
 *
 * @author esauj
 */
public class Sesion {

    private static Persona usuario;
    private static List<Permiso> permisos;

    public static void setUsuario(Persona u) {
        usuario = u;
    }
    

    public static Persona getUsuario() {
        return usuario;
    }

    public static void setPermisos(List<Permiso> p) {
        permisos = p;
    }

    public static List<Permiso> getPermisos() {
        return permisos;
    }

    public static boolean tienePermiso(int idPermiso) {
        return permisos.stream().anyMatch(p -> p.getId() == idPermiso);
    }
}

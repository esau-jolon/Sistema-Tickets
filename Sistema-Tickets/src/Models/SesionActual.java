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
public class SesionActual {
    public static Usuario usuarioLogueado;
    public static List<Integer> permisosId;

    public static boolean tienePermiso(int idPermiso) {
        return permisosId.contains(idPermiso);
    }
}


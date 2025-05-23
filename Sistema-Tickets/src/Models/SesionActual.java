/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import com.sun.jdi.connect.spi.Connection;
import conexion.ConexionDB;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

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

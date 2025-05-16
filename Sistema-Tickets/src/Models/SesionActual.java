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
/*
    public List<Integer> obtenerPermisosPorRol(int rolId) {
        List<Integer> permisos = new ArrayList<>();
        String sql = "SELECT permiso_id FROM rol_permiso WHERE rol_id = ? AND stat = true";

        try (Connection conn = ConexionDB.(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                permisos.add(rs.getInt("permiso_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // o usa un logger
        }

        return permisos;
    }
*/
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author esauj
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB{
    
    private static final String URL = "jdbc:postgresql://ep-falling-paper-a4s8n6xm-pooler.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_vi0MeaqEYP6y&sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_vi0MeaqEYP6y";
    
    public static Connection conectar(){
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Base de datos conectada con exito!");
            return conn;
        } catch (SQLException e){
            System.err.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }
    
}

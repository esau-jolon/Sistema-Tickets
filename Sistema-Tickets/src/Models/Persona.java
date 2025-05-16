/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Connection;
import conexion.ConexionDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author esauj
 */
public abstract class Persona {

    private int Id;
    private String Nombre;
    private String Correo;
    private String user;
    private int IdRol;
    private int IdEmpresa;
    private String Contraseña;
    private Rol rol;
    private Empresa empresa;
    private boolean stat;

    public boolean isStat() {
        return stat;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }

    public abstract void mostrarPerfil();

    public abstract void agregarUsuario();

    public int getId() {
        return Id;
    }

    public String getEstadoTexto() {
        return stat ? "Activo" : "Inactivo";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.Nombre = nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public int getIdRol() {
        return IdRol;
    }

    public void setIdRol(int IdRol) {
        this.IdRol = IdRol;
    }

    public int getIdEmpresa() {
        return IdEmpresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setIdEmpresa(int IdEmpresa) {
        this.IdEmpresa = IdEmpresa;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String Contraseña) {
        this.Contraseña = Contraseña;
    }

    public boolean tienePermiso(String permisoNombre) {
        if (rol == null || rol.getPermisos() == null) {
            return false;
        }

        return rol.getPermisos().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(permisoNombre));
    }

    public abstract void guardar(Connection conexion) throws SQLException;

    public final void guardar() throws SQLException {
        try (Connection conn = ConexionDB.conectar()) {
            guardar(conn);
        }
    }

    public static Persona buscarPorId(int id) {
        String sql = """
        SELECT u.id AS idPersona, u.id_rol, u.nombre, u.correo, u.user, u.contrasenia,
               r.id AS idRol, r.nombre AS nombreRol,
               d.id AS idDepartamento, d.nombre AS nombreDepartamento,
               e.id AS idEmpresa, e.nombre AS nombreEmpresa
        FROM persona u
        JOIN rol r ON u.id_rol = r.id
        LEFT JOIN departamento d ON u.id_departamento = d.id
        LEFT JOIN empresa e ON u.id_empresa = e.id
        WHERE u.id = ?
    """;

        try (Connection conn = ConexionDB.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idRol = rs.getInt("idRol");
                Rol rol = new Rol();
                rol.setId(idRol);
                rol.setNombre(rs.getString("nombreRol"));

                // Construir la instancia de Persona según el rol
                Persona persona;
                switch (idRol) {
                    case 8: // Administrador
                        persona = new Administrador();
                        break;
                    case 9: // Técnico
                        persona = new Tecnico();
                        int idDepartamento = rs.getInt("idDepartamento");
                        if (!rs.wasNull()) {
                            ((Tecnico) persona).setIdDepartamento(idDepartamento);
                        }
                        break;
                    case 10: // Usuario
                    default:
                        persona = new Usuario();
                        break;
                }

                persona.setId(rs.getInt("idPersona"));
                persona.setNombre(rs.getString("nombre"));
                persona.setCorreo(rs.getString("correo"));
                persona.setUser(rs.getString("user"));
                persona.setContraseña(rs.getString("contrasenia"));
                persona.setIdRol(idRol);
                persona.setRol(rol);
                persona.setIdEmpresa(rs.getInt("idEmpresa"));

                return persona;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}

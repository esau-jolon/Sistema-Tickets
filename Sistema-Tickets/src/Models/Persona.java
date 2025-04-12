/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author esauj
 */
public abstract class Persona {
    
    private int Id;
    private String Nombre;
    private String Correo;
    private int IdRol;
    private int IdEmpresa;
    private String Contraseña;
    
    public abstract void mostrarPerfil();
    public abstract void agregarUsuario();
    
    
    public int getId() { return Id; }
    public void setId(int Id) { this.Id = Id; }

    public String getNombre() { return Nombre; }
    
    public void setNombre(String nombre) {
        if(nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.Nombre = nombre;
    }

    public String getCorreo() { return Correo; }
    public void setCorreo(String Correo) { this.Correo = Correo; }

    public int getIdRol() { return IdRol; }
    public void setIdRol(int IdRol) { this.IdRol = IdRol; }

    public int getIdEmpresa() { return IdEmpresa; }
    public void setIdEmpresa(int IdEmpresa) { this.IdEmpresa = IdEmpresa; }

    public String getContraseña() { return Contraseña; }
    public void setContraseña(String Contraseña) { this.Contraseña = Contraseña; }
    
    
}

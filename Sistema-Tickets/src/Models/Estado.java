package Models;

/**
 *
 * @author esauj
 */
public class Estado {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean estadoFinal;

    // Constructor vacío
    public Estado() {
    }

    // Constructor completo
    public Estado(int id, String nombre, String descripcion, boolean estadoFinal) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoFinal = estadoFinal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(boolean estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

}

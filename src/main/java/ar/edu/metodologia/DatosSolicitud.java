package ar.edu.metodologia;

public class DatosSolicitud {
    private int estudianteId;
    private String tipo;
    private String descripcion;
    private Prioridad prioridad;

    public DatosSolicitud(int estudianteId, String tipo, String descripcion, Prioridad prioridad) {
        this.estudianteId = estudianteId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    public boolean sonValidos() {
        return !tipo.isEmpty() && !descripcion.isEmpty() && prioridad != null;
    }

    public int getEstudianteId() { return estudianteId; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
}

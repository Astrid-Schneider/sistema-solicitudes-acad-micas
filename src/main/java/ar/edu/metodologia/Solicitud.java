package ar.edu.metodologia;

public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private String tipo;
    private String descripcion;
    private String prioridad;
    private String estado;
    private Responsable responsable;

    public Solicitud(int id, Estudiante estudiante, String tipo, String descripcion, String prioridad, String estado, Responsable responsable) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.responsable = responsable;
    }
    public int getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public Responsable getResponsable() { return responsable; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setResponsable(Responsable responsable) { this.responsable = responsable; }
    @Override public String toString() {
        String r = responsable == null ? "Sin asignar" : responsable.getNombre();
        return "Solicitud " + id + " | Estudiante: " + estudiante.getNombre() + " | Tipo: " + tipo
                + " | Descripción: " + descripcion + " | Prioridad: " + prioridad + " | Estado: " + estado
                + " | Responsable: " + r;
    }
}

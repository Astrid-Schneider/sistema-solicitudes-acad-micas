package ar.edu.metodologia;

public class Solicitud {
    private int id;
    private Estudiante estudiante;
    private String tipo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private Responsable responsable;

    public Solicitud(int id, Estudiante estudiante, DatosSolicitud datos) {
        this.id = id;
        this.estudiante = estudiante;
        this.tipo = datos.getTipo();
        this.descripcion = datos.getDescripcion();
        this.prioridad = datos.getPrioridad();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.responsable = null;
    }
    public int getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoSolicitud getEstado() { return estado; }
    public Responsable getResponsable() { return responsable; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public void setResponsable(Responsable responsable) { this.responsable = responsable; }
    @Override public String toString() {
        String r = responsable == null ? "Sin asignar" : responsable.getNombre();
        return "Solicitud " + id + " | Estudiante: " + estudiante.getNombre() + " | Tipo: " + tipo
                + " | Descripción: " + descripcion + " | Prioridad: " + prioridad + " | Estado: " + estado
                + " | Responsable: " + r;
    }
}

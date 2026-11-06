package ar.edu.metodologia;

public enum Prioridad {
    BAJA,
    MEDIA,
    ALTA;

    public static Prioridad desdeTexto(String texto) {
        for (Prioridad prioridad : values()) {
            if (prioridad.name().equals(texto)) {
                return prioridad;
            }
        }
        return null;
    }
}

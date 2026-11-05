package ar.edu.metodologia;

public enum EstadoSolicitud {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA;

    public static EstadoSolicitud desdeTexto(String texto) {
        for (EstadoSolicitud estado : values()) {
            if (estado.name().equals(texto)) {
                return estado;
            }
        }
        return null;
    }
}

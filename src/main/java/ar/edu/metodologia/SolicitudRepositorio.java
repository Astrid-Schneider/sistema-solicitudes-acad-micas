package ar.edu.metodologia;

import java.util.List;

public interface SolicitudRepositorio {
    void guardar(Solicitud solicitud);
    Solicitud buscarPorId(int solicitudId);
    List<Solicitud> listarTodas();
}

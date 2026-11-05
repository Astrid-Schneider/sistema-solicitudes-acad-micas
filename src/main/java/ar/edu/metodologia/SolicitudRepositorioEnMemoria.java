package ar.edu.metodologia;

import java.util.ArrayList;
import java.util.List;

public class SolicitudRepositorioEnMemoria implements SolicitudRepositorio {
    private List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void guardar(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    @Override
    public Solicitud buscarPorId(int solicitudId) {
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == solicitudId) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public List<Solicitud> listarTodas() {
        return solicitudes;
    }
}

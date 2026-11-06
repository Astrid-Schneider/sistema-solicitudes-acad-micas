package ar.edu.metodologia;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SolicitudService {
    private SolicitudRepositorio repositorio;
    private EstudianteService estudianteService;
    private int proximaSolicitud = 1;

    public SolicitudService(SolicitudRepositorio repositorio, EstudianteService estudianteService) {
        this.repositorio = repositorio;
        this.estudianteService = estudianteService;
    }

    public Solicitud registrarSolicitud(DatosSolicitud datos) {
        Estudiante estudianteEncontrado = estudianteService.buscarEstudiantePorId(datos.getEstudianteId());
        if (estudianteEncontrado == null || !datos.sonValidos()) {
            return null;
        }
        Solicitud nuevaSolicitud = new Solicitud(proximaSolicitud, estudianteEncontrado, datos);
        repositorio.guardar(nuevaSolicitud);
        proximaSolicitud++;
        return nuevaSolicitud;
    }

    public Solicitud buscarSolicitudPorId(int solicitudId) {
        return repositorio.buscarPorId(solicitudId);
    }

    public List<Solicitud> listarSolicitudes() {
        return repositorio.listarTodas();
    }

    public List<Solicitud> buscarPorEstado(EstadoSolicitud estadoBuscado) {
        List<Solicitud> encontradas = new ArrayList<>();
        for (Solicitud solicitud : repositorio.listarTodas()) {
            if (solicitud.getEstado() == estadoBuscado) {
                encontradas.add(solicitud);
            }
        }
        return encontradas;
    }

    public List<Solicitud> buscarPorTipo(String tipoBuscado) {
        List<Solicitud> encontradas = new ArrayList<>();
        for (Solicitud solicitud : repositorio.listarTodas()) {
            if (solicitud.getTipo().equals(tipoBuscado)) {
                encontradas.add(solicitud);
            }
        }
        return encontradas;
    }

    public boolean asignarResponsable(int solicitudId, Responsable responsable) {
        Solicitud solicitudEncontrada = repositorio.buscarPorId(solicitudId);
        if (solicitudEncontrada == null || responsable == null) {
            return false;
        }
        solicitudEncontrada.setResponsable(responsable);
        return true;
    }

    public boolean cambiarEstado(int solicitudId, EstadoSolicitud nuevoEstado) {
        Solicitud solicitudEncontrada = repositorio.buscarPorId(solicitudId);
        if (solicitudEncontrada == null || nuevoEstado == null) {
            return false;
        }
        solicitudEncontrada.setEstado(nuevoEstado);
        return true;
    }

    public Map<EstadoSolicitud, Integer> contarPorEstado() {
        Map<EstadoSolicitud, Integer> cantidades = new EnumMap<>(EstadoSolicitud.class);
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            cantidades.put(estado, 0);
        }
        for (Solicitud solicitud : repositorio.listarTodas()) {
            cantidades.put(solicitud.getEstado(), cantidades.get(solicitud.getEstado()) + 1);
        }
        return cantidades;
    }

    public int contarTotal() {
        return repositorio.listarTodas().size();
    }
}

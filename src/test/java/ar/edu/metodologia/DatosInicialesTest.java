package ar.edu.metodologia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprueba que los datos de ejemplo del sistema final coinciden con los de la versión inicial
 * (tag estado-inicial): 3 estudiantes, 2 responsables y 3 solicitudes.
 */
class DatosInicialesTest {

    @Test
    @DisplayName("Los datos iniciales cargan 3 estudiantes, 2 responsables y 3 solicitudes")
    void cargaDatosEsperados() {
        EstudianteService estudianteService = new EstudianteService();
        ResponsableService responsableService = new ResponsableService();
        SolicitudService solicitudService =
                new SolicitudService(new SolicitudRepositorioEnMemoria(), estudianteService);

        DatosIniciales.cargar(estudianteService, responsableService, solicitudService);

        assertEquals(3, estudianteService.listarEstudiantes().size());
        assertEquals(2, responsableService.listarResponsables().size());
        assertEquals(3, solicitudService.contarTotal());

        Solicitud primera = solicitudService.buscarSolicitudPorId(1);
        assertEquals("CERTIFICADO", primera.getTipo());
        assertEquals(EstadoSolicitud.PENDIENTE, primera.getEstado());
        assertNull(primera.getResponsable());

        Solicitud segunda = solicitudService.buscarSolicitudPorId(2);
        assertEquals("EQUIVALENCIA", segunda.getTipo());
        assertEquals(EstadoSolicitud.EN_PROCESO, segunda.getEstado());
        assertEquals("Martín Ruiz", segunda.getResponsable().getNombre());

        Solicitud tercera = solicitudService.buscarSolicitudPorId(3);
        assertEquals("EXAMEN", tercera.getTipo());
        assertEquals(EstadoSolicitud.RESUELTA, tercera.getEstado());
        assertEquals("Laura Gómez", tercera.getResponsable().getNombre());

        Map<EstadoSolicitud, Integer> reporte = solicitudService.contarPorEstado();
        assertEquals(1, reporte.get(EstadoSolicitud.PENDIENTE));
        assertEquals(1, reporte.get(EstadoSolicitud.EN_PROCESO));
        assertEquals(1, reporte.get(EstadoSolicitud.RESUELTA));
    }
}

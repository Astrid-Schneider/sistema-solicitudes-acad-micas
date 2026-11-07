package ar.edu.metodologia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudServiceTest {

    private EstudianteService estudianteService;
    private SolicitudService solicitudService;
    private Responsable responsable;

    @BeforeEach
    void preparar() {
        estudianteService = new EstudianteService();
        estudianteService.registrarEstudiante("Estudiante Uno", "uno@alumnos.edu.ar");
        estudianteService.registrarEstudiante("Estudiante Dos", "dos@alumnos.edu.ar");
        solicitudService = new SolicitudService(new SolicitudRepositorioEnMemoria(), estudianteService);
        responsable = new Responsable(1, "Responsable Prueba", "Alumnado");
    }

    private Solicitud crear(int estudianteId, String tipo) {
        return solicitudService.registrarSolicitud(
                new DatosSolicitud(estudianteId, tipo, "Descripción de prueba", Prioridad.MEDIA));
    }

    // Prueba 3: registro de solicitudes asociadas a estudiantes existentes
    @Test
    @DisplayName("Registra una solicitud asociada a un estudiante existente")
    void registraSolicitudCorrectamente() {
        Solicitud solicitud = solicitudService.registrarSolicitud(
                new DatosSolicitud(2, "CERTIFICADO", "Certificado de alumno regular", Prioridad.ALTA));

        assertNotNull(solicitud);
        assertEquals(1, solicitud.getId());
        assertEquals("Estudiante Dos", solicitud.getEstudiante().getNombre());
        assertEquals("CERTIFICADO", solicitud.getTipo());
        assertEquals("Certificado de alumno regular", solicitud.getDescripcion());
        assertEquals(Prioridad.ALTA, solicitud.getPrioridad());
        assertEquals(1, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Una solicitud nueva queda PENDIENTE y sin responsable")
    void solicitudNuevaEstaPendienteYSinResponsable() {
        Solicitud solicitud = crear(1, "EXAMEN");

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertNull(solicitud.getResponsable());
    }

    @Test
    @DisplayName("Los IDs de solicitudes son correlativos")
    void idsCorrelativos() {
        assertEquals(1, crear(1, "EXAMEN").getId());
        assertEquals(2, crear(1, "EXAMEN").getId());
        assertEquals(3, crear(2, "OTRA").getId());
    }

    // Prueba 4: rechazo de solicitudes con datos inválidos
    @Test
    @DisplayName("Rechaza una solicitud de un estudiante inexistente")
    void rechazaEstudianteInexistente() {
        Solicitud solicitud = crear(99, "CERTIFICADO");

        assertNull(solicitud);
        assertEquals(0, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Rechaza una solicitud con tipo vacío")
    void rechazaTipoVacio() {
        assertNull(crear(1, ""));
        assertEquals(0, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Rechaza una solicitud con descripción vacía")
    void rechazaDescripcionVacia() {
        Solicitud solicitud = solicitudService.registrarSolicitud(
                new DatosSolicitud(1, "EXAMEN", "", Prioridad.BAJA));

        assertNull(solicitud);
        assertEquals(0, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Rechaza una solicitud con prioridad nula")
    void rechazaPrioridadNula() {
        Solicitud solicitud = solicitudService.registrarSolicitud(
                new DatosSolicitud(1, "EXAMEN", "Descripción de prueba", null));

        assertNull(solicitud);
        assertEquals(0, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Una solicitud rechazada no consume el número de ID siguiente")
    void rechazoNoConsumeId() {
        crear(99, "EXAMEN");
        Solicitud valida = crear(1, "EXAMEN");

        assertEquals(1, valida.getId());
    }

    // Prueba 5: búsqueda de solicitudes por ID
    @Test
    @DisplayName("Encuentra una solicitud por su ID")
    void buscaPorId() {
        crear(1, "CERTIFICADO");
        Solicitud segunda = crear(2, "EXAMEN");

        Solicitud encontrada = solicitudService.buscarSolicitudPorId(2);

        assertSame(segunda, encontrada);
        assertEquals("EXAMEN", encontrada.getTipo());
    }

    @Test
    @DisplayName("Buscar un ID inexistente devuelve null")
    void buscaPorIdInexistente() {
        crear(1, "CERTIFICADO");

        assertNull(solicitudService.buscarSolicitudPorId(99));
    }

    // Prueba 6: búsqueda por estado y tipo
    @Test
    @DisplayName("Busca solicitudes por estado")
    void buscaPorEstado() {
        Solicitud a = crear(1, "CERTIFICADO");
        Solicitud b = crear(1, "EXAMEN");
        Solicitud c = crear(2, "EXAMEN");
        solicitudService.cambiarEstado(b.getId(), EstadoSolicitud.EN_PROCESO);
        solicitudService.cambiarEstado(c.getId(), EstadoSolicitud.RESUELTA);

        List<Solicitud> pendientes = solicitudService.buscarPorEstado(EstadoSolicitud.PENDIENTE);
        List<Solicitud> enProceso = solicitudService.buscarPorEstado(EstadoSolicitud.EN_PROCESO);
        List<Solicitud> resueltas = solicitudService.buscarPorEstado(EstadoSolicitud.RESUELTA);

        assertEquals(List.of(a), pendientes);
        assertEquals(List.of(b), enProceso);
        assertEquals(List.of(c), resueltas);
    }

    @Test
    @DisplayName("Busca solicitudes por tipo")
    void buscaPorTipo() {
        Solicitud a = crear(1, "CERTIFICADO");
        Solicitud b = crear(1, "EXAMEN");
        Solicitud c = crear(2, "EXAMEN");

        assertEquals(List.of(b, c), solicitudService.buscarPorTipo("EXAMEN"));
        assertEquals(List.of(a), solicitudService.buscarPorTipo("CERTIFICADO"));
    }

    @Test
    @DisplayName("Buscar por un tipo o estado sin coincidencias devuelve lista vacía")
    void busquedasSinCoincidencias() {
        crear(1, "CERTIFICADO");

        assertTrue(solicitudService.buscarPorTipo("EQUIVALENCIA").isEmpty());
        assertTrue(solicitudService.buscarPorEstado(EstadoSolicitud.RESUELTA).isEmpty());
    }

    @Test
    @DisplayName("La búsqueda por tipo distingue mayúsculas (la consola pasa el texto a mayúsculas)")
    void busquedaPorTipoDistingueMayusculas() {
        crear(1, "CERTIFICADO");

        assertTrue(solicitudService.buscarPorTipo("certificado").isEmpty());
    }

    @Test
    @DisplayName("Buscar por estado null devuelve lista vacía")
    void buscarPorEstadoNull() {
        crear(1, "CERTIFICADO");

        assertTrue(solicitudService.buscarPorEstado(null).isEmpty());
    }

    // Prueba 7: asignación correcta de responsables
    @Test
    @DisplayName("Asigna un responsable a una solicitud existente")
    void asignaResponsable() {
        Solicitud solicitud = crear(1, "CERTIFICADO");

        boolean resultado = solicitudService.asignarResponsable(solicitud.getId(), responsable);

        assertTrue(resultado);
        assertSame(responsable, solicitudService.buscarSolicitudPorId(solicitud.getId()).getResponsable());
    }

    @Test
    @DisplayName("Reasignar reemplaza al responsable anterior")
    void reasignaResponsable() {
        Solicitud solicitud = crear(1, "CERTIFICADO");
        Responsable otro = new Responsable(2, "Otro Responsable", "Secretaría Académica");

        solicitudService.asignarResponsable(solicitud.getId(), responsable);
        solicitudService.asignarResponsable(solicitud.getId(), otro);

        assertSame(otro, solicitud.getResponsable());
    }

    // Prueba 8: responsables o solicitudes inexistentes
    @Test
    @DisplayName("Asignar a una solicitud inexistente devuelve false")
    void asignarASolicitudInexistente() {
        assertFalse(solicitudService.asignarResponsable(99, responsable));
    }

    @Test
    @DisplayName("Asignar un responsable null devuelve false y no modifica la solicitud")
    void asignarResponsableNull() {
        Solicitud solicitud = crear(1, "CERTIFICADO");
        solicitudService.asignarResponsable(solicitud.getId(), responsable);

        boolean resultado = solicitudService.asignarResponsable(solicitud.getId(), null);

        assertFalse(resultado);
        assertSame(responsable, solicitud.getResponsable());
    }

    // Prueba 9: cambio correcto de estado
    @Test
    @DisplayName("Cambia el estado de una solicitud")
    void cambiaEstado() {
        Solicitud solicitud = crear(1, "CERTIFICADO");

        assertTrue(solicitudService.cambiarEstado(solicitud.getId(), EstadoSolicitud.EN_PROCESO));
        assertEquals(EstadoSolicitud.EN_PROCESO, solicitud.getEstado());

        assertTrue(solicitudService.cambiarEstado(solicitud.getId(), EstadoSolicitud.RESUELTA));
        assertEquals(EstadoSolicitud.RESUELTA, solicitud.getEstado());
    }

    @Test
    @DisplayName("El sistema permite volver a un estado anterior (no hay reglas de transición)")
    void permiteVolverAEstadoAnterior() {
        Solicitud solicitud = crear(1, "CERTIFICADO");
        solicitudService.cambiarEstado(solicitud.getId(), EstadoSolicitud.RESUELTA);

        assertTrue(solicitudService.cambiarEstado(solicitud.getId(), EstadoSolicitud.PENDIENTE));
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    }

    @Test
    @DisplayName("Cambiar el estado de una solicitud inexistente devuelve false")
    void cambiarEstadoSolicitudInexistente() {
        assertFalse(solicitudService.cambiarEstado(99, EstadoSolicitud.RESUELTA));
    }

    // Prueba 10: rechazo de estados inválidos
    @Test
    @DisplayName("Cambiar a un estado null devuelve false y no modifica la solicitud")
    void cambiarAEstadoNull() {
        Solicitud solicitud = crear(1, "CERTIFICADO");

        assertFalse(solicitudService.cambiarEstado(solicitud.getId(), null));
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    }

    // Prueba 11: reporte por estado
    @Test
    @DisplayName("El reporte cuenta correctamente las solicitudes por estado")
    void reportePorEstado() {
        crear(1, "CERTIFICADO");
        Solicitud b = crear(1, "EXAMEN");
        Solicitud c = crear(2, "EXAMEN");
        Solicitud d = crear(2, "OTRA");
        solicitudService.cambiarEstado(b.getId(), EstadoSolicitud.EN_PROCESO);
        solicitudService.cambiarEstado(c.getId(), EstadoSolicitud.RESUELTA);
        solicitudService.cambiarEstado(d.getId(), EstadoSolicitud.RESUELTA);

        Map<EstadoSolicitud, Integer> reporte = solicitudService.contarPorEstado();

        assertEquals(1, reporte.get(EstadoSolicitud.PENDIENTE));
        assertEquals(1, reporte.get(EstadoSolicitud.EN_PROCESO));
        assertEquals(2, reporte.get(EstadoSolicitud.RESUELTA));
        assertEquals(4, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("El reporte siempre incluye los tres estados y la suma coincide con el total")
    void reporteIncluyeTodosLosEstados() {
        crear(1, "CERTIFICADO");
        crear(2, "EXAMEN");

        Map<EstadoSolicitud, Integer> reporte = solicitudService.contarPorEstado();

        assertEquals(EstadoSolicitud.values().length, reporte.size());
        int suma = reporte.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(solicitudService.contarTotal(), suma);
    }

    // Prueba 12: comportamiento sin datos
    @Test
    @DisplayName("Sin solicitudes: listados y búsquedas vacíos, y reporte en cero")
    void sinSolicitudes() {
        assertTrue(solicitudService.listarSolicitudes().isEmpty());
        assertNull(solicitudService.buscarSolicitudPorId(1));
        assertTrue(solicitudService.buscarPorEstado(EstadoSolicitud.PENDIENTE).isEmpty());
        assertTrue(solicitudService.buscarPorTipo("CERTIFICADO").isEmpty());
        assertEquals(0, solicitudService.contarTotal());

        Map<EstadoSolicitud, Integer> reporte = solicitudService.contarPorEstado();
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            assertEquals(0, reporte.get(estado), "Debería haber 0 en " + estado);
        }
    }

    @Test
    @DisplayName("Sin solicitudes: asignar responsable y cambiar estado devuelven false")
    void operacionesSinSolicitudes() {
        assertFalse(solicitudService.asignarResponsable(1, responsable));
        assertFalse(solicitudService.cambiarEstado(1, EstadoSolicitud.RESUELTA));
    }
}

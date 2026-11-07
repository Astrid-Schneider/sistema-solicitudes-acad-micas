package ar.edu.metodologia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModeloYEnumsTest {

    // Prueba 10: rechazo de estados inválidos (conversión desde texto)
    @Test
    @DisplayName("EstadoSolicitud.desdeTexto reconoce los tres estados válidos")
    void estadoDesdeTextoValido() {
        assertEquals(EstadoSolicitud.PENDIENTE, EstadoSolicitud.desdeTexto("PENDIENTE"));
        assertEquals(EstadoSolicitud.EN_PROCESO, EstadoSolicitud.desdeTexto("EN_PROCESO"));
        assertEquals(EstadoSolicitud.RESUELTA, EstadoSolicitud.desdeTexto("RESUELTA"));
    }

    @Test
    @DisplayName("EstadoSolicitud.desdeTexto devuelve null con textos inválidos")
    void estadoDesdeTextoInvalido() {
        assertNull(EstadoSolicitud.desdeTexto("CANCELADA"));
        assertNull(EstadoSolicitud.desdeTexto(""));
        assertNull(EstadoSolicitud.desdeTexto("pendiente"));
        assertNull(EstadoSolicitud.desdeTexto("EN PROCESO"));
        assertNull(EstadoSolicitud.desdeTexto(null));
    }

    @Test
    @DisplayName("Prioridad.desdeTexto reconoce las prioridades válidas")
    void prioridadDesdeTextoValida() {
        assertEquals(Prioridad.BAJA, Prioridad.desdeTexto("BAJA"));
        assertEquals(Prioridad.MEDIA, Prioridad.desdeTexto("MEDIA"));
        assertEquals(Prioridad.ALTA, Prioridad.desdeTexto("ALTA"));
    }

    @Test
    @DisplayName("Prioridad.desdeTexto devuelve null con textos inválidos")
    void prioridadDesdeTextoInvalida() {
        assertNull(Prioridad.desdeTexto("URGENTE"));
        assertNull(Prioridad.desdeTexto(""));
        assertNull(Prioridad.desdeTexto("alta"));
        assertNull(Prioridad.desdeTexto(null));
    }

    // Prueba 4: validación de los datos de una solicitud
    @Test
    @DisplayName("DatosSolicitud válidos")
    void datosSolicitudValidos() {
        assertTrue(new DatosSolicitud(1, "EXAMEN", "Descripción", Prioridad.BAJA).sonValidos());
    }

    @Test
    @DisplayName("DatosSolicitud inválidos: tipo vacío, descripción vacía o prioridad nula")
    void datosSolicitudInvalidos() {
        assertFalse(new DatosSolicitud(1, "", "Descripción", Prioridad.BAJA).sonValidos());
        assertFalse(new DatosSolicitud(1, "EXAMEN", "", Prioridad.BAJA).sonValidos());
        assertFalse(new DatosSolicitud(1, "EXAMEN", "Descripción", null).sonValidos());
    }

    @Test
    @DisplayName("Limitación conocida: DatosSolicitud con tipo null lanza NullPointerException")
    void datosSolicitudConTipoNullLanzaExcepcion() {
        // Hoy la consola nunca manda null, pero el método no se protege.
        // Se documenta el comportamiento actual (ver docs/code-review.md).
        DatosSolicitud datos = new DatosSolicitud(1, null, "Descripción", Prioridad.BAJA);
        assertThrows(NullPointerException.class, datos::sonValidos);
    }

    @Test
    @DisplayName("Solicitud.toString muestra 'Sin asignar' y luego el nombre del responsable")
    void toStringDeSolicitud() {
        Estudiante estudiante = new Estudiante(1, "Estudiante Prueba", "prueba@alumnos.edu.ar");
        Solicitud solicitud = new Solicitud(7, estudiante,
                new DatosSolicitud(1, "EXAMEN", "Inscripción", Prioridad.ALTA));

        String sinResponsable = solicitud.toString();
        assertTrue(sinResponsable.contains("Solicitud 7"));
        assertTrue(sinResponsable.contains("Estudiante: Estudiante Prueba"));
        assertTrue(sinResponsable.contains("Estado: PENDIENTE"));
        assertTrue(sinResponsable.contains("Responsable: Sin asignar"));

        solicitud.setResponsable(new Responsable(1, "Responsable Prueba", "Alumnado"));
        assertTrue(solicitud.toString().contains("Responsable: Responsable Prueba"));
    }
}

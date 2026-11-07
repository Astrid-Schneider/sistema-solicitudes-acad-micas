package ar.edu.metodologia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstudianteServiceTest {

    private EstudianteService service;

    @BeforeEach
    void preparar() {
        service = new EstudianteService();
    }

    // Prueba 1: registro correcto de estudiantes
    @Test
    @DisplayName("Registra un estudiante y lo guarda con sus datos")
    void registraEstudianteCorrectamente() {
        service.registrarEstudiante("Estudiante Prueba", "prueba@alumnos.edu.ar");

        assertEquals(1, service.listarEstudiantes().size());
        Estudiante estudiante = service.buscarEstudiantePorId(1);
        assertNotNull(estudiante);
        assertEquals("Estudiante Prueba", estudiante.getNombre());
        assertEquals("prueba@alumnos.edu.ar", estudiante.getEmail());
    }

    @Test
    @DisplayName("Los IDs de estudiantes se asignan en forma correlativa desde 1")
    void asignaIdsCorrelativos() {
        service.registrarEstudiante("Uno", "uno@alumnos.edu.ar");
        service.registrarEstudiante("Dos", "dos@alumnos.edu.ar");
        service.registrarEstudiante("Tres", "tres@alumnos.edu.ar");

        assertEquals(1, service.listarEstudiantes().get(0).getId());
        assertEquals(2, service.listarEstudiantes().get(1).getId());
        assertEquals(3, service.listarEstudiantes().get(2).getId());
        assertEquals("Dos", service.buscarEstudiantePorId(2).getNombre());
    }

    @Test
    @DisplayName("Acepta datos válidos")
    void datosValidosSonAceptados() {
        assertTrue(service.datosValidos("Estudiante Prueba", "prueba@alumnos.edu.ar"));
    }

    // Prueba 2: rechazo de estudiantes con datos inválidos
    @Test
    @DisplayName("Rechaza nombre vacío")
    void rechazaNombreVacio() {
        assertFalse(service.datosValidos("", "prueba@alumnos.edu.ar"));
    }

    @Test
    @DisplayName("Rechaza email vacío")
    void rechazaEmailVacio() {
        assertFalse(service.datosValidos("Estudiante Prueba", ""));
    }

    @Test
    @DisplayName("Rechaza email sin arroba")
    void rechazaEmailSinArroba() {
        assertFalse(service.datosValidos("Estudiante Prueba", "pruebaalumnos.edu.ar"));
    }

    @Test
    @DisplayName("Limitación conocida: un nombre con solo espacios se considera válido en el servicio")
    void nombreConSoloEspaciosPasaLaValidacionDelServicio() {
        // La consola hace trim() antes de validar, pero el servicio por sí solo no lo hace.
        // Se documenta el comportamiento actual (ver docs/code-review.md).
        assertTrue(service.datosValidos("   ", "prueba@alumnos.edu.ar"));
    }

    // Prueba 12: comportamiento sin datos
    @Test
    @DisplayName("Sin estudiantes: la lista está vacía y la búsqueda devuelve null")
    void sinEstudiantes() {
        assertTrue(service.listarEstudiantes().isEmpty());
        assertNull(service.buscarEstudiantePorId(1));
    }

    @Test
    @DisplayName("Buscar un ID inexistente devuelve null")
    void buscarIdInexistente() {
        service.registrarEstudiante("Estudiante Prueba", "prueba@alumnos.edu.ar");

        assertNull(service.buscarEstudiantePorId(99));
        assertNull(service.buscarEstudiantePorId(0));
        assertNull(service.buscarEstudiantePorId(-1));
    }
}

package ar.edu.metodologia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la consola: se simula el teclado con System.in y se lee lo que imprime System.out.
 * No hace falta modificar el código de producción porque Sistema crea su Scanner al construirse.
 */
class SistemaConsolaTest {

    private final InputStream entradaOriginal = System.in;
    private final PrintStream salidaOriginal = System.out;
    private ByteArrayOutputStream salida;

    private EstudianteService estudianteService;
    private ResponsableService responsableService;
    private SolicitudService solicitudService;

    @BeforeEach
    void preparar() {
        estudianteService = new EstudianteService();
        responsableService = new ResponsableService();
        solicitudService = new SolicitudService(new SolicitudRepositorioEnMemoria(), estudianteService);
        salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurar() {
        System.setIn(entradaOriginal);
        System.setOut(salidaOriginal);
    }

    /** Ejecuta el sistema con las líneas dadas (siempre se agrega un 0 final para salir). */
    private String ejecutar(String... lineas) {
        String entrada = String.join("\n", lineas) + "\n0\n";
        System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
        new Sistema(estudianteService, responsableService, solicitudService).iniciar();
        return salida.toString(StandardCharsets.UTF_8);
    }

    private void cargarDatosIniciales() {
        DatosIniciales.cargar(estudianteService, responsableService, solicitudService);
    }

    // Prueba 1 y 2 por consola
    @Test
    @DisplayName("Consola: registra un estudiante válido")
    void registraEstudianteValido() {
        String texto = ejecutar("1", "Estudiante Prueba", "prueba@alumnos.edu.ar");

        assertTrue(texto.contains("Estudiante registrado correctamente."));
        assertEquals(1, estudianteService.listarEstudiantes().size());
    }

    @Test
    @DisplayName("Consola: rechaza un estudiante con email sin arroba")
    void rechazaEstudianteInvalido() {
        String texto = ejecutar("1", "Estudiante Prueba", "email-sin-arroba");

        assertTrue(texto.contains("Datos inválidos. El nombre y el email son obligatorios."));
        assertTrue(estudianteService.listarEstudiantes().isEmpty());
    }

    @Test
    @DisplayName("Consola: rechaza un nombre con solo espacios (la consola hace trim)")
    void rechazaNombreSoloEspacios() {
        String texto = ejecutar("1", "    ", "prueba@alumnos.edu.ar");

        assertTrue(texto.contains("Datos inválidos"));
        assertTrue(estudianteService.listarEstudiantes().isEmpty());
    }

    // Prueba 3 y 4 por consola
    @Test
    @DisplayName("Consola: registra una solicitud (acepta minúsculas y las pasa a mayúsculas)")
    void registraSolicitudValida() {
        cargarDatosIniciales();
        int antes = solicitudService.contarTotal();

        String texto = ejecutar("2", "1", "otra", "Consulta de prueba", "baja");

        assertTrue(texto.contains("Solicitud registrada correctamente."));
        assertEquals(antes + 1, solicitudService.contarTotal());
        Solicitud nueva = solicitudService.buscarSolicitudPorId(antes + 1);
        assertEquals("OTRA", nueva.getTipo());
        assertEquals(Prioridad.BAJA, nueva.getPrioridad());
        assertEquals(EstadoSolicitud.PENDIENTE, nueva.getEstado());
    }

    @Test
    @DisplayName("Consola: rechaza una solicitud con estudiante inexistente")
    void rechazaSolicitudEstudianteInexistente() {
        cargarDatosIniciales();
        int antes = solicitudService.contarTotal();

        String texto = ejecutar("2", "99");

        assertTrue(texto.contains("No existe el estudiante."));
        assertEquals(antes, solicitudService.contarTotal());
    }

    @Test
    @DisplayName("Consola: rechaza una solicitud con ID de estudiante no numérico")
    void rechazaSolicitudIdNoNumerico() {
        cargarDatosIniciales();

        String texto = ejecutar("2", "abc");

        assertTrue(texto.contains("ID inválido."));
    }

    @Test
    @DisplayName("Consola: rechaza una solicitud con prioridad inválida")
    void rechazaSolicitudPrioridadInvalida() {
        cargarDatosIniciales();
        int antes = solicitudService.contarTotal();

        String texto = ejecutar("2", "1", "EXAMEN", "Descripción de prueba", "URGENTE");

        assertTrue(texto.contains("Tipo, descripción o prioridad inválidos."));
        assertEquals(antes, solicitudService.contarTotal());
    }

    // Prueba 5 y 6 por consola
    @Test
    @DisplayName("Consola: consulta una solicitud por ID")
    void consultaPorId() {
        cargarDatosIniciales();

        String texto = ejecutar("4", "2");

        assertTrue(texto.contains("Solicitud 2"));
        assertTrue(texto.contains("Equivalencia de Programación I"));
    }

    @Test
    @DisplayName("Consola: avisa si la solicitud no existe")
    void consultaPorIdInexistente() {
        cargarDatosIniciales();

        assertTrue(ejecutar("4", "99").contains("No existe la solicitud."));
    }

    @Test
    @DisplayName("Consola: busca por estado y por tipo")
    void buscaPorEstadoYTipo() {
        cargarDatosIniciales();

        String porEstado = ejecutar("5", "resuelta");
        assertTrue(porEstado.contains("Solicitud 3"));

        salida.reset();
        String porTipo = ejecutar("6", "certificado");
        assertTrue(porTipo.contains("Solicitud 1"));
    }

    @Test
    @DisplayName("Consola: búsqueda sin resultados")
    void busquedaSinResultados() {
        cargarDatosIniciales();

        assertTrue(ejecutar("6", "inexistente").contains("No se encontraron solicitudes."));
    }

    // Prueba 7, 8, 9 y 10 por consola
    @Test
    @DisplayName("Consola: asigna un responsable")
    void asignaResponsable() {
        cargarDatosIniciales();

        String texto = ejecutar("7", "1", "2");

        assertTrue(texto.contains("Responsable asignado correctamente."));
        assertEquals("Martín Ruiz", solicitudService.buscarSolicitudPorId(1).getResponsable().getNombre());
    }

    @Test
    @DisplayName("Consola: avisa si el responsable o la solicitud no existen")
    void asignaResponsableInexistente() {
        cargarDatosIniciales();

        assertTrue(ejecutar("7", "1", "99").contains("No existe el responsable."));
        salida.reset();
        assertTrue(ejecutar("7", "99").contains("No existe la solicitud."));
        assertNull(solicitudService.buscarSolicitudPorId(1).getResponsable());
    }

    @Test
    @DisplayName("Consola: cambia el estado de una solicitud")
    void cambiaEstado() {
        cargarDatosIniciales();

        String texto = ejecutar("8", "1", "en_proceso");

        assertTrue(texto.contains("Estado actualizado correctamente."));
        assertEquals(EstadoSolicitud.EN_PROCESO, solicitudService.buscarSolicitudPorId(1).getEstado());
    }

    @Test
    @DisplayName("Consola: rechaza un estado inválido y no cambia la solicitud")
    void rechazaEstadoInvalido() {
        cargarDatosIniciales();

        String texto = ejecutar("8", "1", "CANCELADA");

        assertTrue(texto.contains("Estado inválido."));
        assertEquals(EstadoSolicitud.PENDIENTE, solicitudService.buscarSolicitudPorId(1).getEstado());
    }

    // Prueba 11 por consola
    @Test
    @DisplayName("Consola: el reporte por estado muestra los números de los datos iniciales")
    void reportePorEstado() {
        cargarDatosIniciales();

        String texto = ejecutar("9");

        assertTrue(texto.contains("PENDIENTE: 1"));
        assertTrue(texto.contains("EN_PROCESO: 1"));
        assertTrue(texto.contains("RESUELTA: 1"));
        assertTrue(texto.contains("TOTAL: 3"));
    }

    // Prueba 12 por consola
    @Test
    @DisplayName("Consola sin datos: listado vacío y reporte en cero")
    void consolaSinDatos() {
        String texto = ejecutar("3", "9");

        assertTrue(texto.contains("No hay solicitudes."));
        assertTrue(texto.contains("PENDIENTE: 0"));
        assertTrue(texto.contains("EN_PROCESO: 0"));
        assertTrue(texto.contains("RESUELTA: 0"));
        assertTrue(texto.contains("TOTAL: 0"));
    }

    @Test
    @DisplayName("Consola: opción inválida y texto no numérico en el menú")
    void opcionInvalida() {
        String texto = ejecutar("77", "hola");

        assertEquals(2, contar(texto, "Opción inválida."));
        assertTrue(texto.contains("Hasta luego."));
    }

    private int contar(String texto, String buscado) {
        int cantidad = 0;
        int posicion = texto.indexOf(buscado);
        while (posicion >= 0) {
            cantidad++;
            posicion = texto.indexOf(buscado, posicion + buscado.length());
        }
        return cantidad;
    }
}

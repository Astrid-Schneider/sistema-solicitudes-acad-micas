package ar.edu.metodologia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponsableServiceTest {

    private ResponsableService service;

    @BeforeEach
    void preparar() {
        service = new ResponsableService();
    }

    // Prueba 7: asignación de responsables (parte de búsqueda)
    @Test
    @DisplayName("Agrega responsables y los encuentra por ID")
    void agregaYBuscaResponsable() {
        Responsable uno = new Responsable(1, "Responsable Uno", "Alumnado");
        Responsable dos = new Responsable(2, "Responsable Dos", "Secretaría Académica");
        service.agregarResponsable(uno);
        service.agregarResponsable(dos);

        assertEquals(2, service.listarResponsables().size());
        assertSame(uno, service.buscarResponsablePorId(1));
        assertSame(dos, service.buscarResponsablePorId(2));
    }

    // Prueba 8: responsables inexistentes
    @Test
    @DisplayName("Buscar un responsable inexistente devuelve null")
    void buscaResponsableInexistente() {
        service.agregarResponsable(new Responsable(1, "Responsable Uno", "Alumnado"));

        assertNull(service.buscarResponsablePorId(99));
    }

    // Prueba 12: sin datos
    @Test
    @DisplayName("Sin responsables: la lista está vacía y la búsqueda devuelve null")
    void sinResponsables() {
        assertTrue(service.listarResponsables().isEmpty());
        assertNull(service.buscarResponsablePorId(1));
    }
}

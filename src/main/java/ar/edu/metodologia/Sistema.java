package ar.edu.metodologia;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Sistema {
    private EstudianteService estudianteService;
    private ResponsableService responsableService;
    private SolicitudService solicitudService;
    private Scanner teclado = new Scanner(System.in);

    public Sistema(EstudianteService estudianteService, ResponsableService responsableService,
                   SolicitudService solicitudService) {
        this.estudianteService = estudianteService;
        this.responsableService = responsableService;
        this.solicitudService = solicitudService;
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            Integer numeroIngresado = leerNumero();
            opcion = numeroIngresado == null ? -1 : numeroIngresado;
            ejecutarOpcion(opcion);
        }
    }

    private void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE SOLICITUDES ACADÉMICAS ===");
        System.out.println("1. Registrar estudiante\n2. Registrar solicitud\n3. Listar solicitudes");
        System.out.println("4. Consultar solicitud por ID\n5. Buscar por estado\n6. Buscar por tipo");
        System.out.println("7. Asignar responsable\n8. Cambiar estado\n9. Reporte por estado\n0. Salir");
        System.out.print("Opción: ");
    }

    private void ejecutarOpcion(int opcion) {
        if (opcion == 1) {
            altaEstudiante();
        } else if (opcion == 2) {
            altaSolicitud();
        } else if (opcion == 3) {
            listarSolicitudes();
        } else if (opcion == 4) {
            consultarSolicitud();
        } else if (opcion == 5) {
            buscarPorEstado();
        } else if (opcion == 6) {
            buscarPorTipo();
        } else if (opcion == 7) {
            asignarResponsable();
        } else if (opcion == 8) {
            cambiarEstado();
        } else if (opcion == 9) {
            mostrarReporte();
        } else if (opcion == 0) {
            System.out.println("Hasta luego.");
        } else {
            System.out.println("Opción inválida.");
        }
    }

    private Integer leerNumero() {
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException entradaInvalida) {
            return null;
        }
    }

    private void altaEstudiante() {
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine().trim();
        System.out.print("Email: ");
        String email = teclado.nextLine().trim();
        if (!estudianteService.datosValidos(nombre, email)) {
            System.out.println("Datos inválidos. El nombre y el email son obligatorios.");
        } else {
            estudianteService.registrarEstudiante(nombre, email);
            System.out.println("Estudiante registrado correctamente.");
        }
    }

    private void altaSolicitud() {
        mostrarEstudiantes();
        System.out.print("ID del estudiante: ");
        Integer estudianteId = leerNumero();
        if (estudianteId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Estudiante estudianteEncontrado = estudianteService.buscarEstudiantePorId(estudianteId);
        if (estudianteEncontrado == null) {
            System.out.println("No existe el estudiante.");
            return;
        }

        registrarSolicitudDesdeConsola(estudianteId);
    }

    private void mostrarEstudiantes() {
        for (Estudiante estudiante : estudianteService.listarEstudiantes()) {
            System.out.println(estudiante);
        }
    }

    private void registrarSolicitudDesdeConsola(int estudianteId) {
        System.out.print("Tipo (CERTIFICADO/EQUIVALENCIA/EXAMEN/OTRA): ");
        String tipo = teclado.nextLine().trim().toUpperCase();
        System.out.print("Descripción: ");
        String descripcion = teclado.nextLine().trim();
        System.out.print("Prioridad (BAJA/MEDIA/ALTA): ");
        Prioridad prioridad = Prioridad.desdeTexto(teclado.nextLine().trim().toUpperCase());

        DatosSolicitud datos = new DatosSolicitud(estudianteId, tipo, descripcion, prioridad);
        if (!datos.sonValidos()) {
            System.out.println("Tipo, descripción o prioridad inválidos.");
            return;
        }

        solicitudService.registrarSolicitud(datos);
        System.out.println("Solicitud registrada correctamente.");
    }

    private void listarSolicitudes() {
        List<Solicitud> solicitudes = solicitudService.listarSolicitudes();
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes.");
        }
        for (Solicitud solicitud : solicitudes) {
            System.out.println(solicitud);
        }
    }

    private void consultarSolicitud() {
        System.out.print("ID de la solicitud: ");
        Integer solicitudId = leerNumero();
        if (solicitudId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Solicitud solicitudEncontrada = solicitudService.buscarSolicitudPorId(solicitudId);
        if (solicitudEncontrada == null) {
            System.out.println("No existe la solicitud.");
        } else {
            System.out.println(solicitudEncontrada);
        }
    }

    private void buscarPorEstado() {
        System.out.print("Estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        EstadoSolicitud estadoBuscado = EstadoSolicitud.desdeTexto(teclado.nextLine().trim().toUpperCase());
        mostrarResultadosDeBusqueda(solicitudService.buscarPorEstado(estadoBuscado));
    }

    private void buscarPorTipo() {
        System.out.print("Tipo a buscar: ");
        String tipoBuscado = teclado.nextLine().trim().toUpperCase();
        mostrarResultadosDeBusqueda(solicitudService.buscarPorTipo(tipoBuscado));
    }

    private void mostrarResultadosDeBusqueda(List<Solicitud> encontradas) {
        if (encontradas.isEmpty()) {
            System.out.println("No se encontraron solicitudes.");
            return;
        }
        for (Solicitud solicitud : encontradas) {
            System.out.println(solicitud);
        }
    }

    private void asignarResponsable() {
        System.out.print("ID de la solicitud: ");
        Integer solicitudId = leerNumero();
        if (solicitudId == null) {
            System.out.println("ID inválido.");
            return;
        }

        if (solicitudService.buscarSolicitudPorId(solicitudId) == null) {
            System.out.println("No existe la solicitud.");
            return;
        }
        for (Responsable responsable : responsableService.listarResponsables()) {
            System.out.println(responsable);
        }
        System.out.print("ID del responsable: ");
        Integer responsableId = leerNumero();
        if (responsableId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Responsable responsableEncontrado = responsableService.buscarResponsablePorId(responsableId);
        if (responsableEncontrado == null) {
            System.out.println("No existe el responsable.");
        } else {
            solicitudService.asignarResponsable(solicitudId, responsableEncontrado);
            System.out.println("Responsable asignado correctamente.");
        }
    }

    private void cambiarEstado() {
        System.out.print("ID de la solicitud: ");
        Integer solicitudId = leerNumero();
        if (solicitudId == null) {
            System.out.println("ID inválido.");
            return;
        }

        if (solicitudService.buscarSolicitudPorId(solicitudId) == null) {
            System.out.println("No existe la solicitud.");
            return;
        }
        System.out.print("Nuevo estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        EstadoSolicitud nuevoEstado = EstadoSolicitud.desdeTexto(teclado.nextLine().trim().toUpperCase());
        if (nuevoEstado == null) {
            System.out.println("Estado inválido.");
        } else {
            solicitudService.cambiarEstado(solicitudId, nuevoEstado);
            System.out.println("Estado actualizado correctamente.");
        }
    }

    private void mostrarReporte() {
        Map<EstadoSolicitud, Integer> cantidadesPorEstado = solicitudService.contarPorEstado();
        System.out.println("\n--- REPORTE POR ESTADO ---");
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            System.out.println(estado + ": " + cantidadesPorEstado.get(estado));
        }
        System.out.println("TOTAL: " + solicitudService.contarTotal());
    }
}

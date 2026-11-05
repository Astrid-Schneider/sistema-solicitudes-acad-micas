package ar.edu.metodologia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sistema {
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Solicitud> solicitudes = new ArrayList<>();
    private List<Responsable> responsables = new ArrayList<>();
    private Scanner teclado = new Scanner(System.in);
    private int proximoEstudiante = 1;
    private int proximaSolicitud = 1;

    public void cargarDatos() {
        registrarEstudiante("Ana López", "ana.lopez@alumnos.edu.ar");
        registrarEstudiante("Bruno Díaz", "bruno.diaz@alumnos.edu.ar");
        registrarEstudiante("Carla Méndez", "carla.mendez@alumnos.edu.ar");
        responsables.add(new Responsable(1, "Laura Gómez", "Alumnado"));
        responsables.add(new Responsable(2, "Martín Ruiz", "Secretaría Académica"));
        registrarSolicitud(1, "CERTIFICADO", "Solicita certificado de alumno regular", "MEDIA", "PENDIENTE", null);
        registrarSolicitud(2, "EQUIVALENCIA", "Equivalencia de Programación I", "ALTA", "EN_PROCESO", responsables.get(1));
        registrarSolicitud(3, "EXAMEN", "Inscripción fuera de término", "ALTA", "RESUELTA", responsables.get(0));
    }

    public void registrarEstudiante(String nombre, String email) {
        estudiantes.add(new Estudiante(proximoEstudiante, nombre, email));
        proximoEstudiante++;
    }

    public void registrarSolicitud(int estudianteId, String tipo, String descripcion, String prioridad,
                                   String estado, Responsable responsable) {
        Estudiante estudianteEncontrado = buscarEstudiantePorId(estudianteId);
        if (estudianteEncontrado != null) {
            solicitudes.add(new Solicitud(proximaSolicitud, estudianteEncontrado, tipo, descripcion,
                    prioridad, estado, responsable));
            proximaSolicitud++;
        }
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
        if (nombre.isEmpty() || email.isEmpty() || !email.contains("@")) {
            System.out.println("Datos inválidos. El nombre y el email son obligatorios.");
        } else {
            registrarEstudiante(nombre, email);
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

        Estudiante estudianteEncontrado = buscarEstudiantePorId(estudianteId);
        if (estudianteEncontrado == null) {
            System.out.println("No existe el estudiante.");
            return;
        }

        registrarSolicitudDesdeConsola(estudianteId);
    }

    private void mostrarEstudiantes() {
        for (Estudiante estudiante : estudiantes) {
            System.out.println(estudiante);
        }
    }

    private Estudiante buscarEstudiantePorId(int estudianteId) {
        Estudiante estudianteEncontrado = null;
        for (Estudiante estudiante : estudiantes) {
            if (estudiante.getId() == estudianteId) {
                estudianteEncontrado = estudiante;
            }
        }
        return estudianteEncontrado;
    }

    private void registrarSolicitudDesdeConsola(int estudianteId) {
        System.out.print("Tipo (CERTIFICADO/EQUIVALENCIA/EXAMEN/OTRA): ");
        String tipo = teclado.nextLine().trim().toUpperCase();
        System.out.print("Descripción: ");
        String descripcion = teclado.nextLine().trim();
        System.out.print("Prioridad (BAJA/MEDIA/ALTA): ");
        String prioridad = teclado.nextLine().trim().toUpperCase();
        if (!datosSolicitudValidos(tipo, descripcion, prioridad)) {
            System.out.println("Tipo, descripción o prioridad inválidos.");
            return;
        }

        registrarSolicitud(estudianteId, tipo, descripcion, prioridad, "PENDIENTE", null);
        System.out.println("Solicitud registrada correctamente.");
    }

    private boolean datosSolicitudValidos(String tipo, String descripcion, String prioridad) {
        return !tipo.isEmpty()
                && !descripcion.isEmpty()
                && (prioridad.equals("BAJA") || prioridad.equals("MEDIA") || prioridad.equals("ALTA"));
    }

    private void listarSolicitudes() {
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes.");
        }
        for (Solicitud solicitud : solicitudes) {
            System.out.println(solicitud);
        }
    }

    private Solicitud buscarSolicitudPorId(int solicitudId) {
        Solicitud solicitudEncontrada = null;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == solicitudId) {
                solicitudEncontrada = solicitud;
            }
        }
        return solicitudEncontrada;
    }

    private void consultarSolicitud() {
        System.out.print("ID de la solicitud: ");
        Integer solicitudId = leerNumero();
        if (solicitudId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Solicitud solicitudEncontrada = buscarSolicitudPorId(solicitudId);
        if (solicitudEncontrada == null) {
            System.out.println("No existe la solicitud.");
        } else {
            System.out.println(solicitudEncontrada);
        }
    }

    private void buscarPorEstado() {
        System.out.print("Estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        String estadoBuscado = teclado.nextLine().trim().toUpperCase();
        boolean haySolicitudes = false;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstado().equals(estadoBuscado)) {
                System.out.println(solicitud);
                haySolicitudes = true;
            }
        }
        if (!haySolicitudes) {
            System.out.println("No se encontraron solicitudes.");
        }
    }

    private void buscarPorTipo() {
        System.out.print("Tipo a buscar: ");
        String tipoBuscado = teclado.nextLine().trim().toUpperCase();
        boolean haySolicitudes = false;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getTipo().equals(tipoBuscado)) {
                System.out.println(solicitud);
                haySolicitudes = true;
            }
        }
        if (!haySolicitudes) {
            System.out.println("No se encontraron solicitudes.");
        }
    }

    private void asignarResponsable() {
        System.out.print("ID de la solicitud: ");
        Integer solicitudId = leerNumero();
        if (solicitudId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Solicitud solicitudEncontrada = buscarSolicitudPorId(solicitudId);
        if (solicitudEncontrada == null) {
            System.out.println("No existe la solicitud.");
            return;
        }
        for (Responsable responsable : responsables) {
            System.out.println(responsable);
        }
        System.out.print("ID del responsable: ");
        Integer responsableId = leerNumero();
        if (responsableId == null) {
            System.out.println("ID inválido.");
            return;
        }

        Responsable responsableEncontrado = null;
        for (Responsable responsable : responsables) {
            if (responsable.getId() == responsableId) {
                responsableEncontrado = responsable;
            }
        }
        if (responsableEncontrado == null) {
            System.out.println("No existe el responsable.");
        } else {
            solicitudEncontrada.setResponsable(responsableEncontrado);
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

        Solicitud solicitudEncontrada = buscarSolicitudPorId(solicitudId);
        if (solicitudEncontrada == null) {
            System.out.println("No existe la solicitud.");
            return;
        }
        System.out.print("Nuevo estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        String estado = teclado.nextLine().trim().toUpperCase();
        if (!(estado.equals("PENDIENTE") || estado.equals("EN_PROCESO") || estado.equals("RESUELTA"))) {
            System.out.println("Estado inválido.");
        } else {
            solicitudEncontrada.setEstado(estado);
            System.out.println("Estado actualizado correctamente.");
        }
    }

    private void mostrarReporte() {
        int pendientes = 0;
        int enProceso = 0;
        int resueltas = 0;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstado().equals("PENDIENTE")) {
                pendientes++;
            }
            if (solicitud.getEstado().equals("EN_PROCESO")) {
                enProceso++;
            }
            if (solicitud.getEstado().equals("RESUELTA")) {
                resueltas++;
            }
        }
        System.out.println("\n--- REPORTE POR ESTADO ---");
        System.out.println("PENDIENTE: " + pendientes + "\nEN_PROCESO: " + enProceso + "\nRESUELTA: " + resueltas);
        System.out.println("TOTAL: " + solicitudes.size());
    }
}

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

    public void registrarSolicitud(int estudianteId, String tipo, String descripcion, String prioridad, String estado, Responsable responsable) {
        Estudiante e = null;
        for (Estudiante x : estudiantes) if (x.getId() == estudianteId) e = x;
        if (e != null) {
            solicitudes.add(new Solicitud(proximaSolicitud, e, tipo, descripcion, prioridad, estado, responsable));
            proximaSolicitud++;
        }
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== SISTEMA DE SOLICITUDES ACADÉMICAS ===");
            System.out.println("1. Registrar estudiante\n2. Registrar solicitud\n3. Listar solicitudes");
            System.out.println("4. Consultar solicitud por ID\n5. Buscar por estado\n6. Buscar por tipo");
            System.out.println("7. Asignar responsable\n8. Cambiar estado\n9. Reporte por estado\n0. Salir");
            System.out.print("Opción: ");
            try { opcion = Integer.parseInt(teclado.nextLine()); }
            catch (NumberFormatException ex) { opcion = -1; }
            if (opcion == 1) altaEstudiante();
            else if (opcion == 2) altaSolicitud();
            else if (opcion == 3) listarSolicitudes();
            else if (opcion == 4) consultarSolicitud();
            else if (opcion == 5) buscarPorEstado();
            else if (opcion == 6) buscarPorTipo();
            else if (opcion == 7) asignarResponsable();
            else if (opcion == 8) cambiarEstado();
            else if (opcion == 9) mostrarReporte();
            else if (opcion == 0) System.out.println("Hasta luego.");
            else System.out.println("Opción inválida.");
        }
    }

    private void altaEstudiante() {
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine().trim();
        System.out.print("Email: ");
        String email = teclado.nextLine().trim();
        if (nombre.isEmpty() || email.isEmpty() || !email.contains("@"))
            System.out.println("Datos inválidos. El nombre y el email son obligatorios.");
        else {
            registrarEstudiante(nombre, email);
            System.out.println("Estudiante registrado correctamente.");
        }
    }

    private void altaSolicitud() {
        for (Estudiante e : estudiantes) System.out.println(e);
        System.out.print("ID del estudiante: ");
        int id;
        try { id = Integer.parseInt(teclado.nextLine()); }
        catch (NumberFormatException ex) { System.out.println("ID inválido."); return; }
        Estudiante encontrado = null;
        for (Estudiante e : estudiantes) if (e.getId() == id) encontrado = e;
        if (encontrado == null) { System.out.println("No existe el estudiante."); return; }
        System.out.print("Tipo (CERTIFICADO/EQUIVALENCIA/EXAMEN/OTRA): ");
        String tipo = teclado.nextLine().trim().toUpperCase();
        System.out.print("Descripción: ");
        String descripcion = teclado.nextLine().trim();
        System.out.print("Prioridad (BAJA/MEDIA/ALTA): ");
        String prioridad = teclado.nextLine().trim().toUpperCase();
        if (tipo.isEmpty() || descripcion.isEmpty() || !(prioridad.equals("BAJA") || prioridad.equals("MEDIA") || prioridad.equals("ALTA"))) {
            System.out.println("Tipo, descripción o prioridad inválidos."); return;
        }
        registrarSolicitud(id, tipo, descripcion, prioridad, "PENDIENTE", null);
        System.out.println("Solicitud registrada correctamente.");
    }

    private void listarSolicitudes() {
        if (solicitudes.isEmpty()) System.out.println("No hay solicitudes.");
        for (Solicitud s : solicitudes) System.out.println(s);
    }

    private void consultarSolicitud() {
        System.out.print("ID de la solicitud: ");
        int id;
        try { id = Integer.parseInt(teclado.nextLine()); }
        catch (NumberFormatException ex) { System.out.println("ID inválido."); return; }
        Solicitud x = null;
        for (Solicitud s : solicitudes) if (s.getId() == id) x = s;
        if (x == null) System.out.println("No existe la solicitud."); else System.out.println(x);
    }

    private void buscarPorEstado() {
        System.out.print("Estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        String valor = teclado.nextLine().trim().toUpperCase();
        boolean hay = false;
        for (Solicitud s : solicitudes) if (s.getEstado().equals(valor)) { System.out.println(s); hay = true; }
        if (!hay) System.out.println("No se encontraron solicitudes.");
    }

    private void buscarPorTipo() {
        System.out.print("Tipo a buscar: ");
        String valor = teclado.nextLine().trim().toUpperCase();
        boolean hay = false;
        for (Solicitud s : solicitudes) if (s.getTipo().equals(valor)) { System.out.println(s); hay = true; }
        if (!hay) System.out.println("No se encontraron solicitudes.");
    }

    private void asignarResponsable() {
        System.out.print("ID de la solicitud: ");
        int id;
        try { id = Integer.parseInt(teclado.nextLine()); }
        catch (NumberFormatException ex) { System.out.println("ID inválido."); return; }
        Solicitud solicitud = null;
        for (Solicitud s : solicitudes) if (s.getId() == id) solicitud = s;
        if (solicitud == null) { System.out.println("No existe la solicitud."); return; }
        for (Responsable r : responsables) System.out.println(r);
        System.out.print("ID del responsable: ");
        int responsableId;
        try { responsableId = Integer.parseInt(teclado.nextLine()); }
        catch (NumberFormatException ex) { System.out.println("ID inválido."); return; }
        Responsable responsable = null;
        for (Responsable r : responsables) if (r.getId() == responsableId) responsable = r;
        if (responsable == null) System.out.println("No existe el responsable.");
        else { solicitud.setResponsable(responsable); System.out.println("Responsable asignado correctamente."); }
    }

    private void cambiarEstado() {
        System.out.print("ID de la solicitud: ");
        int id;
        try { id = Integer.parseInt(teclado.nextLine()); }
        catch (NumberFormatException ex) { System.out.println("ID inválido."); return; }
        Solicitud solicitud = null;
        for (Solicitud s : solicitudes) if (s.getId() == id) solicitud = s;
        if (solicitud == null) { System.out.println("No existe la solicitud."); return; }
        System.out.print("Nuevo estado (PENDIENTE/EN_PROCESO/RESUELTA): ");
        String estado = teclado.nextLine().trim().toUpperCase();
        if (!(estado.equals("PENDIENTE") || estado.equals("EN_PROCESO") || estado.equals("RESUELTA"))) System.out.println("Estado inválido.");
        else { solicitud.setEstado(estado); System.out.println("Estado actualizado correctamente."); }
    }

    private void mostrarReporte() {
        int pendientes = 0, enProceso = 0, resueltas = 0;
        for (Solicitud s : solicitudes) {
            if (s.getEstado().equals("PENDIENTE")) pendientes++;
            if (s.getEstado().equals("EN_PROCESO")) enProceso++;
            if (s.getEstado().equals("RESUELTA")) resueltas++;
        }
        System.out.println("\n--- REPORTE POR ESTADO ---");
        System.out.println("PENDIENTE: " + pendientes + "\nEN_PROCESO: " + enProceso + "\nRESUELTA: " + resueltas);
        System.out.println("TOTAL: " + solicitudes.size());
    }
}

package ar.edu.metodologia;

public class DatosIniciales {

    public static void cargar(EstudianteService estudianteService, ResponsableService responsableService,
                              SolicitudService solicitudService) {
        estudianteService.registrarEstudiante("Ana López", "ana.lopez@alumnos.edu.ar");
        estudianteService.registrarEstudiante("Bruno Díaz", "bruno.diaz@alumnos.edu.ar");
        estudianteService.registrarEstudiante("Carla Méndez", "carla.mendez@alumnos.edu.ar");

        Responsable laura = new Responsable(1, "Laura Gómez", "Alumnado");
        Responsable martin = new Responsable(2, "Martín Ruiz", "Secretaría Académica");
        responsableService.agregarResponsable(laura);
        responsableService.agregarResponsable(martin);

        solicitudService.registrarSolicitud(new DatosSolicitud(1, "CERTIFICADO",
                "Solicita certificado de alumno regular", Prioridad.MEDIA));

        Solicitud equivalencia = solicitudService.registrarSolicitud(new DatosSolicitud(2, "EQUIVALENCIA",
                "Equivalencia de Programación I", Prioridad.ALTA));
        solicitudService.cambiarEstado(equivalencia.getId(), EstadoSolicitud.EN_PROCESO);
        solicitudService.asignarResponsable(equivalencia.getId(), martin);

        Solicitud examen = solicitudService.registrarSolicitud(new DatosSolicitud(3, "EXAMEN",
                "Inscripción fuera de término", Prioridad.ALTA));
        solicitudService.cambiarEstado(examen.getId(), EstadoSolicitud.RESUELTA);
        solicitudService.asignarResponsable(examen.getId(), laura);
    }
}

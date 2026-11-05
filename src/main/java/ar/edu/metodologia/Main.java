package ar.edu.metodologia;

public class Main {
    public static void main(String[] args) {
        EstudianteService estudianteService = new EstudianteService();
        ResponsableService responsableService = new ResponsableService();
        SolicitudService solicitudService = new SolicitudService(new SolicitudRepositorioEnMemoria(), estudianteService);

        DatosIniciales.cargar(estudianteService, responsableService, solicitudService);

        Sistema sistema = new Sistema(estudianteService, responsableService, solicitudService);
        sistema.iniciar();
    }
}

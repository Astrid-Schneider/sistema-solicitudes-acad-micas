package ar.edu.metodologia;

import java.util.ArrayList;
import java.util.List;

public class EstudianteService {
    private List<Estudiante> estudiantes = new ArrayList<>();
    private int proximoEstudiante = 1;

    public boolean datosValidos(String nombre, String email) {
        return !nombre.isEmpty() && !email.isEmpty() && email.contains("@");
    }

    public void registrarEstudiante(String nombre, String email) {
        estudiantes.add(new Estudiante(proximoEstudiante, nombre, email));
        proximoEstudiante++;
    }

    public Estudiante buscarEstudiantePorId(int estudianteId) {
        for (Estudiante estudiante : estudiantes) {
            if (estudiante.getId() == estudianteId) {
                return estudiante;
            }
        }
        return null;
    }

    public List<Estudiante> listarEstudiantes() {
        return estudiantes;
    }
}

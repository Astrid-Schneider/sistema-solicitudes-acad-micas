package ar.edu.metodologia;

import java.util.ArrayList;
import java.util.List;

public class ResponsableService {
    private List<Responsable> responsables = new ArrayList<>();

    public void agregarResponsable(Responsable responsable) {
        responsables.add(responsable);
    }

    public Responsable buscarResponsablePorId(int responsableId) {
        for (Responsable responsable : responsables) {
            if (responsable.getId() == responsableId) {
                return responsable;
            }
        }
        return null;
    }

    public List<Responsable> listarResponsables() {
        return responsables;
    }
}

package formation.model;

import java.util.HashMap;
import java.util.Map;

public class Inscription {
    
    private int etudiantId;
    private int formationId;
    private Map<Integer, Double> notesParModule; // moduleId → note

    public Inscription(int etudiantId, int formationId) {
        this.etudiantId = etudiantId;
        this.formationId = formationId;
        this.notesParModule = new HashMap<>();
    }

    // Ajouter une note pour un module
    public void ajouterNote(int moduleId, double note) {
        notesParModule.put(moduleId, note);
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }

    public Map<Integer, Double> getNotesParModule() {
        return notesParModule;
    }

    public void setNotesParModule(Map<Integer, Double> notesParModule) {
        this.notesParModule = notesParModule;
    }

    
}

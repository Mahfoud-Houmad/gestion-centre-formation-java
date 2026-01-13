package formation.service;

import java.io.IOException;
import java.util.List;

import formation.dao.FormationDAO;
import formation.dao.InscriptionDAO;
import formation.model.Formation;
import formation.model.Inscription;

public class InscriptionService {
    
    private InscriptionDAO inscriptionDAO = new InscriptionDAO();
    private FormationDAO formationDAO = new FormationDAO();

    // Inscrire un étudiant à une formation
    public void inscrireEtudiant(int etudiantId, int formationId) throws IOException {
        List<Inscription> inscriptions = inscriptionDAO.readAll();
        // Vérifier si l'inscription existe déjà
        boolean existeDeja = inscriptions.stream()
            .anyMatch(i -> i.getEtudiantId() == etudiantId && i.getFormationId() == formationId);
        if (existeDeja) {
            throw new IllegalArgumentException("Étudiant déjà inscrit !");
        }

        // Vérifier la capacité de la formation
        Formation formation = formationDAO.readAll().stream()
            .filter(f -> f.getId() == formationId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Formation inexistante !"));
        
        long nbInscrits = inscriptions.stream()
            .filter(i -> i.getFormationId() == formationId)
            .count();
        
        if (nbInscrits >= formation.getCapacite()) {
            throw new IllegalStateException("Formation complète !");
        }

        // Ajouter l'inscription
        inscriptions.add(new Inscription(etudiantId, formationId));
        inscriptionDAO.saveAll(inscriptions);
    }

    // Attribuer une note à un étudiant pour un module
    public void attribuerNote(int etudiantId, int formationId, int moduleId, double note) throws IOException {
        List<Inscription> inscriptions = inscriptionDAO.readAll();
        for (Inscription inscription : inscriptions) {
            if (inscription.getEtudiantId() == etudiantId && inscription.getFormationId() == formationId) {
                inscription.ajouterNote(moduleId, note);
                inscriptionDAO.saveAll(inscriptions);
                return;
            }
        }
        throw new IllegalArgumentException("Inscription non trouvée !");
    }

    public List<Inscription> getInscriptionsByEtudiant(int etudiantId) throws IOException {
    List<Inscription> inscriptions = inscriptionDAO.readAll();
    return inscriptions.stream()
        .filter(i -> i.getEtudiantId() == etudiantId)
        .toList();
}
}

package formation.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import formation.dao.FormationDAO;
import formation.dao.InscriptionDAO;
import formation.model.Formation;
import formation.model.Inscription;

public class FormationService {
    
    private FormationDAO formationDAO = new FormationDAO();
    private InscriptionDAO inscriptionDAO = new InscriptionDAO();
    private final ModuleService moduleService = new ModuleService();
    public void ajouterModuleAFormation(int formationId, String nom, String description) throws IOException {
        moduleService.creerModule(nom, description, formationId);
    }
    // Créer une nouvelle formation
    public void creerFormation(String nom, String description, String dateDebut, String dateFin, int capacite) throws IOException {
        List<Formation> formations = formationDAO.readAll();
        int newId = formations.isEmpty() ? 1 : formations.get(formations.size() - 1).getId() + 1;
        Formation formation = new Formation(
            newId, 
            nom, 
            description, 
            java.time.LocalDate.parse(dateDebut), 
            java.time.LocalDate.parse(dateFin), 
            capacite
        );
        formations.add(formation);
        formationDAO.saveAll(formations);
    }

    // Supprimer une formation (et ses inscriptions)
    public void supprimerFormation(int formationId) throws IOException {
        List<Formation> formations = formationDAO.readAll();
        formations.removeIf(f -> f.getId() == formationId);
        formationDAO.saveAll(formations);

        // Supprimer les inscriptions liées
        List<Inscription> inscriptions = inscriptionDAO.readAll();
        inscriptions.removeIf(i -> i.getFormationId() == formationId);
        inscriptionDAO.saveAll(inscriptions);
    }

    // Lister toutes les formations
    public List<Formation> listerFormations() throws IOException {
        return formationDAO.readAll();
    }

    public void modifierFormation(int formationId, String nouveauNom, String nouvelleDescription, LocalDate nouvelleDateDebut, LocalDate nouvelleDateFin, int nouvelleCapacite) throws IOException {
    List<Formation> formations = formationDAO.readAll();
    for (Formation formation : formations) {
        if (formation.getId() == formationId) {
            formation.setNom(nouveauNom);
            formation.setDescription(nouvelleDescription);
            formation.setDateDebut(nouvelleDateDebut);
            formation.setDateFin(nouvelleDateFin);
            formation.setCapacite(nouvelleCapacite);
            break;
        }
    }
    formationDAO.saveAll(formations);
    }

    public Formation getFormationById(int formationId) throws IOException {
    List<Formation> formations = formationDAO.readAll();
    return formations.stream()
        .filter(f -> f.getId() == formationId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Formation introuvable !"));
    }
}

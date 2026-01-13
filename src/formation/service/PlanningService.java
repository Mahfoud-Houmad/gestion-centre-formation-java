package formation.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import formation.dao.SalleDAO;
import formation.dao.SessionDAO;
import formation.model.Inscription;
import formation.model.Session;

public class PlanningService {
    private final SessionDAO sessionDAO = new SessionDAO();
    private final SalleDAO salleDAO = new SalleDAO();
    private final InscriptionService inscriptionService = new InscriptionService();

    /**
     * Crée une nouvelle session de formation après validation.
     */
    public void creerSession(LocalDateTime heureDebut, LocalDateTime heureFin, String jourSemaine,
            int salleId, int formateurId, int formationId) throws IOException {
        // Validation de l'existence de la salle
        validerExistenceSalle(salleId);

        // Vérification des conflits
        if (salleEstOccupee(salleId, heureDebut, heureFin)) {
            throw new IllegalStateException("Conflit de salle !");
        }
        if (formateurEstOccupe(formateurId, heureDebut, heureFin)) {
            throw new IllegalStateException("Formateur indisponible !");
        }

        // Création de la session
        List<Session> sessions = sessionDAO.readAll();
        int newId = genererNouvelId(sessions);

        sessions.add(new Session(
                newId, heureDebut, heureFin, jourSemaine,
                salleId, formateurId, formationId));

        sessionDAO.saveAll(sessions);
    }

    /**
     * Valide que la salle existe dans le système.
     */
    private void validerExistenceSalle(int salleId) throws IOException {
        boolean salleExiste = salleDAO.readAll().stream()
                .anyMatch(s -> s.getId() == salleId);

        if (!salleExiste) {
            throw new IllegalArgumentException("Salle introuvable !");
        }
    }

    /**
     * Vérifie si la salle est déjà réservée pendant la période demandée.
     */
    private boolean salleEstOccupee(int salleId, LocalDateTime debut, LocalDateTime fin) throws IOException {
        return sessionDAO.readAll().stream()
                .filter(s -> s.getSalleId() == salleId)
                .anyMatch(s -> (s.getHeureDebut().isBefore(fin) &&
                        s.getHeureFin().isAfter(debut)));
    }

    /**
     * Vérifie si le formateur a déjà une session pendant cette période.
     */
    private boolean formateurEstOccupe(int formateurId, LocalDateTime debut, LocalDateTime fin) throws IOException {
        return sessionDAO.readAll().stream()
                .filter(s -> s.getFormateurId() == formateurId)
                .anyMatch(s -> (s.getHeureDebut().isBefore(fin) &&
                        s.getHeureFin().isAfter(debut)));
    }

    /**
     * Génère un nouvel ID incrémental.
     */
    private int genererNouvelId(List<Session> sessions) {
        return sessions.stream()
                .mapToInt(Session::getId)
                .max()
                .orElse(0) + 1;
    }

    public List<Session> getSessionsByFormation(int formationId) throws IOException {
        return sessionDAO.readAll().stream()
                .filter(s -> s.getFormationId() == formationId)
                .toList();
    }

    public List<Session> getSessionsByEtudiant(int etudiantId, LocalDate debutSemaine, LocalDate finSemaine)
            throws IOException {
        // 1. Récupérer les IDs des formations de l'étudiant
        List<Integer> formationIds = inscriptionService.getInscriptionsByEtudiant(etudiantId)
                .stream()
                .map(Inscription::getFormationId)
                .toList();

        System.out.println("[DEBUG] Formations de l'étudiant: " + formationIds);

        // 2. Récupérer toutes les sessions
        List<Session> allSessions = sessionDAO.readAll();
        System.out.println("[DEBUG] Toutes les sessions: " + allSessions.size());

        // 3. Filtrer
        return allSessions.stream()
                .filter(s -> formationIds.contains(s.getFormationId()))
                .filter(s -> {
                    LocalDate dateSession = s.getHeureDebut().toLocalDate();
                    boolean dansLaSemaine = !dateSession.isBefore(debutSemaine) && !dateSession.isAfter(finSemaine);
                    System.out.println("[DEBUG] Session " + s.getId() + " date: " + dateSession + " dans semaine: "
                            + dansLaSemaine);
                    return dansLaSemaine;
                })
                .sorted(Comparator.comparing(Session::getHeureDebut))
                .toList();
    }
}

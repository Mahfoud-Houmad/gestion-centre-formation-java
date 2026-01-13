package formation.app;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import formation.dao.SalleDAO;
import formation.dao.UserDAO;
import formation.model.Etudiant;
import formation.model.Formateur;
import formation.model.Formation;
import formation.model.Inscription;
import formation.model.Salle;
import formation.model.Session;
import formation.model.User;
import formation.model.Module;

import formation.service.*;;

public class ApplicationConsole {

    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static FormationService formationService = new FormationService();
    private static InscriptionService inscriptionService = new InscriptionService();
    private static PlanningService planningService = new PlanningService();
    private static UserDAO userDAO = new UserDAO();
    private static SalleDAO salleDAO = new SalleDAO();
    private static ModuleService moduleService = new ModuleService();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. S'inscrire");
            System.out.println("2. Se connecter");
            System.out.println("3. Quitter");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    inscrireEtudiant();
                    break;
                case 2:
                    User user = connecter();
                    if (user != null) {
                        switch (user.getRole()) {
                            case "STUDENT":
                                menuEtudiant((Etudiant) user);
                                break;
                            case "PROFESSOR":
                                menuFormateur((Formateur) user);
                                break;
                            case "ADMIN":
                                menuAdmin();
                                break;
                        }
                    }
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        }
        scanner.close();
    }

    private static void inscrireEtudiant() {
        System.out.println("\n--- Inscription ---");
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        try {
            authService.registerEtudiant(nom, prenom, email, password);
            System.out.println("Inscription réussie !");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private static void voirPlanningSemaine(Etudiant etudiant) {
        try {
            System.out.println("\n--- Mon Planning Hebdomadaire ---");

            // 1. Définir la semaine (du lundi au dimanche)
            LocalDate aujourdhui = LocalDate.now(); // Date fixe pour test
            LocalDate debutSemaine = aujourdhui.with(DayOfWeek.MONDAY);
            LocalDate finSemaine = debutSemaine.plusDays(6);

            // System.out.println("[DEBUG] Semaine du " + debutSemaine + " au " +
            // finSemaine);

            // 2. Récupérer les sessions
            List<Session> sessions = planningService.getSessionsByEtudiant(etudiant.getId(), debutSemaine, finSemaine);
            // System.out.println("[DEBUG] Sessions filtrées: " + sessions.size());

            if (sessions.isEmpty()) {
                System.out.println("\nAucun cours prévu cette semaine.");
                return;
            }

            // 3. Afficher par jour
            Map<DayOfWeek, List<Session>> sessionsParJour = sessions.stream()
                    .collect(Collectors.groupingBy(s -> s.getHeureDebut().getDayOfWeek()));

            for (DayOfWeek jour : DayOfWeek.values()) {
                List<Session> sessionsDuJour = sessionsParJour.getOrDefault(jour, Collections.emptyList());

                if (!sessionsDuJour.isEmpty()) {
                    System.out.println("\n" + jour.toString().toUpperCase() + ":");

                    for (Session session : sessionsDuJour) {
                        Formation formation = formationService.getFormationById(session.getFormationId());
                        Salle salle = salleDAO.getById(session.getSalleId());

                        System.out.printf(
                                "%s - %s | %s | %s\n",
                                session.getHeureDebut().toLocalTime(),
                                session.getHeureFin().toLocalTime(),
                                formation.getNom(),
                                salle.getNom());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur d'affichage du planning : ");
            e.printStackTrace();
        }
    }

    private static User connecter() {
        System.out.println("\n--- Connexion ---");
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(email, password);
            if (user == null)
                System.out.println("Identifiants incorrects !");
            return user;
        } catch (IOException e) {
            System.out.println("Erreur de lecture des données !");
            return null;
        }
    }

    private static void menuEtudiant(Etudiant etudiant) {
        boolean deconnecte = false;
        while (!deconnecte) {
            System.out.println("\n--- Espace Étudiant ---");
            System.out.println("1. Consulter les formations");
            System.out.println("2. Voir mes inscriptions");
            System.out.println("3. Voir mon planning de la semaine");
            System.out.println("4. Déconnexion");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    consulterFormations(etudiant);
                    break;
                case 2:
                    voirInscriptions(etudiant);
                    break;
                case 3:
                    voirPlanningSemaine(etudiant);
                    break;
                case 4:
                    deconnecte = true;
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        }
    }

    private static void consulterFormations(Etudiant etudiant) {
        try {
            List<Formation> formations = formationService.listerFormations();
            System.out.println("\n--- Formations Disponibles ---");
            formations.forEach(f -> System.out.printf(
                    "%d. %s (%s) - Capacité: %d\n",
                    f.getId(), f.getNom(), f.getDescription(), f.getCapacite()));

            System.out.print("\nChoisir une formation (0 pour annuler) : ");
            int formationId = scanner.nextInt();
            scanner.nextLine();

            if (formationId != 0) {
                try {
                    inscriptionService.inscrireEtudiant(etudiant.getId(), formationId);
                    System.out.println("Inscription réussie !");
                } catch (Exception e) {
                    System.out.println("   Erreur : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("  Erreur de lecture des formations !");
        }
    }

    private static void voirInscriptions(Etudiant etudiant) {
        try {
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(etudiant.getId());
            if (inscriptions.isEmpty()) {
                System.out.println("\n Aucune inscription trouvée.");
                return;
            }

            System.out.println("\n--- Mes Formation  ---");
            for (Inscription inscription : inscriptions) {
                Formation formation = formationService.getFormationById(inscription.getFormationId());
                System.out.println("\nFormation : " + formation.getNom());

                // Récupérer tous les modules de la formation
                List<formation.model.Module> modules = moduleService.listerModulesParFormation(formation.getId());

                // Afficher chaque note avec le nom du module
                for (Map.Entry<Integer, Double> entry : inscription.getNotesParModule().entrySet()) {
                    int moduleId = entry.getKey();
                    double note = entry.getValue();

                    // Trouver le module correspondant
                    String nomModule = "Module Inconnu";
                    for (formation.model.Module module : modules) {
                        if (module.getId() == moduleId) {
                            nomModule = module.getNom();
                            break;
                        }
                    }

                    System.out.printf("- %s : %.1f/20\n", nomModule, note);
                }
            }
        } catch (IOException e) {
            System.out.println(" Erreur de lecture des données : " + e.getMessage());
        }
    }

    private static void menuFormateur(Formateur formateur) {
        boolean deconnecte = false;
        while (!deconnecte) {
            System.out.println("\n--- Espace Formateur ---");
            System.out.println("1. Modifier une formation");
            System.out.println("2. Attribuer une note");
            System.out.println("3. Déconnexion");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    modifierFormation();
                    break;
                case 2:
                    attribuerNote();
                    break;
                case 3:
                    deconnecte = true;
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        }
    }

    private static void modifierFormation() {
        try {
            List<Formation> formations = formationService.listerFormations();
            System.out.println("\n--- Liste des Formations ---");
            formations.forEach(f -> System.out.printf("%d. %s\n", f.getId(), f.getNom()));

            System.out.print("ID de la formation à modifier : ");
            int id = scanner.nextInt();
            scanner.nextLine();

            Formation formation;
            try {
                formation = formationService.getFormationById(id);
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
                return;
            }
            System.out.print("Nouveau nom [" + formation.getNom() + "] : ");
            String nom = scanner.nextLine();
            System.out.print("Nouvelle description [" + formation.getDescription() + "] : ");
            String description = scanner.nextLine();
            System.out.print("Nouvelle date de début (AAAA-MM-JJ) [" + formation.getDateDebut() + "] : ");
            LocalDate dateDebut = LocalDate.parse(scanner.nextLine());
            System.out.print("Nouvelle date de fin (AAAA-MM-JJ) [" + formation.getDateFin() + "] : ");
            LocalDate dateFin = LocalDate.parse(scanner.nextLine());
            System.out.print("Nouvelle capacité [" + formation.getCapacite() + "] : ");
            int capacite = scanner.nextInt();
            scanner.nextLine();

            formationService.modifierFormation(id, nom, description, dateDebut, dateFin, capacite);
            System.out.println("Formation mise à jour !");
        } catch (IOException | DateTimeParseException e) {
            System.out.println("   Erreur : " + e.getMessage());
        }
    }

    private static void attribuerNote() {
        try {
            // 1. Afficher les étudiants
            List<User> users = userDAO.readAll();
            List<Etudiant> etudiants = users.stream()
                    .filter(u -> u instanceof Etudiant)
                    .map(u -> (Etudiant) u)
                    .toList();

            System.out.println("\n--- Liste des Étudiants ---");
            etudiants.forEach(e -> System.out.printf("%d - %s\n", e.getId(), e.getNom()));

            System.out.print("ID de l'étudiant : ");
            int etudiantId = scanner.nextInt();
            scanner.nextLine();

            // 2. Afficher les formations où il est inscrit
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(etudiantId);
            if (inscriptions.isEmpty()) {
                System.out.println(" Cet étudiant n'est inscrit à aucune formation.");
                return;
            }

            System.out.println("\n--- Formations Inscrites ---");
            for (Inscription i : inscriptions) {
                Formation f = formationService.getFormationById(i.getFormationId());
                System.out.printf("%d - %s\n", f.getId(), f.getNom());
            }

            System.out.print("ID de la formation : ");
            int formationId = scanner.nextInt();
            scanner.nextLine();

            // 3. Afficher les modules liés à cette formation
            List<Module> modules = moduleService.listerModulesParFormation(formationId);
            if (modules.isEmpty()) {
                System.out.println("Aucun module associé à cette formation.");
                return;
            }

            System.out.println("\n--- Modules de la Formation ---");
            modules.forEach(m -> System.out.printf("%d - %s\n", m.getId(), m.getNom()));

            System.out.print("ID du module : ");
            int moduleId = scanner.nextInt();

            // Vérifier si ce module appartient bien à la formation
            boolean moduleValide = modules.stream().anyMatch(m -> m.getId() == moduleId);
            if (!moduleValide) {
                System.out.println("Le module ne correspond pas à la formation choisie.");
                return;
            }

            // 4. Saisir la note
            System.out.print("Note (0-20) : ");
            double note = scanner.nextDouble();
            scanner.nextLine();

            if (note < 0 || note > 20) {
                System.out.println("Note invalide !");
                return;
            }

            // 5. Attribuer la note
            inscriptionService.attribuerNote(etudiantId, formationId, moduleId, note);
            System.out.println(" Note attribuée avec succès !");
        } catch (Exception e) {
            System.out.println(" Erreur : " + e.getMessage());
        }
    }

    private static void menuAdmin() {
        boolean deconnecte = false;
        while (!deconnecte) {
            System.out.println("\n--- Espace Admin ---");
            System.out.println("1. Ajouter un formateur");
            System.out.println("2. Supprimer un formateur");
            System.out.println("3. Supprimer un étudiant");
            System.out.println("4. Gérer le planning");
            System.out.println("5. Gérer les salles");
            System.out.println("6. Ajouter un module à une formation");
            System.out.println("7. Modifier une formation ");
            System.out.println("8. Attribuer une note ");
            System.out.println("9. Ajouter une formation");
            System.out.println("10. Déconnexion");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterFormateur();
                    break;
                case 2:
                    supprimerFormateur();
                    break;
                case 3:
                    supprimerEtudiant();
                    break;
                case 4:
                    gererPlanning();
                    break;
                case 5:
                    gererSalles();
                    break;
                case 6:
                    ajouterModuleAFormation();
                    break;
                case 7:
                    modifierFormation();
                    break;
                case 8:
                    attribuerNote();
                    break;
                case 9:
                    ajouterFormation();
                    break;
                case 10:
                    deconnecte = true;
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        }
    }

    private static void ajouterFormation() {
        try {

            System.out.print("Nom de la formation : ");
            String nom = scanner.nextLine();

            System.out.print("Description : ");
            String description = scanner.nextLine();

            System.out.print("Date de début (AAAA-MM-JJ) : ");
            String dateDebut = scanner.nextLine();

            System.out.print("Date de fin (AAAA-MM-JJ) : ");
            String dateFin = scanner.nextLine();

            System.out.print("Capacité : ");
            String capaciteStr = scanner.nextLine();
            if (capaciteStr.trim().isEmpty()) {
                System.out.println("Capacité vide. Annulation.");
                return;
            }
            int capacite = Integer.parseInt(capaciteStr);
            if (capacite <= 0) {
                System.out.println("Capacité invalide. Elle doit être > 0.");
                return;
            }

            formationService.creerFormation(nom, description, dateDebut, dateFin, capacite);
            System.out.println("Formation ajoutée avec succès !");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : La capacité doit être un nombre entier.");
        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getClass().getSimpleName() + " → " + e.getMessage());

        }
    }

    private static void ajouterModuleAFormation() {
        try {
            List<Formation> formations = formationService.listerFormations();
            System.out.println("\n--- Formations Disponibles ---");
            formations.forEach(f -> System.out.printf("%d - %s\n", f.getId(), f.getNom()));

            System.out.print("ID de la formation : ");
            int formationId = scanner.nextInt();
            scanner.nextLine();
            boolean formationExiste = formations.stream().anyMatch(f -> f.getId() == formationId);
            if (!formationExiste) {
                System.out.println("Erreur : formation introuvable.");
                return;
            }

            System.out.print("Nom du module : ");
            String nom = scanner.nextLine();

            System.out.print("Description du module : ");
            String description = scanner.nextLine();

            formationService.ajouterModuleAFormation(formationId, nom, description);
            System.out.println("Module ajouté à la formation !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private static void ajouterFormateur() {
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        try {
            authService.ajouterFormateur(nom, prenom, email, password);
            System.out.println("Formateur ajouté !");
        } catch (Exception e) {
            System.out.println("   Erreur : " + e.getMessage());
        }
    }

    private static void supprimerFormateur() {
        try {
            List<Formateur> formateurs = userDAO.getFormateurs();
            System.out.println("\n--- Liste des Formateurs ---");
            formateurs.forEach(f -> System.out.printf("%d. %s %s\n", f.getId(), f.getPrenom(), f.getNom()));

            System.out.print("ID du formateur à supprimer : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            boolean existe = formateurs.stream().anyMatch(f -> f.getId() == id);
            if (!existe) {
                System.out.println("Aucun formateur trouvé avec cet ID.");
                return;
            }

            userDAO.supprimerFormateur(id);
            System.out.println("Formateur supprimé !");
        } catch (Exception e) {
            System.out.println("   Erreur : " + e.getMessage());
        }
    }

    private static void supprimerEtudiant() {
        try {
            List<Etudiant> etudiants = userDAO.getEtudiants();
            System.out.println("\n--- Liste des Étudiants ---");
            etudiants.forEach(e -> System.out.printf("%d. %s %s\n", e.getId(), e.getPrenom(), e.getNom()));

            System.out.print("ID de l'étudiant à supprimer : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            boolean existe = etudiants.stream().anyMatch(e -> e.getId() == id);
            if (!existe) {
                System.out.println("Aucun étudiant trouvé avec cet ID.");
                return;
            }

            userDAO.supprimerUser(id);
            System.out.println("Étudiant supprimé !");
        } catch (Exception e) {
            System.out.println("   Erreur : " + e.getMessage());
        }
    }

    private static void gererPlanning() {
        try {
            // 🔹 Afficher les formations disponibles
            List<Formation> formations = formationService.listerFormations();
            System.out.println("\n--- Formations Disponibles ---");
            formations.forEach(f -> System.out.printf("%d - %s\n", f.getId(), f.getNom()));

            System.out.print("ID de la formation : ");
            int formationId = scanner.nextInt();
            scanner.nextLine();
            boolean formationExiste = formations.stream().anyMatch(f -> f.getId() == formationId);
            if (!formationExiste) {
                System.out.println("Formation introuvable.");
                return;
            }

            // 🔹 Afficher les salles disponibles
            List<Salle> salles = salleDAO.readAll();
            System.out.println("\n--- Salles Disponibles ---");
            salles.forEach(s -> System.out.printf("%d - %s\n", s.getId(), s.getNom()));

            System.out.print("ID de la salle : ");
            int salleId = scanner.nextInt();
            scanner.nextLine();
            boolean salleExiste = salles.stream().anyMatch(s -> s.getId() == salleId);
            if (!salleExiste) {
                System.out.println("Salle introuvable.");
                return;
            }

            // 🔹 Afficher les formateurs disponibles
            List<User> users = userDAO.readAll();
            List<Formateur> formateurs = users.stream()
                    .filter(u -> u instanceof Formateur)
                    .map(u -> (Formateur) u)
                    .toList();

            System.out.println("\n--- Formateurs Disponibles ---");
            formateurs.forEach(f -> System.out.printf("%d - %s\n", f.getId(), f.getNom()));

            System.out.print("ID du formateur : ");
            int formateurId = scanner.nextInt();
            scanner.nextLine();
            boolean formateurExiste = formateurs.stream().anyMatch(f -> f.getId() == formateurId);
            if (!formateurExiste) {
                System.out.println("Formateur introuvable.");
                return;
            }

            // 🔹 Dates et horaire de la session
            System.out.print("Date/heure de début (AAAA-MM-JJTHH:MM) : ");
            LocalDateTime debut = LocalDateTime.parse(scanner.nextLine());

            System.out.print("Date/heure de fin (AAAA-MM-JJTHH:MM) : ");
            LocalDateTime fin = LocalDateTime.parse(scanner.nextLine());

            System.out.print("Jour de la semaine : ");
            String jour = scanner.nextLine();

            // 🔹 Création de la session
            planningService.creerSession(debut, fin, jour, salleId, formateurId, formationId);
            System.out.println(" Session ajoutée avec succès !");
        } catch (DateTimeParseException e) {
            System.out.println(" Format de date invalide !");
        } catch (Exception e) {
            System.out.println(" Erreur : " + e.getMessage());
        }
    }

    private static void gererSalles() {
        try {
            System.out.println("\n--- Gestion des Salles ---");
            System.out.println("1. Ajouter une salle");
            System.out.println("2. Lister les salles");
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Nom de la salle : ");
                    String nom = scanner.nextLine();
                    System.out.print("Capacité : ");
                    int capacite = scanner.nextInt();
                    scanner.nextLine();
                    if (capacite <= 0) {
                        System.out.println("Capacité invalide. Elle doit être supérieure à 0.");
                        return;
                    }

                    List<Salle> salles = salleDAO.readAll();
                    int newId = salles.isEmpty() ? 1 : salles.get(salles.size() - 1).getId() + 1;
                    salles.add(new Salle(newId, nom, capacite));
                    salleDAO.saveAll(salles);
                    System.out.println("Salle ajoutée !");
                    break;
                case 2:
                    salles = salleDAO.readAll();
                    System.out.println("\n--- Liste des Salles ---");
                    salles.forEach(s -> System.out.printf(
                            "%d. %s (Capacité: %d)\n", s.getId(), s.getNom(), s.getCapacite()));
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        } catch (Exception e) {
            System.out.println("   Erreur : " + e.getMessage());
        }
    }
}

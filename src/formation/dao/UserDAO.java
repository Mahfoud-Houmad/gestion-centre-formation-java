package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import formation.model.Admin;
import formation.model.Etudiant;
import formation.model.Formateur;
import formation.model.User;

public class UserDAO {
    
    private static final String FILE_PATH = "data/utilisateurs.txt";
    private static final String DELIMITER = "\\|";

    // Charger tous les utilisateurs depuis le fichier
    public List<User> readAll() throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return users;

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int id = Integer.parseInt(parts[0]);
            String nom = parts[1];
            String prenom = parts[2];
            String email = parts[3];
            String password = parts[4];
            String role = parts[5];

            switch (role) {
                case "STUDENT":
                    LocalDate dateInscription = LocalDate.parse(parts[6]);
                    users.add(new Etudiant(id, nom, prenom, email, password, dateInscription));
                    break;
                case "PROFESSOR":
                    users.add(new Formateur(id, nom, prenom, email, password));
                    break;
                case "ADMIN":
                    users.add(new Admin(id, nom, prenom, email, password));
                    break;
            }
        }
        return users;
    }

    // Sauvegarder tous les utilisateurs dans le fichier
    public void saveAll(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            StringBuilder sb = new StringBuilder();
            sb.append(user.getId()).append("|")
              .append(user.getNom()).append("|")
              .append(user.getPrenom()).append("|")
              .append(user.getEmail()).append("|")
              .append(user.getPassword()).append("|")
              .append(user.getRole());

            if (user instanceof Etudiant) {
                Etudiant etudiant = (Etudiant) user;
                sb.append("|").append(etudiant.getDateInscription());
            }

            lines.add(sb.toString());
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }
    
    public void supprimerUser(int userId) throws IOException {
    List<User> users = readAll();
    users.removeIf(user -> user.getId() == userId);
    saveAll(users);
    }

    public void supprimerFormateur(int formateurId) throws IOException {
        List<User> users = readAll();
        users.removeIf(user -> user.getId() == formateurId && user.getRole().equals("PROFESSOR"));
        saveAll(users);
        
        // Nettoyer les sessions liées
        SessionDAO sessionDAO = new SessionDAO();
        sessionDAO.supprimerSessionsParFormateur(formateurId);
    }

    public List<Formateur> getFormateurs() throws IOException {
    List<User> users = readAll();
    return users.stream()
        .filter(u -> u.getRole().equals("PROFESSOR"))
        .map(u -> (Formateur) u)
        .toList();
    }

    public List<Etudiant> getEtudiants() throws IOException {
        List<User> users = readAll();
        return users.stream()
            .filter(u -> u.getRole().equals("STUDENT"))
            .map(u -> (Etudiant) u)
            .toList();
    }
}

package formation.service;

import java.io.IOException;
import java.util.List;

import formation.dao.UserDAO;
import formation.model.Etudiant;
import formation.model.Formateur;
import formation.model.User;

public class AuthService {
    
    private UserDAO userDAO = new UserDAO();

    // Connexion d'un utilisateur
    public User login(String email, String password) throws IOException {
        List<User> users = userDAO.readAll();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user; // Retourne Étudiant/Formateur/Admin
            }
        }
        return null; // Aucun utilisateur trouvé
    }

    // Inscription d'un nouvel étudiant
    public void registerEtudiant(String nom, String prenom, String email, String password) throws IOException {
        List<User> users = userDAO.readAll();
        // Vérifier si l'email existe déjà
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email déjà utilisé !");
            }
        }
        // Créer un nouvel ID
        int newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
        Etudiant etudiant = new Etudiant(newId, nom, prenom, email, password, java.time.LocalDate.now());
        users.add(etudiant);
        userDAO.saveAll(users);
    }
    public void ajouterFormateur(String nom, String prenom, String email, String password) throws IOException {
        List<User> users = userDAO.readAll();
        // Vérifier l'email unique
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email déjà utilisé !");
            }
        }
        int newId = users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
        users.add(new Formateur(newId, nom, prenom, email, password));
        userDAO.saveAll(users);
    }
}

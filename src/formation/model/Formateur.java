package formation.model;

public class Formateur extends User{
    public Formateur(int id, String nom, String prenom, String email, String password) {
        super(id, nom, prenom, email, password, "PROFESSOR");
    }
}

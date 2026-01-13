package formation.model;

public class Admin extends User{
    
     public Admin(int id, String nom, String prenom, String email, String password) {
        super(id, nom, prenom, email, password, "ADMIN");
    }

}

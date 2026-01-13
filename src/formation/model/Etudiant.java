package formation.model;

import java.time.LocalDate;

public class Etudiant extends User{
    private LocalDate dateInscription;

    public Etudiant(int id, String nom, String prenom, String email, String password, LocalDate dateInscription) {
        super(id, nom, prenom, email, password, "STUDENT");
        this.dateInscription = dateInscription;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    
}

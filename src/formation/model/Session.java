package formation.model;

import java.time.LocalDateTime;

public class Session {
    private int id;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private String jourSemaine;
    private int salleId;
    private int formateurId;
    private int formationId;

    public Session(int id, LocalDateTime heureDebut, LocalDateTime heureFin, String jourSemaine, int salleId, int formateurId, int formationId) {
        this.id = id;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.jourSemaine = jourSemaine;
        this.salleId = salleId;
        this.formateurId = formateurId;
        this.formationId = formationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalDateTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalDateTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalDateTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(String jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public int getSalleId() {
        return salleId;
    }

    public void setSalleId(int salleId) {
        this.salleId = salleId;
    }

    public int getFormateurId() {
        return formateurId;
    }

    public void setFormateurId(int formateurId) {
        this.formateurId = formateurId;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }


    
}

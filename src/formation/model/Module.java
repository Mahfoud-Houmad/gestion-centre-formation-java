package formation.model;

public class Module {
    private int id;
    private String nom;
    private String description;
    private int formationId;
    public Module(int id, String nom, String description,int formationId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.formationId = formationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }
    
    
}
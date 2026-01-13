package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import formation.model.Salle;

public class SalleDAO {
    
    private static final String FILE_PATH = "data/salles.txt";
    private static final String DELIMITER = "\\|";

    public List<Salle> readAll() throws IOException {
        List<Salle> salles = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return salles;

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int id = Integer.parseInt(parts[0]);
            String nom = parts[1];
            int capacite = Integer.parseInt(parts[2]);
            salles.add(new Salle(id, nom, capacite));
        }
        return salles;
    }

    public void saveAll(List<Salle> salles) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Salle salle : salles) {
            String line = String.join("|",
                String.valueOf(salle.getId()),
                salle.getNom(),
                String.valueOf(salle.getCapacite())
            );
            lines.add(line);
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }
    public Salle getById(int salleId) throws IOException {
    return readAll().stream()
        .filter(s -> s.getId() == salleId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Salle introuvable"));
    }
}

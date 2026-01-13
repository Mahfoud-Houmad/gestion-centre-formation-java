package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import formation.model.Formation;

public class FormationDAO {
    private static final String FILE_PATH = "data/formations.txt";
    private static final String DELIMITER = "\\|";

    public List<Formation> readAll() throws IOException {
        List<Formation> formations = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return formations;

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int id = Integer.parseInt(parts[0]);
            String nom = parts[1];
            String description = parts[2];
            LocalDate dateDebut = LocalDate.parse(parts[3]);
            LocalDate dateFin = LocalDate.parse(parts[4]);
            int capacite = Integer.parseInt(parts[5]);

            formations.add(new Formation(id, nom, description, dateDebut, dateFin, capacite));
        }
        return formations;
    }

    public void saveAll(List<Formation> formations) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Formation formation : formations) {
            String line = String.join("|",
                String.valueOf(formation.getId()),
                formation.getNom(),
                formation.getDescription(),
                formation.getDateDebut().toString(),
                formation.getDateFin().toString(),
                String.valueOf(formation.getCapacite())
            );
            lines.add(line);
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }
}

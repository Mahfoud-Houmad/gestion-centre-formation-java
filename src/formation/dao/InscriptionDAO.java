package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import formation.model.Inscription;

public class InscriptionDAO {
    
    private static final String FILE_PATH = "data/inscriptions.txt";
    private static final String DELIMITER = "\\|";
    private static final String NOTES_DELIMITER = ",";
    private static final String NOTE_SEPARATOR = ":";

    public List<Inscription> readAll() throws IOException {
        List<Inscription> inscriptions = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return inscriptions;

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int etudiantId = Integer.parseInt(parts[0]);
            int formationId = Integer.parseInt(parts[1]);
            Inscription inscription = new Inscription(etudiantId, formationId);

            if (parts.length > 2) {
                String[] notes = parts[2].split(NOTES_DELIMITER);
                for (String noteStr : notes) {
                    String[] noteParts = noteStr.split(NOTE_SEPARATOR);
                    int moduleId = Integer.parseInt(noteParts[0]);
                    double note = Double.parseDouble(noteParts[1]);
                    inscription.ajouterNote(moduleId, note);
                }
            }
            inscriptions.add(inscription);
        }
        return inscriptions;
    }

    public void saveAll(List<Inscription> inscriptions) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Inscription inscription : inscriptions) {
            StringBuilder sb = new StringBuilder();
            sb.append(inscription.getEtudiantId()).append("|")
              .append(inscription.getFormationId());

            if (!inscription.getNotesParModule().isEmpty()) {
                sb.append("|");
                List<String> notesStr = new ArrayList<>();
                for (Map.Entry<Integer, Double> entry : inscription.getNotesParModule().entrySet()) {
                    notesStr.add(entry.getKey() + NOTE_SEPARATOR + entry.getValue());
                }
                sb.append(String.join(NOTES_DELIMITER, notesStr));
            }
            lines.add(sb.toString());
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }
}

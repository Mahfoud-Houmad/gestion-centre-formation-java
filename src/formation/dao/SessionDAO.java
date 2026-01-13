package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import formation.model.Session;

public class SessionDAO {
    
    private static final String FILE_PATH = "data/sessions.txt";
    private static final String DELIMITER = "\\|";

    public List<Session> readAll() throws IOException {
        List<Session> sessions = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return sessions;

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int id = Integer.parseInt(parts[0]);
            LocalDateTime heureDebut = LocalDateTime.parse(parts[1]);
            LocalDateTime heureFin = LocalDateTime.parse(parts[2]);
            String jourSemaine = parts[3].toUpperCase();
            int salleId = Integer.parseInt(parts[4]);
            int formateurId = Integer.parseInt(parts[5]);
            int formationId = Integer.parseInt(parts[6]);

            sessions.add(new Session(id, heureDebut, heureFin, jourSemaine, salleId, formateurId, formationId));
        }
        return sessions;
    }

    public void saveAll(List<Session> sessions) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Session session : sessions) {
            String line = String.join("|",
                String.valueOf(session.getId()),
                session.getHeureDebut().toString(),
                session.getHeureFin().toString(),
                session.getJourSemaine(),
                String.valueOf(session.getSalleId()),
                String.valueOf(session.getFormateurId()),
                String.valueOf(session.getFormationId())
            );
            lines.add(line);
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }

    public void supprimerSessionsParFormateur(int formateurId) throws IOException {
    List<Session> sessions = readAll();
    sessions.removeIf(s -> s.getFormateurId() == formateurId);
    saveAll(sessions);
    }

    public void supprimerSessionsParSalle(int salleId) throws IOException {
        List<Session> sessions = readAll();
        sessions.removeIf(s -> s.getSalleId() == salleId);
        saveAll(sessions);
    }
}

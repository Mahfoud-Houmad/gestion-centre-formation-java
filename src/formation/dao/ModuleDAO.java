package formation.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import formation.model.Module;
public class ModuleDAO {
    private static final String FILE_PATH = "data/modules.txt";
    private static final String DELIMITER = "\\|";

    public List<Module> readAll() throws IOException {
        List<Module> modules = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE_PATH))) return modules;

        for (String line : Files.readAllLines(Paths.get(FILE_PATH))) {
            String[] parts = line.split(DELIMITER);
            modules.add(new Module(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                Integer.parseInt(parts[3])
            ));
        }
        return modules;
    }

    public void saveAll(List<Module> modules) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Module m : modules) {
            lines.add(String.join("|",
                String.valueOf(m.getId()),
                m.getNom(),
                m.getDescription(),
                String.valueOf(m.getFormationId())
            ));
        }
        Files.write(Paths.get(FILE_PATH), lines);
    }

    // Récupérer les modules d'une formation spécifique
    public List<Module> getModulesByFormationId(int formationId) throws IOException {
        return readAll().stream()
            .filter(m -> m.getFormationId() == formationId)
            .toList();
    }
}

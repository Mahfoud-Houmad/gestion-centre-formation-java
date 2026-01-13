package formation.service;


import formation.model.Module;
import java.io.IOException;
import java.util.List;
import formation.dao.ModuleDAO;
public class ModuleService {
    private final ModuleDAO moduleDAO = new ModuleDAO();

    public void creerModule(String nom, String description, int formationId) throws IOException {
        List<Module> modules = moduleDAO.readAll();
        int newId = modules.isEmpty() ? 1 : modules.get(modules.size() - 1).getId() + 1;
        modules.add(new Module(newId, nom, description, formationId));
        moduleDAO.saveAll(modules);
    }

    public List<Module> listerModulesParFormation(int formationId) throws IOException {
        return moduleDAO.getModulesByFormationId(formationId);
    }
}

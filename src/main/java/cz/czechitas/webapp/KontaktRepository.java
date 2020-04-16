package cz.czechitas.webapp;

import java.util.List;

public interface KontaktRepository {
    public List<Kontakt> findAll();

    public Kontakt findById(Long id);

    public Kontakt save(Kontakt zaznamKUlozeni);
    
    public void delete(Long id);

}

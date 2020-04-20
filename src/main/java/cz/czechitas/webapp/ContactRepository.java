package cz.czechitas.webapp;

import java.util.List;

public interface ContactRepository {
    public List<Contact> findAll();

    public Contact findById(Long id);

    public Contact save(Contact contactToSave);
    
    public void delete(Long id);

}

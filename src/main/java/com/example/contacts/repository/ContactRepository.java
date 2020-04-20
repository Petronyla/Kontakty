package com.example.contacts.repository;

import java.util.List;
import com.example.contacts.entity.Contact;

public interface ContactRepository {

    public List<Contact> findAll();

    public Contact findById(Long id);

    public Contact save(Contact contactToSave);
    
    public void deleteById(Long id);

}

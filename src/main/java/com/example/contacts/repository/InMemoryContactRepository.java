package com.example.contacts.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.example.contacts.entity.Contact;

public class InMemoryContactRepository implements ContactRepository {

    private Long idSequence = 104L;

    private static List<Contact> listOfContacts = new CopyOnWriteArrayList<>(Arrays.asList(
            new Contact(100L, "Hulk", "555 555 648", "hulk@avengers.com"),
            new Contact(101L, "Kapitán Amerika", "547 986 324", "captainamerica@avengers.com"),
            new Contact(102L, "Ironman", "598 635 226", "ironman@avengers.com"),
            new Contact(103L, "Spiderman", "514 255 363", "spiderman@avengers.com")
    ));

    @Override
    public synchronized List<Contact> findAll() {
        List<Contact> contacts = new ArrayList<>(listOfContacts.size());
        for (Contact aListOfContacts : listOfContacts) {
            contacts.add(clone(aListOfContacts));
        }
        return contacts;
    }

    @Override
    public synchronized Contact findById(Long id) {
        int index = findIndexOfRecord(id);
        if (index == -1) {
            return null;
        }
        return clone(listOfContacts.get(index));
    }

    @Override
    public synchronized Contact save(Contact contactToSave) {
        int index = findIndexOfRecord(contactToSave.getId());
        if (index == -1) {
            return add(contactToSave);

        }
        return update(contactToSave, index);
    }

    @Override
    public synchronized void deleteById(Long id) {
        int index = findIndexOfRecord(id);
        if (index == -1) return;
        listOfContacts.remove(index);
    }

    //-------------------------------------------------------------------------


    private Contact update(Contact contactToSave, int index) {
        Contact contact = clone(contactToSave);
        listOfContacts.set(index, contact);
        return clone(contact);
    }

    private Contact add(Contact contactToAdd) {
        Contact contact = clone(contactToAdd);
        contact.setId(idSequence);
        idSequence = idSequence + 1L;
        listOfContacts.add(contact);
        return clone(contact);
    }

    private int findIndexOfRecord(Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < listOfContacts.size(); i++) {
            Contact contact = listOfContacts.get(i);
            if (contact.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Contact clone(Contact originalContact) {
        return new Contact(originalContact.getId(), originalContact.getName(), originalContact.getPhoneNumber(),
                originalContact.getEmail());
    }


}

package cz.czechitas.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryContactRepository implements ContactRepository {

    private Long idSequence = 104L;

    /**
     * Method to fill the contact list with data
     */
    public static List<Contact> listOfConatcts = new CopyOnWriteArrayList<>(Arrays.asList(
            new Contact(100L, "Hulk", "555 555 648", "hulk@avengers.com"),
            new Contact(101L, "Kapitán Amerika", "547 986 324", "captainamerica@avengers.com"),
            new Contact(102L, "Ironman", "598 635 226", "ironman@avengers.com"),
            new Contact(103L, "Spiderman", "514 255 363", "spiderman@avengers.com")
    ));

    /**
     * Method to find all contacts from the list
     * @return contacts
     */
    @Override
    public synchronized List<Contact> findAll() {
        List<Contact> contacts = new ArrayList<>(listOfConatcts.size());
        for (Contact aListOfContacts : listOfConatcts) {
            contacts.add(clone(aListOfContacts));
        }
        return contacts;
    }

    /**
     * Method to find contact by ID
     * @param id
     * @return clone()
     */
    @Override
    public synchronized Contact findById(Long id) {
        int index = findIndexOfRecord(id);
        if (index == -1) {
            return null;
        }
        return clone(listOfConatcts.get(index));
    }

    /**
     * Method to update record or save new record
     * @param contactToSave
     * @return update()
     */
    @Override
    public synchronized Contact save(Contact contactToSave) {
        int index = findIndexOfRecord(contactToSave.getId());
        if (index == -1) {
            return add(contactToSave);

        }
        return update(contactToSave, index);
    }

    /**
     * Method to delete record
     * @param id
     */
    @Override
    public synchronized void delete(Long id) {
        int index = findIndexOfRecord(id);
        if (index == -1) return;
        listOfConatcts.remove(index);
    }

    //-------------------------------------------------------------------------


    private Contact update(Contact contactToSave, int index) {
        Contact contact = clone(contactToSave);
        listOfConatcts.set(index, contact);
        return clone(contact);
    }

    private Contact add(Contact contactToAdd) {
        Contact contact = clone(contactToAdd);
        contact.setId(idSequence);
        idSequence = idSequence + 1L;
        listOfConatcts.add(contact);
        return clone(contact);
    }

    private int findIndexOfRecord(Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < listOfConatcts.size(); i++) {
            Contact contact = listOfConatcts.get(i);
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

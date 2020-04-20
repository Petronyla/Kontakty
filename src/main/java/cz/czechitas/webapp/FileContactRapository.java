package cz.czechitas.webapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContactRapository implements ContactRepository {
    public static final Pattern REGEX_RADKU = Pattern.compile("([0-9]+)[,;]\"(.*?)\"[,;]\"(.*?)\"[,;]\"(.*?)\"");

    private Long idSequence = 3000L;

    /**
     * Method to find all contacts from the list
     * @return contacts
     */
    @Override
    public synchronized List<Contact> findAll() {
        try {
            List<String> rows = Files.readAllLines(Paths.get("data.csv"));

            List<Contact> contacts = new ArrayList<>(rows.size());
            for (String row : rows) {
                Matcher regularAutomat = REGEX_RADKU.matcher(row);
                if (!regularAutomat.find()) continue;

                Contact contact = new Contact();
                contact.setId(Long.parseLong(regularAutomat.group(1)));
                contact.setName(regularAutomat.group(2));
                contact.setPhoneNumber(regularAutomat.group(3));
                contact.setEmail(regularAutomat.group(4));
                contacts.add(contact);
            }
            return contacts;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to find contact by ID
     * @param id
     * @return clone()
     */
    @Override
    public synchronized Contact findById(Long id) {
        List<Contact> contacts = findAll();
        int index = findIndexOfRecord(contacts, id);
        if (index == -1) {
            return null;
        }
        return clone(contacts.get(index));
    }

    /**
     * Method to update record or save new record
     * @param recordToSave
     * @return update()
     */
    @Override
    public synchronized Contact save(Contact recordToSave) {
        List<Contact> contacts = findAll();
        int index = findIndexOfRecord(contacts, recordToSave.getId());
        if (index == -1) {
            return add(contacts, recordToSave);

        }
        return update(contacts, recordToSave, index);
    }

    /**
     * Method to delete record
     * @param id
     */
    @Override
    public synchronized void delete(Long id) {
        List<Contact> contacts = findAll();
        int index = findIndexOfRecord(contacts, id);
        if (index == -1) return;
        contacts.remove(index);
        saveContact(contacts);
    }

    //-------------------------------------------------------------------------

    private Contact update(List<Contact> contacts, Contact recordForSave, int index) {
        Contact contact = clone(recordForSave);
        contacts.set(index, contact);
        saveContact(contacts);
        return clone(contact);
    }

    private Contact add(List<Contact> contacts, Contact recordForAdd) {
        Contact contact = clone(recordForAdd);
        contact.setId(idSequence);
        idSequence = idSequence + 1L;
        contacts.add(contact);
        saveContact(contacts);
        return clone(contact);
    }

    private int findIndexOfRecord(List<Contact> contacts, Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Contact clone(Contact originalContact) {
        return new Contact(originalContact.getId(), originalContact.getName(), originalContact.getPhoneNumber(), originalContact.getEmail());
    }

    private void saveContact(List<Contact> contacts) {
        try {
            List<String> rows = new ArrayList<>(contacts.size());
            for (Contact oneContact : contacts) {
                String row =
                        oneContact.getId() + ",\"" + oneContact.getName() + "\",\"" + oneContact.getPhoneNumber() + "\",\"" + oneContact.getEmail() + "\"";
                rows.add(row);
            }
            Files.write(Paths.get("data.csv"), rows, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

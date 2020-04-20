package com.example.contacts.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.mariadb.jdbc.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.lookup.*;
import org.springframework.jdbc.support.*;
import com.example.contacts.entity.Contact;

public class JdbcContactRepository implements ContactRepository {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<Contact> rowMapper;

    public JdbcContactRepository(JdbcTemplate jdbcTemplate, RowMapper<Contact> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public synchronized List<Contact> findAll() {
        List<Contact> listOfContacts = jdbcTemplate.query(
                "select ID, Jmeno, TelefonniCislo, Email" +
                        " from Kontakt",
                rowMapper);
        return listOfContacts;
    }

    @Override
    public synchronized Contact findById(Long id) {
        Contact contact = jdbcTemplate.queryForObject(
                "select ID, Jmeno, TelefonniCislo, Email" +
                        " from Kontakt where ID=?",
                rowMapper,
                id);
        return contact;
    }

    @Override
    public synchronized Contact save(Contact recordForSave) {
        if (recordForSave.getId() == null) {
            return add(recordForSave);
        }
        return update(recordForSave);
    }

    @Override
    public synchronized void deleteById(Long id) {
        jdbcTemplate.update(
                "DELETE FROM Kontakt WHERE id = ?",
                id);
    }

    //-------------------------------------------------------------------------

    private Contact update(Contact recordForSave) {
        recordForSave = clone(recordForSave);
        jdbcTemplate.update(
                "UPDATE Kontakt SET Jmeno = ?, TelefonniCislo = ?, Email = ? WHERE id = ?",
                recordForSave.getName(),
                recordForSave.getPhoneNumber(),
                recordForSave.getEmail(),
                recordForSave.getId());
        return recordForSave;
    }

    private Contact add(Contact recordForAdd) {
        Contact oneContact = clone(recordForAdd);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO Kontakt (Jmeno, TelefonniCislo, Email) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update((Connection con) -> {
                    PreparedStatement command = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    command.setString(1, oneContact.getName());
                    command.setString(2, oneContact.getPhoneNumber());
                    command.setString(3, oneContact.getEmail());
                    return command;
                },
                keyHolder);
        oneContact.setId(keyHolder.getKey().longValue());
        return oneContact;

    }

    private Contact clone(Contact original) {
        return new Contact(original.getId(), original.getName(), original.getPhoneNumber(), original.getEmail());
    }

}

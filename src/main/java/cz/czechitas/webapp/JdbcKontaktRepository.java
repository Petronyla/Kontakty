package cz.czechitas.webapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.mariadb.jdbc.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.lookup.*;
import org.springframework.jdbc.support.*;

public class JdbcKontaktRepository implements KontaktRepository {

    private JdbcTemplate odesilacDotazu;
    private RowMapper<Kontakt> prevodnik;

    public JdbcKontaktRepository() {
        try {
            MariaDbDataSource konfiguraceDatabaze = new MariaDbDataSource();
            konfiguraceDatabaze.setUserName("student");
            konfiguraceDatabaze.setPassword("password");
            konfiguraceDatabaze.setUrl("jdbc:mysql://localhost:3306/SeznamKontaktu");

            odesilacDotazu = new JdbcTemplate(konfiguraceDatabaze);
            prevodnik = BeanPropertyRowMapper.newInstance(Kontakt.class);
        } catch (SQLException e) {
            throw new DataSourceLookupFailureException("Nepodarilo se vytvorit DataSource", e);
        }
    }

    public synchronized List<Kontakt> findAll() {
        List<Kontakt> seznamKontaktu = odesilacDotazu.query(
                "select ID, Jmeno, TelefonniCislo, Email" +
                        " from Kontakt",
                prevodnik);
        return seznamKontaktu;
    }

    public synchronized Kontakt findById(Long id) {
        Kontakt kontakt = odesilacDotazu.queryForObject(
                "select ID, Jmeno, TelefonniCislo, Email" +
                        " from Kontakt where ID=?",
                prevodnik,
                id);
        return kontakt;
    }

    public synchronized Kontakt save(Kontakt zaznamKUlozeni) {
        if (zaznamKUlozeni.getId() == null) {
            return pridej(zaznamKUlozeni);
        }
        return updatuj(zaznamKUlozeni);
    }

    public synchronized void delete(Long id) {
        odesilacDotazu.update(
                "DELETE FROM Kontakt WHERE id = ?",
                id);
    }

    //-------------------------------------------------------------------------

    private Kontakt updatuj(Kontakt zaznamKUlozeni) {
        zaznamKUlozeni = clone(zaznamKUlozeni);
        odesilacDotazu.update(
                "UPDATE Kontakt SET Jmeno = ?, TelefonniCislo = ?, Email = ? WHERE id = ?",
                zaznamKUlozeni.getJmeno(),
                zaznamKUlozeni.getTelefonniCislo(),
                zaznamKUlozeni.getEmail(),
                zaznamKUlozeni.getId());
        return zaznamKUlozeni;
    }

    private Kontakt pridej(Kontakt zaznamKPridani) {
        Kontakt jedenKontakt = clone(zaznamKPridani);
        GeneratedKeyHolder drzakNaVygenerovanyKlic = new GeneratedKeyHolder();
        String sql = "INSERT INTO Kontakt (Jmeno, TelefonniCislo, Email) " +
                "VALUES (?, ?, ?)";
        odesilacDotazu.update((Connection con) -> {
                    PreparedStatement prikaz = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    prikaz.setString(1, jedenKontakt.getJmeno());
                    prikaz.setString(2, jedenKontakt.getTelefonniCislo());
                    prikaz.setString(3, jedenKontakt.getEmail());
                    return prikaz;
                },
                drzakNaVygenerovanyKlic);
        jedenKontakt.setId(drzakNaVygenerovanyKlic.getKey().longValue());
        return jedenKontakt;

    }

    private Kontakt clone(Kontakt puvodni) {
        return new Kontakt(puvodni.getId(), puvodni.getJmeno(), puvodni.getTelefonniCislo(), puvodni.getEmail());
    }

}

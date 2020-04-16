package cz.czechitas.webapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SouborovaKontaktRapository implements KontaktRepository {
    public static final Pattern REGEX_RADKU = Pattern.compile("([0-9]+)[,;]\"(.*?)\"[,;]\"(.*?)\"[,;]\"(.*?)\"");

    private Long idSequence = 3000L;

    /**
     * Metoda na vyhledání všech kontaktů ze seznamu
     * @return kontakty
     */
    public synchronized List<Kontakt> findAll() {
        try {
            List<String> radky = Files.readAllLines(Paths.get("data.csv"));

            List<Kontakt> kontakty = new ArrayList<>(radky.size());
            for (String radek : radky) {
                Matcher regularniAutomat = REGEX_RADKU.matcher(radek);
                if (!regularniAutomat.find()) continue;

                Kontakt jedenKontakt = new Kontakt();
                jedenKontakt.setId(Long.parseLong(regularniAutomat.group(1)));
                jedenKontakt.setJmeno(regularniAutomat.group(2));
                jedenKontakt.setTelefonniCislo(regularniAutomat.group(3));
                jedenKontakt.setEmail(regularniAutomat.group(4));
                kontakty.add(jedenKontakt);
            }
            return kontakty;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metoda na vyhledání kontaktu podle ID
     * @param id
     * @return clone()
     */
    public synchronized Kontakt findById(Long id) {
        List<Kontakt> kontakty = findAll();
        int index = najdiIndexZaznamu(kontakty, id);
        if (index == -1) {
            return null;
        }
        return clone(kontakty.get(index));
    }

    /**
     * Metoda k uložení upraveného nebo nového záznamu
     * @param zaznamKUlozeni
     * @return updatuj()
     */
    public synchronized Kontakt save(Kontakt zaznamKUlozeni) {
        List<Kontakt> kontakty = findAll();
        int index = najdiIndexZaznamu(kontakty, zaznamKUlozeni.getId());
        if (index == -1) {
            return pridej(kontakty, zaznamKUlozeni);
        }
        return updatuj(kontakty, zaznamKUlozeni, index);
    }

    /**
     * Metoda na odstranění záznamu
     * @param id
     */
    public synchronized void delete(Long id) {
        List<Kontakt> kontakty = findAll();
        int index = najdiIndexZaznamu(kontakty, id);
        if (index == -1) return;
        kontakty.remove(index);
        ulozKontakty(kontakty);
    }

    //-------------------------------------------------------------------------

    private Kontakt updatuj(List<Kontakt> kontakty, Kontakt zaznamKUlozeni, int index) {
        Kontakt kontakt = clone(zaznamKUlozeni);
        kontakty.set(index, kontakt);
        ulozKontakty(kontakty);
        return clone(kontakt);
    }

    private Kontakt pridej(List<Kontakt> kontakty, Kontakt zaznamKPridani) {
        Kontakt kontakt = clone(zaznamKPridani);
        kontakt.setId(idSequence);
        idSequence = idSequence + 1L;
        kontakty.add(kontakt);
        ulozKontakty(kontakty);
        return clone(kontakt);
    }

    private int najdiIndexZaznamu(List<Kontakt> kontakty, Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < kontakty.size(); i++) {
            Kontakt kontakt = kontakty.get(i);
            if (kontakt.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Kontakt clone(Kontakt puvodni) {
        return new Kontakt(puvodni.getId(), puvodni.getJmeno(), puvodni.getTelefonniCislo(), puvodni.getEmail());
    }

    private void ulozKontakty(List<Kontakt> kontakty) {
        try {
            List<String> radky = new ArrayList<>(kontakty.size());
            for (Kontakt jedenKontakt : kontakty) {
                String radek = jedenKontakt.getId() + ",\"" + jedenKontakt.getJmeno() + "\",\"" + jedenKontakt.getTelefonniCislo() + "\",\"" + jedenKontakt.getEmail() + "\"";
                radky.add(radek);
            }
            Files.write(Paths.get("data.csv"), radky, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

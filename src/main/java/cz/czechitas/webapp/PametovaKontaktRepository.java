package cz.czechitas.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PametovaKontaktRepository implements KontaktRepository {

    private Long idSequence = 104L;

    /**
     * Metoda na naplnění seznamu kontaktů daty
     */
    public static List<Kontakt> seznamKontaktu = new ArrayList<>(Arrays.asList(
            new Kontakt(100L, "Hulk", "555 555 648", "hulk@avengers.com"),
            new Kontakt(101L, "Kapitán Amerika", "547 986 324", "captainamerica@avengers.com"),
            new Kontakt(102L, "Ironman", "598 635 226", "ironman@avengers.com"),
            new Kontakt(103L, "Spiderman", "514 255 363", "spiderman@avengers.com")
    ));

    /**
     * Metoda na vyhledání všech kontaktů ze seznamu
     * @return kontakty
     */
    public synchronized List<Kontakt> findAll() {
        List<Kontakt> kontakty = new ArrayList<>(seznamKontaktu.size());
        for (Kontakt aSeznamKontaktu : seznamKontaktu) {
            kontakty.add(clone(aSeznamKontaktu));
        }
        return kontakty;
    }

    /**
     * Metoda na vyhledání kontaktu podle ID
     * @param id
     * @return clone()
     */
    public synchronized Kontakt findById(Long id) {
        int index = najdiIndexZaznamu(id);
        if (index == -1) {
            return null;
        }
        return clone(seznamKontaktu.get(index));
    }

    /**
     * Metoda k uložení upraveného nebo nového záznamu
     * @param zaznamKUlozeni
     * @return updatuj()
     */
    public synchronized Kontakt save(Kontakt zaznamKUlozeni) {
        int index = najdiIndexZaznamu(zaznamKUlozeni.getId());
        if (index == -1) {
            return pridej(zaznamKUlozeni);
        }
        return updatuj(zaznamKUlozeni, index);
    }

    /**
     * Metoda na odstranění záznamu
     * @param id
     */
    public synchronized void delete(Long id) {
        int index = najdiIndexZaznamu(id);
        if (index == -1) return;
        seznamKontaktu.remove(index);
    }

    //-------------------------------------------------------------------------


    private Kontakt updatuj(Kontakt zaznamKUlozeni, int index) {
        Kontakt kontakt = clone(zaznamKUlozeni);
        seznamKontaktu.set(index, kontakt);
        return clone(kontakt);
    }

    private Kontakt pridej(Kontakt zaznamKPridani) {
        Kontakt kontakt = clone(zaznamKPridani);
        kontakt.setId(idSequence);
        idSequence = idSequence + 1L;
        seznamKontaktu.add(kontakt);
        return clone(kontakt);
    }

    private int najdiIndexZaznamu(Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < seznamKontaktu.size(); i++) {
            Kontakt kontakt = seznamKontaktu.get(i);
            if (kontakt.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private Kontakt clone(Kontakt puvodni) {
        return new Kontakt(puvodni.getId(), puvodni.getJmeno(), puvodni.getTelefonniCislo(), puvodni.getEmail());
    }


}

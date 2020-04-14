package cz.czechitas.webapp;

import java.util.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class HlavniController {

    List<Kontakt> seznamKontaktu;

    public HlavniController() {
        seznamKontaktu = new ArrayList<>();
        seznamKontaktu.add(new Kontakt(100L, "Hulk", "555 555 648", "hulk@avengers.com"));
        seznamKontaktu.add(new Kontakt(101L, "Kapitán Amerika", "547 986 324", "captainamerica@avengers.com"));
        seznamKontaktu.add(new Kontakt(102L, "Ironman", "598 635 226", "ironman@avengers.com"));
        seznamKontaktu.add(new Kontakt(103L, "Spiderman", "514 255 363", "spiderman@avengers.com"));
    }

    /**
     * Metoda, která přesměruje z /index.html na /seznam.html
     * @return data
     */
    @RequestMapping("/")
    public ModelAndView zobrazIndex() {
        ModelAndView data = new ModelAndView("redirect:/seznam.html");
        return data;
    }

    /**
     * Metoda, která zobrazí seznam.html
     * @return data
     */
    @RequestMapping("/seznam.html")
    public ModelAndView zobrazSeznam() {
        ModelAndView data = new ModelAndView("seznam");
        data.addObject("seznamKontaktu", seznamKontaktu);
        return data;
    }

    /**
     * Metoda, která zobrazí detail vybraného kontaktu
     * @param idKontaktu
     * @return data
     */
    @RequestMapping(value = "/{idKontaktu:[0-9]+}.html", method = RequestMethod.GET)
    public ModelAndView zobrazDetail(@PathVariable Long idKontaktu) {
        ModelAndView data = new ModelAndView("detail");
        Kontakt jedenKontakt = findById(idKontaktu);
        data.addObject("jedenKontakt", jedenKontakt);
        return data;
    }

    /**
     * Metoda, která zpracuje a uloží změny ve formuláři s vybraným kontaktem
     * @param idKontaktu
     * @param vstup
     * @return data
     */
    @RequestMapping(value = "/{idKontaktu:[0-9]+}.html", method = RequestMethod.POST)
    public ModelAndView zpracujDetail(@PathVariable Long idKontaktu, DetailForm vstup) {
        Kontakt jedenKontakt = findById(idKontaktu);
        jedenKontakt.setEmail(vstup.getEmail());
        jedenKontakt.setJmeno(vstup.getJmeno());
        jedenKontakt.setTelefonniCislo(vstup.getTelefonniCislo());
        return new ModelAndView("redirect:/seznam.html");
    }

    /**
     * Metoda, která zajistí smazání záznamu z tabulky kontaktů
     * @param idKontaktu
     * @return data
     */
    @RequestMapping(value = "/{cislo:[0-9]+}/delete")
    public ModelAndView zpracujSeznam(@PathVariable("cislo") Long idKontaktu) {
        Kontakt jedenKontakt = findById(idKontaktu);
        seznamKontaktu.remove(jedenKontakt);
        return  new ModelAndView("redirect:/seznam.html");
    }

    /**
     * Metoda na nalezení kontaktu podle id
     * @param id
     * @return kontakt
     */
    public Kontakt findById(Long id) {
        for (Kontakt kontakt : seznamKontaktu) {
            if (kontakt.getId().equals(id)) {
                return kontakt;
            }
        }
        return null;
    }

}

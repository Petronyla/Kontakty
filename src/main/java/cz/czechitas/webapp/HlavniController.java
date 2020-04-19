package cz.czechitas.webapp;

import java.util.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class HlavniController {

    private KontaktRepository kontaktRepository = new SouborovaKontaktRapository();

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
        List<Kontakt> seznamKontaktu = kontaktRepository.findAll();

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
        Kontakt nalezenyKontakt = kontaktRepository.findById(idKontaktu);
        data.addObject("jedenKontakt", nalezenyKontakt);
        return data;
    }

    /**
     * Metoda, která zpracuje a uloží změny ve formuláři s vybraným kontaktem
     * @param idKontaktu
     * @param vstup
     * @return ModelAndView
     */
    @RequestMapping(value = "/{idKontaktu:[0-9]+}.html", method = RequestMethod.POST)
    public ModelAndView zpracujDetail(@PathVariable Long idKontaktu, DetailForm vstup) {
        Kontakt nalezenyKontakt = kontaktRepository.findById(idKontaktu);
        nalezenyKontakt.setEmail(vstup.getEmail());
        nalezenyKontakt.setJmeno(vstup.getJmeno());
        nalezenyKontakt.setTelefonniCislo(vstup.getTelefonniCislo());
        kontaktRepository.save(nalezenyKontakt);
        return new ModelAndView("redirect:/seznam.html");
    }

    /**
     * Metoda, která zajistí smazání záznamu z tabulky kontaktů
     * @param idKontaktu
     * @return ModelAndView
     */
    @RequestMapping(value = "/{cislo:[0-9]+}/delete")
    public ModelAndView zpracujSeznam(@PathVariable("cislo") Long idKontaktu) {
        kontaktRepository.delete(idKontaktu);
        return  new ModelAndView("redirect:/seznam.html");
    }

    /**
     * Metoda na založení nového kontaktu a přípravu prázdného formuláře
     * @return ModelAndView
     */
    @RequestMapping(value = "/new.html", method = RequestMethod.GET)
    public ModelAndView zobrazNovy() {
        Kontakt novyKontakt = new Kontakt();

        ModelAndView data = new ModelAndView("detail");
        data.addObject("jedenKontakt", novyKontakt);
        return data;
    }

    /**
     * Metoda na zpracování nového kontaktu a uložení proměnných
     * @param vstup
     * @return ModelAndView
     */
    @RequestMapping(value = "/new.html", method = RequestMethod.POST)
    public ModelAndView zpracujNovy(DetailForm vstup) {
        Kontakt novyKontakt = new Kontakt(vstup.getJmeno(), vstup.getTelefonniCislo(), vstup.getEmail());
        kontaktRepository.save(novyKontakt);

        return new ModelAndView("redirect:/seznam.html");
    }


    /**
     * Metoda na nalezení kontaktu podle id
     * @param id
     * @return kontakt
     */

    public Kontakt findById(Long id) {
        for (Kontakt kontakt : kontaktRepository.findAll()) {
            if (kontakt.getId().equals(id)) {
                return kontakt;
            }
        }
        return null;
    }
}

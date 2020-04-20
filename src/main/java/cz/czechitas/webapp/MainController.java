package cz.czechitas.webapp;

import java.util.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@Controller
public class MainController {

    private ContactRepository contactRepository = new FileContactRapository();

    /**
     * Method to redirect from /index.html to /list.html
     * @return modelAndView
     */
    @RequestMapping("/")
    public ModelAndView displayIndex() {
        ModelAndView modelAndView = new ModelAndView("redirect:/list.html");
        return modelAndView;
    }

    /**
     * Method to display list.html
     * @return modelAndView
     */
    @RequestMapping("/list.html")
    public ModelAndView displayList() {
        List<Contact> listOfContacts = contactRepository.findAll();

        ModelAndView modelAndView = new ModelAndView("list");
        modelAndView.addObject("listOfContacts", listOfContacts);
        return modelAndView;
    }

    /**
     * Method to display detail of selected contact
     * @param idContact
     * @return modelAndView
     */
    @RequestMapping(value = "/{idContact:[0-9]+}.html", method = RequestMethod.GET)
    public ModelAndView displayDetail(@PathVariable Long idContact) {
        ModelAndView modelAndView = new ModelAndView("detail");
        Contact foundContact = contactRepository.findById(idContact);
        modelAndView.addObject("contact", foundContact);
        return modelAndView;
    }

    /**
     * Method to process and save changes in selected contact
     * @param idContact
     * @param input
     * @return ModelAndView
     */
    @RequestMapping(value = "/{idContact:[0-9]+}.html", method = RequestMethod.POST)
    public ModelAndView processDetail(@PathVariable Long idContact, DetailForm input) {
        Contact foundContact = contactRepository.findById(idContact);
        foundContact.setEmail(input.getEmail());
        foundContact.setName(input.getName());
        foundContact.setPhoneNumber(input.getPhoneNumber());
        contactRepository.save(foundContact);
        return new ModelAndView("redirect:/list.html");
    }

    /**
     * Method to delete record
     * @param idContact
     * @return ModelAndView
     */
    @RequestMapping(value = "/{number:[0-9]+}/delete")
    public ModelAndView processList(@PathVariable("number") Long idContact) {
        contactRepository.delete(idContact);
        return new ModelAndView("redirect:/list.html");
    }

    /**
     * Method to prepare blank form add establish new contact
     * @return ModelAndView
     */
    @RequestMapping(value = "/new.html", method = RequestMethod.GET)
    public ModelAndView displayNew() {
        Contact newContact = new Contact();
        ModelAndView modelAndView = new ModelAndView("detail");
        modelAndView.addObject("contact", newContact);
        return modelAndView;
    }

    /**
     * Method to process new contact and save fields
     * @param input
     * @return ModelAndView
     */
    @RequestMapping(value = "/new.html", method = RequestMethod.POST)
    public ModelAndView processNew(DetailForm input) {
        Contact newContact = new Contact(input.getName(), input.getPhoneNumber(), input.getEmail());
        contactRepository.save(newContact);

        return new ModelAndView("redirect:/list.html");
    }

    /**
     * Method to find contact by ID
     * @param id
     * @return contact
     */

    public Contact findById(Long id) {
        for (Contact contact : contactRepository.findAll()) {
            if (contact.getId().equals(id)) {
                return contact;
            }
        }
        return null;
    }
}

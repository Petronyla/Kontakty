package com.example.contacts.ui;

import java.util.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import com.example.contacts.entity.Contact;
import com.example.contacts.repository.ContactRepository;
import com.example.contacts.repository.FileContactRepository;

@Controller
public class MainController {

    private ContactRepository contactRepository;

    public MainController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * When a user accesses the root URL (/), redirect them to the welcome page.
     */
    @RequestMapping("/")
    public ModelAndView displayIndex() {
        ModelAndView modelAndView = new ModelAndView("redirect:/list.html");
        return modelAndView;
    }

    @RequestMapping("/list.html")
    public ModelAndView displayList() {
        List<Contact> listOfContacts = contactRepository.findAll();

        ModelAndView modelAndView = new ModelAndView("list");
        modelAndView.addObject("listOfContacts", listOfContacts);
        return modelAndView;
    }

    @RequestMapping(value = "/{idContact:[0-9]+}.html", method = RequestMethod.GET)
    public ModelAndView displayDetail(@PathVariable Long idContact) {
        ModelAndView modelAndView = new ModelAndView("detail");
        Contact foundContact = contactRepository.findById(idContact);
        modelAndView.addObject("contact", foundContact);
        return modelAndView;
    }

    @RequestMapping(value = "/{idContact:[0-9]+}.html", method = RequestMethod.POST)
    public ModelAndView processDetail(@PathVariable Long idContact, DetailForm input) {
        Contact foundContact = contactRepository.findById(idContact);
        foundContact.setEmail(input.getEmail());
        foundContact.setName(input.getName());
        foundContact.setPhoneNumber(input.getPhoneNumber());
        contactRepository.save(foundContact);
        return new ModelAndView("redirect:/list.html");
    }

    @RequestMapping(value = "/{idContact:[0-9]+}/delete.html")
    public ModelAndView processDelete(@PathVariable("idContact") Long idContact) {
        contactRepository.deleteById(idContact);
        return new ModelAndView("redirect:/list.html");
    }

    @RequestMapping(value = "/new.html", method = RequestMethod.GET)
    public ModelAndView displayNew() {
        Contact newContact = new Contact();
        ModelAndView modelAndView = new ModelAndView("detail");
        modelAndView.addObject("contact", newContact);
        return modelAndView;
    }

    @RequestMapping(value = "/new.html", method = RequestMethod.POST)
    public ModelAndView processNew(DetailForm input) {
        Contact newContact = new Contact(input.getName(), input.getPhoneNumber(), input.getEmail());
        contactRepository.save(newContact);
        return new ModelAndView("redirect:/list.html");
    }

    public Contact findById(Long id) {
        for (Contact contact : contactRepository.findAll()) {
            if (contact.getId().equals(id)) {
                return contact;
            }
        }
        return null;
    }
}

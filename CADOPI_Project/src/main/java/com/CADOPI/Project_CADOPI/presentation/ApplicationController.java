package com.CADOPI.Project_CADOPI.presentation;

import com.CADOPI.Project_CADOPI.Constant;
import com.CADOPI.Project_CADOPI.infrastructure.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ApplicationController {

    @Autowired
    private LdapClient ldapClient;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView home(Model model) {
        getInstancesAutorise(model, ldapClient);
        return new ModelAndView("chargtOPI");
    }

    public static Map<String, String> getInstancesAutorise(Model model, LdapClient ldapClient) {
        // recupère la liste des instances en fonction de recherche dans ldap
        // Donne un login dans le nom du fichier, ce login est récupéré après la connection
        LdapUserDetails ldapuser = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String login = ldapuser.getUsername();

        List<String> seeAlsoList = ldapClient.search(login);
        seeAlsoList.stream().forEach(s -> System.out.println("cn : " + s));
        Map<String, String> instancesAutorises = getInstancesAutorise(seeAlsoList);
        model.addAttribute("nomInstance", instancesAutorises);
        return instancesAutorises;
    }

    private static Map<String, String> getInstancesAutorise(List<String> seeAlsoList) {
        Map<String, String> instancesAuto = getInstanceAutorise(seeAlsoList)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Constant.INSTANCES.get(e.getKey())));
        return instancesAuto;
    }

    private static Stream<Map.Entry<String, String>> getInstanceAutorise(List<String> seeAlsoList) {
        return seeAlsoList.stream().flatMap(s ->
                Constant.INSTANCES_AUTO.entrySet().stream().filter(entry -> entry.getValue().equals(s))
        );
    }

    @RequestMapping(value = "/chargtOPI", method = RequestMethod.GET)
    public ModelAndView chargtOpi(Model model) {
        return home(model);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView login() {
        return new ModelAndView("login");
    }

//    @RequestMapping(value = "/historique", method = RequestMethod.GET)
//    public ModelAndView historique() {
//        return new ModelAndView("historique");
//    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public ModelAndView contact() {
        return new ModelAndView("contact");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ModelAndView("login");
    }
}

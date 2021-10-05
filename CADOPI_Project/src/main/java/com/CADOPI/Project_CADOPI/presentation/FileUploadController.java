package com.CADOPI.Project_CADOPI.presentation;

import com.CADOPI.Project_CADOPI.Constant;
import com.CADOPI.Project_CADOPI.infrastructure.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@RestController
public class FileUploadController {

    private static Logger logger = Logger.getLogger("FileUploadController.class");

    @Autowired
    private Environment env;

    @Autowired
    private LdapClient ldapClient;

    @RequestMapping(value = "/chargtOPI", method = RequestMethod.POST)
    public ModelAndView uploadFile(Model model, @RequestParam("uploadfile") MultipartFile[] files,
                                   @RequestParam("instance") String instance, HttpServletRequest request, HttpServletResponse response) {

        // Donne une date dans le nom du fichier
//		LocalDateTime date = LocalDateTime.now().withMinute(2);
//		String date_String = date.toString().replace(":", "-");

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        String date_String = now.format(formatter);

        // Donne un login dans le nom du fichier, ce login est récupéré après la
        // connection
        LdapUserDetails ldapuser = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String user = ldapuser.getUsername();

        // Recupère le répertoire racine de l'application
        String path = env.getRequiredProperty("upload.directory");

        // recupère les instances autorisés
        Map<String, String> instancesAutorisesMap = ApplicationController.getInstancesAutorise(model, ldapClient);
        Set<String> instancesAutorisesList = instancesAutorisesMap.keySet();
        // Mettre en session les instances autorisés
        model.addAttribute("instancesAutorises", instancesAutorisesList);

        boolean uploadSucces = false;

        String lastFileName = null;

        try {

            for (int i = files.length - 1; i >= 0; i--) {
                MultipartFile file = files[i];
                String destinationFileName = "";

                if (file.getOriginalFilename().isEmpty()) {
                    continue;
                }
                if (i == 1) {
                    destinationFileName = path + "/storages/voeux/" + instance + "_" + user + "_" + date_String + "_"
                            + file.getOriginalFilename();
                    String extension = file.getContentType();
                    if (extension.equals(Constant.EXTENSION_TEXT) || extension.equals(Constant.EXTENSION_OS)) {
                        if (isValidFileName(file.getOriginalFilename())) {
                            Files.copy(file.getInputStream(), Path.of(destinationFileName),
                                    StandardCopyOption.REPLACE_EXISTING);
                            uploadSucces = true;
                            // mettre les droits de lire pour tout le monde sur serveur
                            if (!env.getProperty("window").equals("true")) {
                                Runtime.getRuntime().exec("chmod 644 " + destinationFileName);
                            }
                            lastFileName = path + "/storages/voeux/traiter_" + instance + "_" + user + "_" + date_String + "_"
                                    + file.getOriginalFilename();
                        }
                        else {
                            model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_CHARACTER + " : " + file.getOriginalFilename());
                            return new ModelAndView("chargtOPI");
                        }
                    } else {
                        model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_FORMAT);
                        logger.warning("Chargement des fichiers avec l'erreur de format : " + destinationFileName);
                        return new ModelAndView("chargtOPI");
                    }
                }
                if (i == 0) {
                    destinationFileName = path + "/storages/individus/" + instance + "_" + user + "_" + date_String
                            + "_" + file.getOriginalFilename();
                    String extension = file.getContentType();
                    if (extension.equals(Constant.EXTENSION_TEXT) || extension.equals(Constant.EXTENSION_OS)) {
                        if (isValidFileName(file.getOriginalFilename())) {
                            Files.copy(file.getInputStream(), Path.of(destinationFileName),
                                    StandardCopyOption.REPLACE_EXISTING);
                            uploadSucces = true;
                            // mettre les droits de lire pour tout le monde sur serveur
                            if (!env.getProperty("window").equals("true")) {
                                Runtime.getRuntime().exec("chmod 644 " + destinationFileName);
                            }
                            if (lastFileName == null)
                                lastFileName = path + "/storages/individus/traiter_" + instance + "_" + user + "_" + date_String
                                        + "_" + file.getOriginalFilename();
                        }
                        else {
                            model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_CHARACTER + " : " + file.getOriginalFilename());
                            return new ModelAndView("chargtOPI");

                        }
                    } else {
                        model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_FORMAT);
                        logger.warning("Chargement des fichiers avec l'erreur de format : " + destinationFileName);
                        return new ModelAndView("chargtOPI");
                    }
                }

            }
            if (uploadSucces) {
                logger.info("Chargement des fichiers avec succès !");
                return uploadFileSucces(model, instance, request, response, lastFileName);
            }
        } catch (IOException e) {
            model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_GENERAL);
            logger.warning("Chargement des fichiers avec une erreur : " + e.getMessage());
            return new ModelAndView("chargtOPI");
        }
        catch (InterruptedException e) {
            model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_GENERAL);
            logger.warning("Copie des fichiers vers les serveurs Oracle avec une erreur : " + e.getMessage());
            return new ModelAndView("chargtOPI");
        }
        model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_GENERAL);
        logger.severe("Chargement des fichiers avec une erreur générale");
        return new ModelAndView("chargtOPI");
    }

    public static boolean isValidFileName(String filename) {
        String regExp = "(\\w*|\\.*|-*|_*)+\\.(DAT|dat|txt|TXT)";
        return filename.matches(regExp);
    }


    public ModelAndView uploadFileSucces(Model model, String instance, HttpServletRequest request,
                                         HttpServletResponse response, String lastFileName) throws InterruptedException {
        // On écoute si le fichier est traité (avec le nom traiter_ pour le dernier fichier envoyé : voeux s'il y a)
        // ici lastFileName est toujours != null
        int i = 0;
        while (i < Integer.parseInt(env.getRequiredProperty("waitTime")))  {
            Thread.sleep(200);
            // Vérifier s'il y a le fichier traiter_
            File f = new File(lastFileName);
            if (f.exists()) break;
            i++;
        }
        if (i < Integer.parseInt(env.getRequiredProperty("waitTime"))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formatDateTime = now.format(formatter);
            model.addAttribute("dateDuJour", formatDateTime);
            model.addAttribute("chgtOpiSucces", Constant.MESSAGE_SUCCES + instance);
            // On déconnecte
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return new ModelAndView("chargtOPISucces");
        }
        else {
            model.addAttribute("chgtOpiErreur", Constant.MESSAGE_ERREUR_GENERAL);
            logger.severe("Chargement des fichiers avec une erreur de copie de fichier vers les serveurs oracle");
            return new ModelAndView("chargtOPI");
        }
    }
}
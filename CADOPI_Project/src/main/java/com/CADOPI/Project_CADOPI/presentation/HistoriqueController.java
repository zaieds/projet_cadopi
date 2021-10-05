package com.CADOPI.Project_CADOPI.presentation;

import com.CADOPI.Project_CADOPI.infrastructure.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class HistoriqueController {
    @Autowired
    private Environment env;

    @Autowired
    private LdapClient ldapClient;

    private static final String dirIndividus = "//storages//individus//";
    private static final String dirVoeux = "//storages//voeux//";
    private static final String UNDERSCORE = "_";
    private static final String PREFIX = "traiter_";

    private static Logger logger = Logger.getLogger("DiskFileExplorer.class");

    @RequestMapping(value = "/historique", method = RequestMethod.GET)
    public ModelAndView historiqueOpi(Model model) {
        String path = env.getRequiredProperty("upload.directory");
        String dirLocation_indiv = path + dirIndividus;
        String dirLocation_voeu = path + dirVoeux;

        // récupère les instances autorisées
        Map<String, String> instancesAutorisesMap = ApplicationController.getInstancesAutorise(model, ldapClient);
        Set<String> instancesAutorisesList = instancesAutorisesMap.keySet();

        List<Opi> opiList_indiv = new ArrayList<>();
        try {
            List<File> files = Files.list(Paths.get(dirLocation_indiv))
                    .map(Path::toFile)
                    .filter(f -> f.getName().startsWith(PREFIX))
                    .collect(Collectors.toList());
            sortList(files);
            opiList_indiv = getOpiList(files, instancesAutorisesList);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        List<Opi> opiList_voeu = new ArrayList<>();
        try {
            List<File> files = Files.list(Paths.get(dirLocation_voeu))
                    .map(Path::toFile)
                    .filter(f -> f.getName().startsWith(PREFIX))
                    .collect(Collectors.toList());
            sortList(files);
            opiList_voeu = getOpiList(files, instancesAutorisesList);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
//        

        model.addAttribute("opiList_indiv", opiList_indiv);
        model.addAttribute("opiList_voeu", opiList_voeu);
        return new ModelAndView("historique");
    }

    public static void sortList(List<File> files) {
        Collections.sort(files, (File file1, File file2) -> {
            return Long.compare(file2.lastModified(), file1.lastModified());
        });
    }

    public List<Opi> getOpiList(List<File> files, Set<String> instancesAutorisesList) {
        List<Opi> opiList = new ArrayList<>();
        int j = 1;
        for (File f : files) {
            Opi opi = getOpi(f.getName(), j++, instancesAutorisesList);
            if (opi != null) {
                opiList.add(opi);
            }
        }
        //opiList.sort(Comparator.comparing(Opi::getDateTime).reversed());
//        Collections.sort(opiList, (Opi opi1, Opi opi2) -> {
//            return opi2.getDateTime().compareTo(opi1.getDateTime());
//        });
        return opiList;
    }

    //récupération des paramètre extraire de nom de fichier enregistré (nombre de fichier, la personne qui a déposé le fichier, nom de l'instance choisis, nom de fichier)
    public Opi getOpi(String fileName, int number, Set<String> instancesAutorisesList) {
        String[] fileNames = fileName.split(UNDERSCORE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        LocalDateTime dateTime = LocalDateTime.parse(fileNames[3], formatter);
        String instance = fileNames[1];

        String titre = fileNames[4];
        for (int i = 5; i < fileNames.length; i++) {
            titre = titre + UNDERSCORE + fileNames[i];
        }
        if (instancesAutorisesList.contains(instance)) {
            return new Opi(number, instance, fileNames[2], dateTime, titre);
        }
        return null;

    }


    public class Opi {
        private int number;
        private String title;
        private String author;
        private String instance;
        private LocalDateTime dateTime;

        public Opi(int number, String instance, String author, LocalDateTime dateTime, String title) {
            this.number = number;
            this.title = title;
            this.author = author;
            this.instance = instance;
            this.dateTime = dateTime;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }

}
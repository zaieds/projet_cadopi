package com.CADOPI.Project_CADOPI.presentation;

import com.CADOPI.Project_CADOPI.infrastructure.LdapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@SessionAttributes("compteRendus")
public class CompteRenduController {
    @Autowired
    private Environment env;

    @Autowired
    private LdapClient ldapClient;

    private static final String dirCompteRendu = "/storages/compterendu/";
    private static final String PREFIX = "CP_OPI";
    private static final String UNDERSCORE = "_";

    private static Logger logger = Logger.getLogger("CompteRenduController.class");

    @RequestMapping(value = "/compteRendu", method = RequestMethod.GET)
    public ModelAndView compteRenduFiles(Model model, @ModelAttribute("compteRendus") CompteRenduList compteRendus) {
        String path = env.getRequiredProperty("upload.directory");
        String dirLocation_cr = path + dirCompteRendu;

        // récupère les instances autorisées
        Map<String, String> instancesAutorisesMap = ApplicationController.getInstancesAutorise(model, ldapClient);
        Set<String> instancesAutorisesList = instancesAutorisesMap.keySet();
        HashSet instances = new HashSet(instancesAutorisesList);

        try {
            ArrayList<File> files = (ArrayList<File>) Files.list(Paths.get(dirLocation_cr))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            HistoriqueController.sortList(files);
            compteRendus(files, instances, compteRendus);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }

        model.addAttribute("compteRenduList_cr", compteRendus);

        return new ModelAndView("compteRendu");
    }

    @RequestMapping(value = "/compteRendu/{cr}", method = RequestMethod.GET)
    public ModelAndView compteRenduOpi(Model model, @PathVariable("cr") String crOpi, @ModelAttribute("compteRendus") CompteRenduList compteRendus) {
        try {
            String pathString = env.getRequiredProperty("upload.directory");
            String dirLocation_cr = pathString + dirCompteRendu;
            logger.info("dirLocation_cr : " + dirLocation_cr);
            if (!compteRendus.isEmpty()) {
                model.addAttribute("compteRenduList_cr", compteRendus);
            }
            Path path = Path.of(dirLocation_cr + crOpi);
            logger.info("path : " + dirLocation_cr + crOpi);
            Stream<String> lines = Files.lines(path);
            List<CompteRenduData> data = lines.map(
                    line -> {
                        logger.info("line : " + line);
                        return new CompteRenduData(line);
                    }
            ).collect(Collectors.toList());
            model.addAttribute("compteRendu_data", data);
            logger.info("fin : ");
            lines.close();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return new ModelAndView("compteRendu");
    }

    @ModelAttribute("compteRendus")
    public CompteRenduList compteRendus() {
        return new CompteRenduList();
    }

    public void compteRendus(ArrayList<File> files, HashSet<String> instancesAutorisesList, CompteRenduList  compteRenduFileList) {
        int j = 1;
        for (File f : files) {
            CompteRenduFile compteRenduFile = getCompteRendu(f.getName(), j++, instancesAutorisesList);
            if (compteRenduFile != null && !compteRenduFileList.contains(compteRenduFile)) {
                compteRenduFileList.add(compteRenduFile);
            }
        }

    }

    //récupération des paramètre extraire de nom de fichier enregistré (nombre de fichier, la personne qui a déposé le fichier, nom de l'instance choisis, nom de fichier)
    public CompteRenduFile getCompteRendu(String fileName, int number, Set<String> instancesAutorisesList) {
        String[] fileNames = fileName.split(UNDERSCORE);

        String instance = fileNames[0];
        logger.info("Titre : " + fileName);
        logger.info("instancesAutorisesList : " + instancesAutorisesList.size() + " : " + instancesAutorisesList);
        if (instancesAutorisesList != null && instancesAutorisesList.contains(instance)) {
            logger.info("CompteRenduFile instance : " + instance);
            return new CompteRenduFile(number, fileName);
        }
        return null;

    }


    public class CompteRenduFile {
        private int number;
        private String name;

        public CompteRenduFile(int number, String name) {
            this.number = number;
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompteRenduFile that = (CompteRenduFile) o;
            return number == that.number && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, name);
        }
    }

    public class CompteRenduData {
        private String noEtu;
        private String nom;
        private String prenom;
        private String dateNaissance;
        private String etat;
        private String statut;
        private String champsErreur;
        private String raison;
        private String est;

        public CompteRenduData(String line) {
            String[] champs = line.split(";");
            this.noEtu = champs[0];
            this.nom = champs[1];
            this.prenom = champs[2];
            this.dateNaissance = champs[3];
            this.etat = champs[7];
            this.statut = champs[8];
            this.champsErreur = champs[9];
            this.raison = champs[10];
            this.est = champs[11];
        }

        public String getNoEtu() {
            return noEtu;
        }

        public void setNoEtu(String noEtu) {
            this.noEtu = noEtu;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getDateNaissance() {
            return dateNaissance;
        }

        public void setDateNaissance(String dateNaissance) {
            this.dateNaissance = dateNaissance;
        }

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }

        public String getStatut() {
            return statut;
        }

        public void setStatut(String statut) {
            this.statut = statut;
        }

        public String getChampsErreur() {
            return champsErreur;
        }

        public void setChampsErreur(String champsErreur) {
            this.champsErreur = champsErreur;
        }

        public String getRaison() {
            return raison;
        }

        public void setRaison(String raison) {
            this.raison = raison;
        }

        public String getEst() {
            return est;
        }

        public void setEst(String est) {
            this.est = est;
        }
    }

}
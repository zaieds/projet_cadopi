package com.CADOPI.Project_CADOPI;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static Map<String, String> INSTANCES = null;

    static {
        INSTANCES = new HashMap<>();
        INSTANCES.put("APTS0UGA", "UGA APTS0UGA Test");
        INSTANCES.put("APPRUGA", "UGA APPRUGA Prod");
        INSTANCES.put("APTSSAV", "USMB APTSSAV Test");
        INSTANCES.put("APPRSAV", "USMB APPRSAV Prod");
        INSTANCES.put("APTSINP", "INP APTSINP Test");
        INSTANCES.put("APPRINP", "INP APPRINP Prod");
    }

    public static Map<String, String> INSTANCES_AUTO = null;

    static {
        INSTANCES_AUTO = new HashMap<>();
        INSTANCES_AUTO.put("APTS0UGA", "cn=interu-habU_Appli-CADOPI_Role-DepotUga,ou=groupes,ou=uga,dc=agalan,dc=org");
        INSTANCES_AUTO.put("APPRUGA", "cn=interu-habU_Appli-CADOPI_Role-DepotUga,ou=groupes,ou=uga,dc=agalan,dc=org");
        INSTANCES_AUTO.put("APTSSAV", "cn=interu-habU_Appli-CADOPI_Role-DepotUsmb,ou=groupes,ou=uga,dc=agalan,dc=org");
        INSTANCES_AUTO.put("APPRSAV", "cn=interu-habU_Appli-CADOPI_Role-DepotUsmb,ou=groupes,ou=uga,dc=agalan,dc=org");
        INSTANCES_AUTO.put("APTSINP", "cn=interu-habU_Appli-CADOPI_Role-DepotInp,ou=groupes,ou=uga,dc=agalan,dc=org");
        INSTANCES_AUTO.put("APPRINP", "cn=interu-habU_Appli-CADOPI_Role-DepotInp,ou=groupes,ou=uga,dc=agalan,dc=org");
    }

    public static String EXTENSION_TEXT = "text/plain";
    public static String EXTENSION_OS = "application/octet-stream";

    public static String MESSAGE_SUCCES = "Merci de vous connecter à Apogée pour finaliser le chargement des OPIs de la base ";
    public static String MESSAGE_ERREUR_FORMAT = "Le format de fichier n'est pas correct ! Les formats corrects : .txt ou .dat";
    public static String MESSAGE_ERREUR_GENERAL = "Une erreur s'est produite";
    public static String MESSAGE_ERREUR_CHARACTER = "Le nom de fichier n’est pas valide";
}

#!/bin/bash

WORKDIR_INDIVIDU="/data/chgt_opi/storages/individus"
WORKDIR_VOEUX="/data/chgt_opi/storages/voeux"
UGA_INSTANCE_TEST="APTS0UGA"
INP_INSTANCE_TEST="APTSINP"
USMB_INSTANCE_TEST="APTSSAV"
UGA_INSTANCE_PROD="APPRUGA"
INP_INSTANCE_PROD="APPRINP"
USMB_INSTANCE_PROD="APPRSAV"
HOST1="orabd19c-pr1.grenet.fr"
HOST2="orabd19c-pr2.grenet.fr"
# suppression des fichiers voeux si existe
#ssh -l chgt_opi orabd19c-pr1.grenet.fr "rm /data/apogee_appli/apo_APTS0UGA/batch/fic/APTS0UGA/baiadvi2.dat"
FILE_PATH_UGA_TEST="/data/apogee_appli/apo_${UGA_INSTANCE_TEST}/batch/fic/${UGA_INSTANCE_TEST}/baiadvi2.dat"
FILE_PATH_UGA_PROD="/data/apogee_appli/apo_${UGA_INSTANCE_PROD}/batch/fic/${UGA_INSTANCE_PROD}/baiadvi2.dat"
FILE_PATH_INP_TEST="/data/apogee_appli/apo_${INP_INSTANCE_TEST}/batch/fic/${INP_INSTANCE_TEST}/baiadvi2.dat"
FILE_PATH_INP_PROD="/data/apogee_appli/apo_${INP_INSTANCE_PROD}/batch/fic/${INP_INSTANCE_PROD}/baiadvi2.dat"
FILE_PATH_USMB_TEST="/data/apogee_appli/apo_${USMB_INSTANCE_TEST}/batch/fic/${USMB_INSTANCE_TEST}/baiadvi2.dat"
FILE_PATH_USMB_PROD="/data/apogee_appli/apo_${USMB_INSTANCE_PROD}/batch/fic/${USMB_INSTANCE_PROD}/baiadvi2.dat"

#/home/chgt_opi/storages/individus      IN_CREATE       /home/chgt_opi/scriptDepot.sh
#/home/samieadm/individu IN_CREATE       /home/samieadm/script.sh
#https://pablumfication.co.uk/2010/09/23/incron-file-system-event-monitoring/
# Traiter les fichiers individus
cd ${WORKDIR_INDIVIDU}
echo $(pwd)
#####################################
# POUR LES INSTANCES DE TEST INDIVIDU
#####################################
# chercher les fichiers pour chaque instance qui n'a pas encore exécuté
UGAFILE_EXISTE_TEST=$(find -name "${UGA_INSTANCE_TEST}*")
INPFILE_EXISTE_TEST=$(find -name "${INP_INSTANCE_TEST}*")
USMBFILE_EXISTE_TEST=$(find -name "${USMB_INSTANCE_TEST}*")

UGA_TEST_IS_LANCER=false
INP_TEST_IS_LANCER=false
USMB_TEST_IS_LANCER=false
UGA_PROD_IS_LANCER=false
INP_PROD_IS_LANCER=false
USMB_PROD_IS_LANCER=false

[[ -z $UGAFILE_EXISTE_TEST ]] || {
  echo "UGA TEST INDIVIDU"
  UGAFILE=$(ls -rt ${UGA_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${UGAFILE} chgt_opi@orabd19c-pr1.grenet.fr:/data/apogee_appli/apo_${UGA_INSTANCE_TEST}/batch/fic/${UGA_INSTANCE_TEST}/baiadvi1.dat && mv ${UGAFILE} traiter_${UGAFILE}
  #renommer le fichier après traiter
  UGA_TEST_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST1 [[ -f $FILE_PATH_UGA_TEST ]] && ssh -l chgt_opi $HOST1 "rm ${FILE_PATH_UGA_TEST}"
}

[[ -z $INPFILE_EXISTE_TEST ]] || {
  echo "INP TEST INDIVIDU";
  INPFILE=$(ls -rt ${INP_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${INPFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${INP_INSTANCE_TEST}/batch/fic/${INP_INSTANCE_TEST}/baiadvi1.dat && mv ${INPFILE} traiter_${INPFILE}
  INP_TEST_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST2 [[ -f $FILE_PATH_INP_TEST ]] && ssh -l chgt_opi $HOST2 "rm ${FILE_PATH_INP_TEST}"
}

[[ -z $USMBFILE_EXISTE_TEST ]] || {
  echo "USMB TEST INDIVIDU";
  USMBFILE=$(ls -rt ${USMB_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${USMBFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${USMB_INSTANCE_TEST}/batch/fic/${USMB_INSTANCE_TEST}/baiadvi1.dat && mv ${USMBFILE} traiter_${USMBFILE}
  USMB_TEST_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST2 [[ -f $FILE_PATH_USMB_TEST ]] && ssh -l chgt_opi $HOST2 "rm ${FILE_PATH_USMB_TEST}"
}
#####################################
# POUR LES INSTANCES DE PROD INDIVIDU
#####################################
# chercher les fichiers pour chaque instance qui n'a pas encore exécuté
UGAFILE_EXISTE_PROD=$(find -name "${UGA_INSTANCE_PROD}*")
INPFILE_EXISTE_PROD=$(find -name "${INP_INSTANCE_PROD}*")
USMBFILE_EXISTE_PROD=$(find -name "${USMB_INSTANCE_PROD}*")

[[ -z $UGAFILE_EXISTE_PROD ]] || {
  echo "UGA PROD INDIVIDU";
  UGAFILE=$(ls -rt ${UGA_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${UGAFILE} chgt_opi@orabd19c-pr1.grenet.fr:/data/apogee_appli/apo_${UGA_INSTANCE_PROD}/batch/fic/${UGA_INSTANCE_PROD}/baiadvi1.dat && mv ${UGAFILE} traiter_${UGAFILE}
  UGA_PROD_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST1 [[ -f $FILE_PATH_UGA_PROD ]] && ssh -l chgt_opi $HOST1 "rm ${FILE_PATH_UGA_PROD}"
}

[[ -z $INPFILE_EXISTE_PROD ]] || {
  echo "INP PROD INDIVIDU";
  INPFILE=$(ls -rt ${INP_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${INPFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${INP_INSTANCE_PROD}/batch/fic/${INP_INSTANCE_PROD}/baiadvi1.dat &&  mv ${INPFILE} traiter_${INPFILE}
  INP_PROD_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST2 [[ -f $FILE_PATH_INP_PROD ]] && ssh -l chgt_opi $HOST2 "rm ${FILE_PATH_INP_PROD}"
}

[[ -z $USMBFILE_EXISTE_PROD ]] || {
  echo "USMB PROD INDIVIDU";
  USMBFILE=$(ls -rt ${USMB_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_INDIVIDU}/${USMBFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${USMB_INSTANCE_PROD}/batch/fic/${USMB_INSTANCE_PROD}/baiadvi1.dat && mv ${USMBFILE} traiter_${USMBFILE}
  USMB_PROD_IS_LANCER=true
  # suppression des fichiers voeux si existe
  ssh $HOST2 [[ -f $FILE_PATH_USMB_PROD ]] && ssh -l chgt_opi $HOST2 "rm ${FILE_PATH_USMB_PROD}"
}

# Traiter les fichiers voeux
cd ${WORKDIR_VOEUX}
echo $(pwd)
##################################
# POUR LES INSTANCES DE TEST VOEUX
##################################
# chercher les fichiers pour chaque instance qui n'a pas encore exécuté
UGAFILE_EXISTE_TEST=$(find -name "${UGA_INSTANCE_TEST}*")
INPFILE_EXISTE_TEST=$(find -name "${INP_INSTANCE_TEST}*")
USMBFILE_EXISTE_TEST=$(find -name "${USMB_INSTANCE_TEST}*")

[[ -z $UGAFILE_EXISTE_TEST ]] || {
  echo "UGA TEST VOEUX";
  UGAFILE=$(ls -rt ${UGA_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_VOEUX}/${UGAFILE} chgt_opi@orabd19c-pr1.grenet.fr:/data/apogee_appli/apo_${UGA_INSTANCE_TEST}/batch/fic/${UGA_INSTANCE_TEST}/baiadvi2.dat && mv ${UGAFILE} traiter_${UGAFILE}
  UGA_TEST_IS_LANCER=true
}

[[ -z $INPFILE_EXISTE_TEST ]] || {
  echo "INP TEST VOEUX";
  INPFILE=$(ls -rt ${INP_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_VOEUX}/${INPFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${INP_INSTANCE_TEST}/batch/fic/${INP_INSTANCE_TEST}/baiadvi2.dat && mv ${INPFILE} traiter_${INPFILE}
  INP_TEST_IS_LANCER=true
}

[[ -z $USMBFILE_EXISTE_TEST ]] || {
  echo "USMB TEST VOEUX";
  USMBFILE=$(ls -rt ${USMB_INSTANCE_TEST}* | tail -1)
  scp ${WORKDIR_VOEUX}/${USMBFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${USMB_INSTANCE_TEST}/batch/fic/${USMB_INSTANCE_TEST}/baiadvi2.dat && mv ${USMBFILE} traiter_${USMBFILE}
  USMB_PROD_IS_LANCER=true
}
##################################
# POUR LES INSTANCES DE PROD VOEUX
##################################
# chercher les fichiers pour chaque instance qui n'a pas encore exécuté
UGAFILE_EXISTE_PROD=$(find -name "${UGA_INSTANCE_PROD}*")
INPFILE_EXISTE_PROD=$(find -name "${INP_INSTANCE_PROD}*")
USMBFILE_EXISTE_PROD=$(find -name "${USMB_INSTANCE_PROD}*")

[[ -z $UGAFILE_EXISTE_PROD ]] || {
  echo "UGA PROD VOEUX";
  UGAFILE=$(ls -rt ${UGA_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_VOEUX}/${UGAFILE} chgt_opi@orabd19c-pr1.grenet.fr:/data/apogee_appli/apo_${UGA_INSTANCE_PROD}/batch/fic/${UGA_INSTANCE_PROD}/baiadvi2.dat && mv ${UGAFILE} traiter_${UGAFILE}
  UGA_PROD_IS_LANCER=true
}

[[ -z $INPFILE_EXISTE_PROD ]] || {
  echo "INP PROD VOEUX";
  INPFILE=$(ls -rt ${INP_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_VOEUX}/${INPFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${INP_INSTANCE_PROD}/batch/fic/${INP_INSTANCE_PROD}/baiadvi2.dat && mv ${INPFILE} traiter_${INPFILE}
  INP_PROD_IS_LANCER=true
}

[[ -z $USMBFILE_EXISTE_PROD ]] || {
  echo "USMB PROD VOEUX";
  USMBFILE=$(ls -rt ${USMB_INSTANCE_PROD}* | tail -1)
  scp ${WORKDIR_VOEUX}/${USMBFILE} chgt_opi@orabd19c-pr2.grenet.fr:/data/apogee_appli/apo_${USMB_INSTANCE_PROD}/batch/fic/${USMB_INSTANCE_PROD}/baiadvi2.dat && mv ${USMBFILE} traiter_${USMBFILE}
  USMB_PROD_IS_LANCER=true
}

#############################################
# LANCER LES SCRIPTS POUR L'INDIVIDU ET VOEUX
#############################################
[[ "$UGA_TEST_IS_LANCER" == "false" ]] || {
  echo "UGA TEST LANCER"
  ssh -l chgt_opi orabd19c-pr1.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${UGA_INSTANCE_TEST}"
}
[[ "$INP_TEST_IS_LANCER" == "false" ]] || {
  echo "INP TEST LANCER"
  ssh -l chgt_opi orabd19c-pr2.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${INP_INSTANCE_TEST}"
}
[[ "$USMB_TEST_IS_LANCER" == "false" ]] || {
  echo "USMB TEST LANCER"
  ssh -l chgt_opi orabd19c-pr2.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${USMB_INSTANCE_TEST}"
  #ssh -l chgt_opi orabd19c-pr2.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh APTSSAV"
}
[[ "$UGA_PROD_IS_LANCER" == "false" ]] || {
  echo "UGA PROD LANCER"
  ssh -l chgt_opi orabd19c-pr1.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${UGA_INSTANCE_PROD}"
}
[[ "$INP_PROD_IS_LANCER" == "false" ]] || {
  echo "INP PROD LANCER"
  ssh -l chgt_opi orabd19c-pr2.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${INP_INSTANCE_PROD}"
}
[[ "$USMB_PROD_IS_LANCER" == "false" ]] || {
  echo "USMB PROD LANCER"
  ssh -l chgt_opi orabd19c-pr2.grenet.fr "ksh /usr/local/scripts/apogee/commun/chargt_opi/lancer_vider_tablesbatch.ksh ${USMB_INSTANCE_PROD}"
}
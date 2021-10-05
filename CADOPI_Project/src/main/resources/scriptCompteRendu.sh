#!/bin/bash

INSTANCE="APTS0UGA"
INSTANCE="APTSINP"
INSTANCE="APTSSAV"
INSTANCE="APPRUGA"
INSTANCE="APPRINP"
INSTANCE="APPRSAV"
HOST="cadopi-preprod-1.grenet.fr"
WORKDIR="/data/apogee_appli/apo_${INSTANCE}/batch/fic/${INSTANCE}"
PREFIX="CR_OPI_"
WORKDIR_CR="/data/chgt_opi/storages/compterendu"

# Traiter les fichiers individus
cd ${WORKDIR}
echo $(pwd)
#####################################
# POUR LES INSTANCES DE TEST INDIVIDU
#####################################
# chercher les fichiers pour chaque instance qui n'a pas encore exécuté
FILE_NAME=$1

# Si c'est le fichier CR_OPI on copie vers le server de cadopi, sinon on ne fait rien
if [[ "$FILE_NAME" == "$PREFIX"* ]]; then
  scp ${$FILE_NAME} ${HOST}:${WORKDIR_CR}/${INSTANCE}_${$FILE_NAME}
fi

#/data/apogee_appli/apo_APTS0UGA/batch/fic/APTS0UGA IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
#/data/apogee_appli/apo_APTSINP/batch/fic/APTSINP IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
#/data/apogee_appli/apo_APTSSAV/batch/fic/APTSSAV IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
#/data/apogee_appli/apo_APPRUGA/batch/fic/APPRUGA IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
#/data/apogee_appli/apo_APPRINP/batch/fic/APPRINP IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
#/data/apogee_appli/apo_APPRSAV/batch/fic/APPRSAV IN_CLOSE_WRITE /home/chgt_opi/scriptCompteRendu.sh $#
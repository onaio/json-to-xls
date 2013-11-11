#!/bin/bash

[[ -z "$1" ]] && echo "Usage: $0 version-of-json-to-xls-delivery-to-deploy" >&2 && exit 1

VERSION=$1
FILENAME="json-to-xls-${VERSION}.jar"
echo "FETCHING FILE: ${FILENAME}"
ssh prod '\
    export time=`date +%F_%T`; \
    mkdir -p old/backup-json-to-xls.${time} && \
    ([[ `ls | grep -q "json-to-xls.*"; echo $?` = 0 ]] && /bin/mv -f json-to-xls* old/backup-json-to-xls.${time} || true) && \
    wget http://nexus.motechproject.org/content/repositories/json-to-xls/io/ei/jsontoxls/json-to-xls/0.2-SNAPSHOT/'${FILENAME}' && \
    unzip -d json-to-xls '${FILENAME}' && \
    java -jar '${FILENAME}'` server json-to-xls/json-to-xls.yml'

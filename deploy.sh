#!/bin/bash

VERSION="$1"
[[ -z "$1" ]] && echo "Usage: $0 version-of-json-to-xls-delivery-to-deploy" >&2 && exit 1

ssh prod '\
    export time=`date +%F_%T`; \
    mkdir -p old/backup-json-to-xls.${time} && \
    ([[ `ls | grep -q "json-to-xls.*"; echo $?` = 0 ]] && /bin/mv -f json-to-xls* old/backup-json-to-xls.${time} || true) && \
    wget http://nexus.motechproject.org/content/repositories/json-to-xls/io/ei/jsontoxls/json-to-xls/0.2-SNAPSHOT/json-to-xls-0.2-20131106.124454-1.jar && \
    java -jar json-to-xls-0.2-20131106.124454-1.jar'

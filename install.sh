#!/bin/bash

#mvn clean

mvn compile

mvn jar:jar

mvn install:install-file -Dfile=target/waimea-1.0-SNAPSHOT.jar -DgroupId=waimea -DartifactId=waimea -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

exit 0

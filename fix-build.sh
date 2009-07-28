#! /bin/bash

cd target

# fix grid-swing-client
mkdir grisu-client-swing
cd grisu-client-swing
unzip -o ../grisu-clients-0.3-SNAPSHOT-grisu-client-swing.jar
#rm ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
rm -f META-INF/INDEX.LIST
cp ../../grisu-swing/src/main/resources/log4j.properties .
cp ../../gluev12r2-ext-class-map.properties .
jar cmf ../../grisu-swing/MANIFEST.MF ../grisu-dummy.jar .
cd ..

cd ..





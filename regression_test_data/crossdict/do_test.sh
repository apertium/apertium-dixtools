#!/bin/sh

#Here goes the command line

rm -rf dix
cd input
rm -rf dix
# apertium-dixtools cross-param apertium-cy-en.cy.dix -r apertium-cy-en.cy-en.dix -r apertium-eo-en.eo-en.dix apertium-eo-en.eo.dix
#apertium-dixtools cross-param apertium-es-ca.es.dix -r bil-es-ca-per-creuar.dix -r apertium-en-ca.en-ca.dix apertium-en-ca.en.metadix
export CROSSDICS_PATH=../../../../apertium-dixtools 

../../../apertium-dixtools cross-param apertium-es-ca.es.dix -r apertium-es-ca.es-ca.dix -r apertium-en-ca.en-ca.dix apertium-en-ca.en.metadix
rm -rf ../actual_output
mv dix ../actual_output
cd ..


echo -------------------------------
res=FAILED
diff -bBw -x .svn -I processed expected_output actual_output && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
BIN=../../apertium-dixtools
#DIX=../../test/sample.metadix
DIX=../../test/sample.eo-en.dix

$BIN dix2tiny $DIX eo-en Esperanto-English all

rm -rf actual_output
mkdir actual_output
mv eo-en-data.cc actual_output
rm -f eo-en-apertium-palm.pdb
cd actual_output
unzip ../eo-en-data.zip
rm -f ../eo-en-data.zip
cd ..

echo -------------------------------
res=FAILED
diff -r -x .svn expected_output actual_output && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

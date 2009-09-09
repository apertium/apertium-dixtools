#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
BIN=../../apertium-dixtools

rm -f actual_output.dix
$BIN merge-morph ../../test/sample.metadix ../../test/sample2.metadix actual_output.dix

echo -------------------------------
res=FAILED
diff -bBw  -I apertium-dixtools  expected_output.dix actual_output.dix && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

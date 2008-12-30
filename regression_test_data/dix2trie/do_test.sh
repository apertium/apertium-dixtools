#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
BIN=../../apertium-dixtools

DIX=../../test/sample.eo-en.dix
rm -f actual_output.txt
$BIN dix2trie $DIX lr actual_output.txt

echo -------------------------------
res=FAILED
diff -bBw -x .svn expected_output.txt actual_output.txt && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

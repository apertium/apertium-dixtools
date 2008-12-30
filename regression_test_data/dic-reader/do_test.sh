#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
export LANG=eo.UTF-8
BIN=../../apertium-dixtools

DIX=../../test/sample.metadix
echo dic-reader list-paradigms $DIX > actual_output.txt
$BIN dic-reader list-paradigms $DIX >> actual_output.txt

echo dic-reader list-lemmas $DIX >> actual_output.txt
$BIN dic-reader list-lemmas $DIX >> actual_output.txt

echo dic-reader list-definitions $DIX >> actual_output.txt
$BIN dic-reader list-definitions $DIX >> actual_output.txt

DIX=../../test/sample.eo-en.dix
echo dic-reader list-pairs $DIX >> actual_output.txt
$BIN dic-reader list-pairs $DIX >> actual_output.txt

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

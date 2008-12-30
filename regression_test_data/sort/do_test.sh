#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
BIN=../../apertium-dixtools

rm -rf actual_output
mkdir actual_output
DIX=../../test/sample.metadix
$BIN sort -mon $DIX actual_output/mon.dix

DIX=../../test/sample.eo-en.dix
$BIN sort -bil $DIX actual_output/bil.dix

echo -------------------------------
res=FAILED
diff -bBw -x .svn expected_output actual_output && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

#!/bin/sh

#Here goes the command line
export CROSSDICS_PATH=../../../apertium-dixtools 
BIN=../../apertium-dixtools

DIX=../../test/sample.eo-en.dix
$BIN reverse-bil $DIX actual_output.dix

echo -------------------------------
res=FAILED
diff -bBw -x .svn expected_output.dix actual_output.dix && res=SUCCESS

echo test $res

if [ "$res" = "FAILED" ]; 
then
	echo test fail: there was a difference;
	exit 1
fi

echo  test success: No difference found compared to expected_output
exit 0 

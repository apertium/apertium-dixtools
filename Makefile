PACKAGE=apertium-dixtools-1.0
DISTFILES=README distr autogen.sh build.xml COPYING Makefile nbproject src test schemas resources regression_test_data

all: 
	ant -quiet jar

install: 
	ant install

tests:
	ant -quiet test

clean:
	ant -quiet clean

dist: 
	ant dist




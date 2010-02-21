#!/bin/sh 

echo "- aclocal."		&& \
aclocal				&& \
echo "- autoconf."		&& \
autoconf			&& \
echo "- automake."		&& \
automake --add-missing --gnu	&& \
echo				&& \
./configure "$@"		&& exit 0

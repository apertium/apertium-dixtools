#!/bin/sh

# echo "- libtoolize."            && \
# if test x$(uname -s) = xDarwin; then glibtoolize --force; else libtoolize --force; fi && \
echo "- aclocal."		&& \
aclocal	-I m4			&& \
echo "- autoconf."		&& \
autoconf			&& \
echo "- automake."		&& \
automake --add-missing --gnu	&& \
echo				&& \
./configure "$@"		&& exit 0
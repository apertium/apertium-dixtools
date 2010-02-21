AC_PREREQ(2.52)

AC_INIT([apertium-dixtools], [0.1.0], [ftyers@users.sourceforge.net])

AC_CANONICAL_SYSTEM

AM_INIT_AUTOMAKE([apertium-dixtools], [0.1.0], no-define)

AC_PROG_CC
AC_PROG_CXX

JAVA=${JAVA-java}
AC_SUBST(JAVA)
JAVAC=${JAVAC-javac}
AC_SUBST(JAVAC)

AM_PROG_GCJ

AC_SET_MAKE

dnl # check for jar and javac

AC_PATH_PROGS(JAVAC,[javac],[])
if test "$JAVAC" = "" ; then
  AC_MSG_ERROR([javac not installed])
fi

AC_PATH_PROGS(JAR,jar,[])
if test "$JAR" = "" ; then
  AC_MSG_ERROR([jar not installed])
fi

JAVACFLAGS=-g:none

AC_OUTPUT([Makefile src/Makefile])

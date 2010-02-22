AC_PREREQ(2.52)

AC_CONFIG_MACRO_DIR(m4)

AC_INIT([dixtools], [0.1.0], [ftyers@users.sourceforge.net])

AC_CANONICAL_SYSTEM

AM_INIT_AUTOMAKE([dixtools], [0.1.0], no-define)

PACKAGE="dixtools"
VERSION="0.1.0"

AC_SUBST(PACKAGE)
AC_SUBST(VERSION)

AC_PROG_CC
AM_PROG_GCJ

AC_JAVA_OPTIONS

AC_CHECK_JAVA_HOME
AC_CHECK_CLASSPATH

AC_PROG_JAVAC
AC_PROG_JAVA
AC_PROG_JAR
AC_PROG_JAVADOC
AC_JAVA_OPTIONS

AC_ARG_ENABLE([native],
              AS_HELP_STRING([--enable-native],
                             [enable generation of native binaries]),
                             [enable_native=$enableval],
                             [enable_native=yes])

if test "x${enable_native}" = "xno"; then
  echo "Native compilation disabled."
else
  if test "x${GCJ}" = "xfalse"; then
    enable_native=no
    echo "Native compilation disabled as gcj has not been found"
  fi
fi

AM_CONDITIONAL([ENABLE_NATIVE], [test x${enable_native} = xyes])

AC_SET_MAKE

AC_OUTPUT([Makefile src/Makefile])

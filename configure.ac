AC_PREREQ(2.52)

AC_INIT([dixtools], [0.1.0], [ftyers@users.sourceforge.net])

AC_CANONICAL_SYSTEM

AM_INIT_AUTOMAKE([dixtools], [0.1.0], no-define)

AC_ARG_WITH(gcj,
  AS_HELP_STRING(--with-gcj,Compile using GCJ (GNU Compiler for Java)))

AC_ARG_WITH(java-source,
  AS_HELP_STRING(--with-java-source=NUM,Assume functionality of given Java/JDK version))

with_java_source=`echo ${with_java_source} | tr A-Z a-z`
case "/${with_java_source}/" in
  /1/) JAVA_SOURCE=1 ;;
  /2/) JAVA_SOURCE=2 ;;
  /4/) JAVA_SOURCE=4 ;;
  /4x/) JAVA_SOURCE=4x ;;
  /5/) JAVA_SOURCE=5 ;;
  /6/) JAVA_SOURCE=6 ;;
  /1.1*/) JAVA_SOURCE=1 ;;
  /1.2*/) JAVA_SOURCE=2 ;;
  /1.3*/) JAVA_SOURCE=2 ;;
  /1.4*+jaxp1.3/) JAVA_SOURCE=4x ;;
  /1.4*/) JAVA_SOURCE=4 ;;
  /gcj4/) JAVA_SOURCE=4x ;;
  /1.5*/) JAVA_SOURCE=5 ;;
  /1.6*/) JAVA_SOURCE=6 ;;
  //) JAVA_SOURCE=4 ;;
  /*/) AC_MSG_ERROR([Invalid version for --with-java-version.
  (Must be 1, 2, 5, 6, or a JDK version such as 1.4.)]) ;;
esac
AC_SUBST(JAVA_SOURCE)

AC_PROG_CC
AC_PROG_CXX
AC_ISC_POSIX
AC_HEADER_STDC

TOP_BUILDDIR=`pwd`

case ${with_gcj} in
  yes)
    test -z "$enable_kawa_frontend" && enable_kawa_frontend="yes"
    test -z "${with_gcj_dbtool}" && with_gcj_dbtool=no

    GCJ_COMPILED_SELECTED=ENABLE_GCJ_COMPILED
    GCJ_SELECTED=WITH_GCJ
    JAVA=${JAVA-gij}
    JAVAC=${JAVAC-"gcj -C"}

    # AM_PROG_GCJ isn't good enough, we have to roll our own.
    # AC_CHECK_TOOL(GCJ, gcj)
    LT_AC_PROG_GCJ
    test -z "$GCJ" && AC_MSG_ERROR([no acceptable gcj found in \$PATH])
    if test "x${GCJFLAGS-unset}" = xunset; then
       GCJFLAGS="-g -O2"
    fi
    AC_SUBST(GCJFLAGS)

   _AM_DEPENDENCIES(GCJ)
    ;;
  *)
    GCJ_COMPILED_SELECTED=DISABLE_GCJ_COMPILED
    GCJ_SELECTED=WITHOUT_GCJ
    JAVA=${JAVA-java}
    JAVAC=${JAVAC-javac}
    dnl needed to match _AM_DEPENDENCIES
    am__fastdepGCJ_TRUE='#'
    ;;
esac
JAVACFLAGS=${JAVACFLAGS--g}

AM_CONDITIONAL(WITH_GCJ, test "$with_gcj" = "yes")

AC_SUBST(JAVA)
AC_SUBST(JAVAC)
AC_SUBST(JAVACFLAGS)

AC_SUBST(GCJ_COMPILED_SELECTED)
AC_SUBST(GCJ_SELECTED)
AC_SUBST(TOP_BUILDDIR)
AC_SUBST(CC)
AC_SUBST(CFLAGS)

AC_SET_MAKE

AC_OUTPUT([Makefile src/Makefile])


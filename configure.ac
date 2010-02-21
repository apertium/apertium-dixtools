AC_PREREQ(2.52)

AC_INIT([dixtools], [0.1.0], [ftyers@users.sourceforge.net])

AC_CANONICAL_SYSTEM

AM_INIT_AUTOMAKE([dixtools], [0.1.0], no-define)

AC_PROG_CC
AC_PROG_CXX
AC_ISC_POSIX
AC_HEADER_STDC

AC_CHECK_PROGS(GCJ, gcj, gcj)
if test -z "$GCJ" ; then
     have_gcj=no
else
     AM_PROG_GCJ

     have_gcj=yes
     #if test "x${GCJFLAGS-unset}" = xunset; then
        GCJFLAGS="-g -O2"
     #fi
     AC_SUBST(GCJFLAGS)
fi

if test x$enable_gcj = xauto ; then
    if test x$have_gcj = xno ; then
        enable_gcj=no
    else
        enable_gcj=yes
    fi
fi

if test x$enable_gcj = xyes; then
    if test x$have_gcj = xno ; then
        AC_MSG_ERROR([Building gcj explicitly required, but gcj not found])
    else
        AC_CHECK_PROGS(JAR, gcj-jar fastjar jar)
    fi
fi

#AC_SUBST(GCJ)
#AC_SUBST(GCJFLAGS)

AC_SET_MAKE

AC_OUTPUT([Makefile src/Makefile])


public class HelloWord {
    public static void main(String [] args) {
        System.out.println("Bonjour" + args[0]);
    }
}

// java HelloWord Malia
"Bonjour la delice"
"c'est comment"
//ligne ajouter par oupere:


"This document describes the installation of PostgreSQL using this source code distribution."

PostgreSQL Installation from Source Code

------------------------------------------------------------------------

This document describes the installation of PostgreSQL using this source
code distribution.

If you are building PostgreSQL for Microsoft Windows, read this document
if you intend to build with MinGW or Cygwin; but if you intend to build
with Microsoft's Visual C++, see the main documentation instead.

------------------------------------------------------------------------


Short Version

    ./configure
    make
    su
    make install
    adduser postgres
    mkdir /usr/local/pgsql/data
    chown postgres /usr/local/pgsql/data
    su - postgres
    /usr/local/pgsql/bin/initdb -D /usr/local/pgsql/data
    /usr/local/pgsql/bin/pg_ctl -D /usr/local/pgsql/data -l logfile start
    /usr/local/pgsql/bin/createdb test
    /usr/local/pgsql/bin/psql test

The long version is the rest of this document.

------------------------------------------------------------------------


Requirements

In general, a modern Unix-compatible platform should be able to run
PostgreSQL. The platforms that had received specific testing at the time
of release are described in the section called "Supported Platforms"
below.

The following software packages are required for building PostgreSQL:

-   GNU make version 3.80 or newer is required; other make programs or
    older GNU make versions will *not* work. (GNU make is sometimes
    installed under the name "gmake".) To test for GNU make enter:

        make --version

-   You need an ISO/ANSI C compiler (at least C99-compliant). Recent
    versions of GCC are recommended, but PostgreSQL is known to build
    using a wide variety of compilers from different vendors.

-   tar is required to unpack the source distribution, in addition to
    either gzip or bzip2.

-   The GNU Readline library is used by default. It allows psql (the
    PostgreSQL command line SQL interpreter) to remember each command
    you type, and allows you to use arrow keys to recall and edit
    previous commands. This is very helpful and is strongly recommended.
    If you don't want to use it then you must specify the
    "--without-readline" option to "configure". As an alternative, you
    can often use the BSD-licensed "libedit" library, originally
    developed on NetBSD. The "libedit" library is GNU
    Readline-compatible and is used if "libreadline" is not found, or if
    "--with-libedit-preferred" is used as an option to "configure". If
    you are using a package-based Linux distribution, be aware that you
    need both the readline and readline-devel packages, if those are
    separate in your distribution.

-   The zlib compression library is used by default. If you don't want
    to use it then you must specify the "--without-zlib" option to
    "configure". Using this option disables support for compressed
    archives in pg_dump and pg_restore.

The following packages are optional. They are not required in the
default configuration, but they are needed when certain build options
are enabled, as explained below:

-   To build the server programming language PL/Perl you need a full
    Perl installation, including the "libperl" library and the header
    files. The minimum required version is Perl 5.8.3. Since PL/Perl
    will be a shared library, the "libperl" library must be a shared
    library also on most platforms. This appears to be the default in
    recent Perl versions, but it was not in earlier versions, and in any
    case it is the choice of whomever installed Perl at your site.
    "configure" will fail if building PL/Perl is selected but it cannot
    find a shared "libperl". In that case, you will have to rebuild and
    install Perl manually to be able to build PL/Perl. During the
    configuration process for Perl, request a shared library.

    If you intend to make more than incidental use of PL/Perl, you
    should ensure that the Perl installation was built with the
    usemultiplicity option enabled (perl -V will show whether this is
    the case).

-   To build the PL/Python server programming language, you need a
    Python installation with the header files and the distutils module.
    The minimum required version is Python 2.4. Python 3 is supported if
    it's version 3.1 or later; but see the PL/Python documentation when
    using Python 3.

    Since PL/Python will be a shared library, the "libpython" library
    must be a shared library also on most platforms. This is not the
    case in a default Python installation built from source, but a
    shared library is available in many operating system distributions.
    "configure" will fail if building PL/Python is selected but it
    cannot find a shared "libpython". That might mean that you either
    have to install additional packages or rebuild (part of) your Python
    installation to provide this shared library. When building from
    source, run Python's configure with the --enable-shared flag.

-   To build the PL/Tcl procedural language, you of course need a Tcl
    installation. The minimum required version is Tcl 8.4.

-   To enable Native Language Support (NLS), that is, the ability to
    display a program's messages in a language other than English, you
    need an implementation of the Gettext API. Some operating systems
    have this built-in (e.g., Linux, NetBSD, Solaris), for other systems
    you can download an add-on package from
    http://www.gnu.org/software/gettext/. If you are using the Gettext
    implementation in the GNU C library then you will additionally need
    the GNU Gettext package for some utility programs. For any of the
    other implementations you will not need it.

-   You need OpenSSL, if you want to support encrypted client
    connections. OpenSSL is also required for random number generation
    on platforms that do not have "/dev/urandom" (except Windows). The
    minimum version required is 0.9.8.

-   You need Kerberos, OpenLDAP, and/or PAM, if you want to support
    authentication using those services.

-   To build the PostgreSQL documentation, there is a separate set of
    requirements; see the main documentation's appendix on
    documentation.

If you are building from a Git tree instead of using a released source
package, or if you want to do server development, you also need the
following packages:

-   Flex and Bison are needed to build from a Git checkout, or if you
    changed the actual scanner and parser definition files. If you need
    them, be sure to get Flex 2.5.31 or later and Bison 1.875 or later.
    Other lex and yacc programs cannot be used.

-   Perl 5.8.3 or later is needed to build from a Git checkout, or if
    you changed the input files for any of the build steps that use Perl
    scripts. If building on Windows you will need Perl in any case. Perl
    is also required to run some test suites.

If you need to get a GNU package, you can find it at your local GNU
mirror site (see https://www.gnu.org/prep/ftp for a list) or at
ftp://ftp.gnu.org/gnu/.

Also check that you have sufficient disk space. You will need about 100
MB for the source tree during compilation and about 20 MB for the
installation directory. An empty database cluster takes about 35 MB;
databases take about five times the amount of space that a flat text
file with the same data would take. If you are going to run the
regression tests you will temporarily need up to an extra 150 MB. Use
the "df" command to check free disk space.

------------------------------------------------------------------------


Installation Procedure

1.  CONFIGURATION

    The first step of the installation procedure is to configure the
    source tree for your system and choose the options you would like.
    This is done by running the "configure" script. For a default
    installation simply enter:

        ./configure

    This script will run a number of tests to determine values for
    various system dependent variables and detect any quirks of your
    operating system, and finally will create several files in the build
    tree to record what it found. You can also run "configure" in a
    directory outside the source tree, if you want to keep the build
    directory separate. This procedure is also called a VPATH build.
    Here's how:

        mkdir build_dir
        cd build_dir
        /path/to/source/tree/configure [options go here]
        make

    The default configuration will build the server and utilities, as
    well as all client applications and interfaces that require only a C
    compiler. All files will be installed under "/usr/local/pgsql" by
    default.

    You can customize the build and installation process by supplying
    one or more of the following command line options to "configure":

    --prefix=PREFIX

        Install all files under the directory "PREFIX" instead of
        "/usr/local/pgsql". The actual files will be installed into
        various subdirectories; no files will ever be installed directly
        into the "PREFIX" directory.

        If you have special needs, you can also customize the individual
        subdirectories with the following options. However, if you leave
        these with their defaults, the installation will be relocatable,
        meaning you can move the directory after installation. (The man
        and doc locations are not affected by this.)

        For relocatable installs, you might want to use "configure"'s
        --disable-rpath option. Also, you will need to tell the
        operating system how to find the shared libraries.

    --exec-prefix=EXEC-PREFIX

        You can install architecture-dependent files under a different
        prefix, "EXEC-PREFIX", than what "PREFIX" was set to. This can
        be useful to share architecture-independent files between hosts.
        If you omit this, then "EXEC-PREFIX" is set equal to "PREFIX"
        and both architecture-dependent and independent files will be
        installed under the same tree, which is probably what you want.

    --bindir=DIRECTORY

        Specifies the directory for executable programs. The default is
        "EXEC-PREFIX/bin", which normally means "/usr/local/pgsql/bin".

    --sysconfdir=DIRECTORY

        Sets the directory for various configuration files, "PREFIX/etc"
        by default.

    --libdir=DIRECTORY

        Sets the location to install libraries and dynamically loadable
        modules. The default is "EXEC-PREFIX/lib".

    --includedir=DIRECTORY

        Sets the directory for installing C and C++ header files. The
        default is "PREFIX/include".

    --datarootdir=DIRECTORY

        Sets the root directory for various types of read-only data
        files. This only sets the default for some of the following
        options. The default is "PREFIX/share".

    --datadir=DIRECTORY

        Sets the directory for read-only data files used by the
        installed programs. The default is "DATAROOTDIR". Note that this
        has nothing to do with where your database files will be placed.

    --localedir=DIRECTORY

        Sets the directory for installing locale data, in particular
        message translation catalog files. The default is
        "DATAROOTDIR/locale".

    --mandir=DIRECTORY

        The man pages that come with PostgreSQL will be installed under
        this directory, in their respective "manx" subdirectories. The
        default is "DATAROOTDIR/man".

    --docdir=DIRECTORY

        Sets the root directory for installing documentation files,
        except "man" pages. This only sets the default for the following
        options. The default value for this option is
        "DATAROOTDIR/doc/postgresql".

    --htmldir=DIRECTORY

        The HTML-formatted documentation for PostgreSQL will be
        installed under this directory. The default is "DATAROOTDIR".

    NOTE:

    Care has been taken to make it possible to install PostgreSQL into
    shared installation locations (such as "/usr/local/include") without
    interfering with the namespace of the rest of the system. First, the
    string "/postgresql" is automatically appended to datadir,
    sysconfdir, and docdir, unless the fully expanded directory name
    already contains the string "postgres" or "pgsql". For example, if
    you choose "/usr/local" as prefix, the documentation will be
    installed in "/usr/local/doc/postgresql", but if the prefix is
    "/opt/postgres", then it will be in "/opt/postgres/doc". The public
    C header files of the client interfaces are installed into
    includedir and are namespace-clean. The internal header files and
    the server header files are installed into private directories under
    includedir. See the documentation of each interface for information
    about how to access its header files. Finally, a private
    subdirectory will also be created, if appropriate, under libdir for
    dynamically loadable modules.

    --with-extra-version=STRING

        Append "STRING" to the PostgreSQL version number. You can use
        this, for example, to mark binaries built from unreleased Git
        snapshots or containing custom patches with an extra version
        string such as a "git describe" identifier or a distribution
        package release number.

    --with-includes=DIRECTORIES

        "DIRECTORIES" is a colon-separated list of directories that will
        be added to the list the compiler searches for header files. If
        you have optional packages (such as GNU Readline) installed in a
        non-standard location, you have to use this option and probably
        also the corresponding "--with-libraries" option.

        Example: --with-includes=/opt/gnu/include:/usr/sup/include.

    --with-libraries=DIRECTORIES

        "DIRECTORIES" is a colon-separated list of directories to search
        for libraries. You will probably have to use this option (and
        the corresponding "--with-includes" option) if you have packages
        installed in non-standard locations.

        Example: --with-libraries=/opt/gnu/lib:/usr/sup/lib.

    --enable-nls[=LANGUAGES]

        Enables Native Language Support (NLS), that is, the ability to
        display a program's messages in a language other than English.
        "LANGUAGES" is an optional space-separated list of codes of the
        languages that you want supported, for example
        --enable-nls='de fr'. (The intersection between your list and
        the set of actually provided translations will be computed
        automatically.) If you do not specify a list, then all available
        translations are installed.

        To use this option, you will need an implementation of the
        Gettext API; see above.

    --with-pgport=NUMBER

        Set "NUMBER" as the default port number for server and clients.
        The default is 5432. The port can always be changed later on,
        but if you specify it here then both server and clients will
        have the same default compiled in, which can be very convenient.
        Usually the only good reason to select a non-default value is if
        you intend to run multiple PostgreSQL servers on the same
        machine.

    --with-perl

        Build the PL/Perl server-side language.

    --with-python

        Build the PL/Python server-side language.

    --with-tcl

        Build the PL/Tcl server-side language.

    --with-tclconfig=DIRECTORY

        Tcl installs the file "tclConfig.sh", which contains
        configuration information needed to build modules interfacing to
        Tcl. This file is normally found automatically at a well-known
        location, but if you want to use a different version of Tcl you
        can specify the directory in which to look for it.

    --with-gssapi

        Build with support for GSSAPI authentication. On many systems,
        the GSSAPI (usually a part of the Kerberos installation) system
        is not installed in a location that is searched by default
        (e.g., "/usr/include", "/usr/lib"), so you must use the options
        "--with-includes" and "--with-libraries" in addition to this
        option. "configure" will check for the required header files and
        libraries to make sure that your GSSAPI installation is
        sufficient before proceeding.

    --with-krb-srvnam=NAME

        The default name of the Kerberos service principal used by
        GSSAPI. postgres is the default. There's usually no reason to
        change this unless you have a Windows environment, in which case
        it must be set to upper case POSTGRES.

    --with-llvm

        Build with support for LLVM based JIT compilation. This requires
        the LLVM library to be installed. The minimum required version
        of LLVM is currently 3.9.

        "llvm-config" will be used to find the required compilation
        options. "llvm-config", and then "llvm-config-$major-$minor" for
        all supported versions, will be searched on PATH. If that would
        not yield the correct binary, use LLVM_CONFIG to specify a path
        to the correct "llvm-config". For example

            ./configure ... --with-llvm LLVM_CONFIG='/path/to/llvm/bin/llvm-config'

        LLVM support requires a compatible "clang" compiler (specified,
        if necessary, using the CLANG environment variable), and a
        working C++ compiler (specified, if necessary, using the CXX
        environment variable).

    --with-icu

        Build with support for the ICU library. This requires the ICU4C
        package to be installed. The minimum required version of ICU4C
        is currently 4.2.

        By default, pkg-config will be used to find the required
        compilation options. This is supported for ICU4C version 4.6 and
        later. For older versions, or if pkg-config is not available,
        the variables ICU_CFLAGS and ICU_LIBS can be specified to
        "configure", like in this example:

            ./configure ... --with-icu ICU_CFLAGS='-I/some/where/include' ICU_LIBS='-L/some/where/lib -licui18n -licuuc -licudata'

        (If ICU4C is in the default search path for the compiler, then
        you still need to specify a nonempty string in order to avoid
        use of pkg-config, for example, ICU_CFLAGS=' '.)

    --with-openssl 

        Build with support for SSL (encrypted) connections. This
        requires the OpenSSL package to be installed. "configure" will
        check for the required header files and libraries to make sure
        that your OpenSSL installation is sufficient before proceeding.

    --with-pam

        Build with PAM (Pluggable Authentication Modules) support.

    --with-bsd-auth

        Build with BSD Authentication support. (The BSD Authentication
        framework is currently only available on OpenBSD.)

    --with-ldap

        Build with LDAP support for authentication and connection
        parameter lookup (see the documentation about client
        authentication and libpq for more information). On Unix, this
        requires the OpenLDAP package to be installed. On Windows, the
        default WinLDAP library is used. "configure" will check for the
        required header files and libraries to make sure that your
        OpenLDAP installation is sufficient before proceeding.

    --with-systemd

        Build with support for systemd service notifications. This
        improves integration if the server binary is started under
        systemd but has no impact otherwise. libsystemd and the
        associated header files need to be installed to be able to use
        this option.

    --without-readline

        Prevents use of the Readline library (and libedit as well). This
        option disables command-line editing and history in psql, so it
        is not recommended.

    --with-libedit-preferred

        Favors the use of the BSD-licensed libedit library rather than
        GPL-licensed Readline. This option is significant only if you
        have both libraries installed; the default in that case is to
        use Readline.

    --with-bonjour

        Build with Bonjour support. This requires Bonjour support in
        your operating system. Recommended on macOS.

    --with-uuid=LIBRARY

        Build the uuid-ossp module (which provides functions to generate
        UUIDs), using the specified UUID library. "LIBRARY" must be one
        of:

        -   "bsd" to use the UUID functions found in FreeBSD, NetBSD,
            and some other BSD-derived systems

        -   "e2fs" to use the UUID library created by the e2fsprogs
            project; this library is present in most Linux systems and
            in macOS, and can be obtained for other platforms as well

        -   "ossp" to use the OSSP UUID library

    --with-ossp-uuid

        Obsolete equivalent of --with-uuid=ossp.

    --with-libxml

        Build with libxml2, enabling SQL/XML support. Libxml2 version
        2.6.23 or later is required for this feature.

        To detect the required compiler and linker options, PostgreSQL
        will query "pkg-config", if that is installed and knows about
        libxml2. Otherwise the program "xml2-config", which is installed
        by libxml2, will be used if it is found. Use of "pkg-config" is
        preferred, because it can deal with multi-architecture
        installations better.

        To use a libxml2 installation that is in an unusual location,
        you can set "pkg-config"-related environment variables (see its
        documentation), or set the environment variable XML2_CONFIG to
        point to the "xml2-config" program belonging to the libxml2
        installation, or set the variables XML2_CFLAGS and XML2_LIBS.
        (If "pkg-config" is installed, then to override its idea of
        where libxml2 is you must either set XML2_CONFIG or set both
        XML2_CFLAGS and XML2_LIBS to nonempty strings.)

    --with-libxslt

        Use libxslt when building the xml2 module. xml2 relies on this
        library to perform XSL transformations of XML.

    --disable-float4-byval

        Disable passing float4 values "by value", causing them to be
        passed "by reference" instead. This option costs performance,
        but may be needed for compatibility with old user-defined
        functions that are written in C and use the "version 0" calling
        convention. A better long-term solution is to update any such
        functions to use the "version 1" calling convention.

    --disable-float8-byval

        Disable passing float8 values "by value", causing them to be
        passed "by reference" instead. This option costs performance,
        but may be needed for compatibility with old user-defined
        functions that are written in C and use the "version 0" calling
        convention. A better long-term solution is to update any such
        functions to use the "version 1" calling convention. Note that
        this option affects not only float8, but also int8 and some
        related types such as timestamp. On 32-bit platforms,
        "--disable-float8-byval" is the default and it is not allowed to
        select "--enable-float8-byval".

    --with-segsize=SEGSIZE

        Set the segment size, in gigabytes. Large tables are divided
        into multiple operating-system files, each of size equal to the
        segment size. This avoids problems with file size limits that
        exist on many platforms. The default segment size, 1 gigabyte,
        is safe on all supported platforms. If your operating system has
        "largefile" support (which most do, nowadays), you can use a
        larger segment size. This can be helpful to reduce the number of
        file descriptors consumed when working with very large tables.
        But be careful not to select a value larger than is supported by
        your platform and the file systems you intend to use. Other
        tools you might wish to use, such as tar, could also set limits
        on the usable file size. It is recommended, though not
        absolutely required, that this value be a power of 2. Note that
        changing this value requires an initdb.

    --with-blocksize=BLOCKSIZE

        Set the block size, in kilobytes. This is the unit of storage
        and I/O within tables. The default, 8 kilobytes, is suitable for
        most situations; but other values may be useful in special
        cases. The value must be a power of 2 between 1 and 32
        (kilobytes). Note that changing this value requires an initdb.

    --with-wal-blocksize=BLOCKSIZE

        Set the WAL block size, in kilobytes. This is the unit of
        storage and I/O within the WAL log. The default, 8 kilobytes, is
        suitable for most situations; but other values may be useful in
        special cases. The value must be a power of 2 between 1 and 64
        (kilobytes). Note that changing this value requires an initdb.

    --disable-spinlocks

        Allow the build to succeed even if PostgreSQL has no CPU
        spinlock support for the platform. The lack of spinlock support
        will result in poor performance; therefore, this option should
        only be used if the build aborts and informs you that the
        platform lacks spinlock support. If this option is required to
        build PostgreSQL on your platform, please report the problem to
        the PostgreSQL developers.

    --disable-thread-safety

        Disable the thread-safety of client libraries. This prevents
        concurrent threads in libpq and ECPG programs from safely
        controlling their private connection handles.

    --with-system-tzdata=DIRECTORY 

        PostgreSQL includes its own time zone database, which it
        requires for date and time operations. This time zone database
        is in fact compatible with the IANA time zone database provided
        by many operating systems such as FreeBSD, Linux, and Solaris,
        so it would be redundant to install it again. When this option
        is used, the system-supplied time zone database in "DIRECTORY"
        is used instead of the one included in the PostgreSQL source
        distribution. "DIRECTORY" must be specified as an absolute path.
        "/usr/share/zoneinfo" is a likely directory on some operating
        systems. Note that the installation routine will not detect
        mismatching or erroneous time zone data. If you use this option,
        you are advised to run the regression tests to verify that the
        time zone data you have pointed to works correctly with
        PostgreSQL.

        This option is mainly aimed at binary package distributors who
        know their target operating system well. The main advantage of
        using this option is that the PostgreSQL package won't need to
        be upgraded whenever any of the many local daylight-saving time
        rules change. Another advantage is that PostgreSQL can be
        cross-compiled more straightforwardly if the time zone database
        files do not need to be built during the installation.

    --without-zlib

        Prevents use of the Zlib library. This disables support for
        compressed archives in pg_dump and pg_restore. This option is
        only intended for those rare systems where this library is not
        available.

    --enable-debug

        Compiles all programs and libraries with debugging symbols. This
        means that you can run the programs in a debugger to analyze
        problems. This enlarges the size of the installed executables
        considerably, and on non-GCC compilers it usually also disables
        compiler optimization, causing slowdowns. However, having the
        symbols available is extremely helpful for dealing with any
        problems that might arise. Currently, this option is recommended
        for production installations only if you use GCC. But you should
        always have it on if you are doing development work or running a
        beta version.

    --enable-coverage

        If using GCC, all programs and libraries are compiled with code
        coverage testing instrumentation. When run, they generate files
        in the build directory with code coverage metrics. This option
        is for use only with GCC and when doing development work.

    --enable-profiling

        If using GCC, all programs and libraries are compiled so they
        can be profiled. On backend exit, a subdirectory will be created
        that contains the "gmon.out" file for use in profiling. This
        option is for use only with GCC and when doing development work.

    --enable-cassert

        Enables assertion checks in the server, which test for many
        "cannot happen" conditions. This is invaluable for code
        development purposes, but the tests can slow down the server
        significantly. Also, having the tests turned on won't
        necessarily enhance the stability of your server! The assertion
        checks are not categorized for severity, and so what might be a
        relatively harmless bug will still lead to server restarts if it
        triggers an assertion failure. This option is not recommended
        for production use, but you should have it on for development
        work or when running a beta version.

    --enable-depend

        Enables automatic dependency tracking. With this option, the
        makefiles are set up so that all affected object files will be
        rebuilt when any header file is changed. This is useful if you
        are doing development work, but is just wasted overhead if you
        intend only to compile once and install. At present, this option
        only works with GCC.

    --enable-dtrace

        Compiles PostgreSQL with support for the dynamic tracing tool
        DTrace.

        To point to the "dtrace" program, the environment variable
        DTRACE can be set. This will often be necessary because "dtrace"
        is typically installed under "/usr/sbin", which might not be in
        the path.

        Extra command-line options for the "dtrace" program can be
        specified in the environment variable DTRACEFLAGS. On Solaris,
        to include DTrace support in a 64-bit binary, you must specify
        DTRACEFLAGS="-64" to configure. For example, using the GCC
        compiler:

            ./configure CC='gcc -m64' --enable-dtrace DTRACEFLAGS='-64' ...

        Using Sun's compiler:

            ./configure CC='/opt/SUNWspro/bin/cc -xtarget=native64' --enable-dtrace DTRACEFLAGS='-64' ...

    --enable-tap-tests

        Enable tests using the Perl TAP tools. This requires a Perl
        installation and the Perl module IPC::Run.

    If you prefer a C compiler different from the one "configure" picks,
    you can set the environment variable CC to the program of your
    choice. By default, "configure" will pick "gcc" if available, else
    the platform's default (usually "cc"). Similarly, you can override
    the default compiler flags if needed with the CFLAGS variable.

    You can specify environment variables on the "configure" command
    line, for example:

        ./configure CC=/opt/bin/gcc CFLAGS='-O2 -pipe'

    Here is a list of the significant variables that can be set in this
    manner:

    BISON

        Bison program

    CC

        C compiler

    CFLAGS

        options to pass to the C compiler

    CLANG

        path to "clang" program used to process source code for inlining
        when compiling with --with-llvm

    CPP

        C preprocessor

    CPPFLAGS

        options to pass to the C preprocessor

    CXX

        C++ compiler

    CXXFLAGS

        options to pass to the C++ compiler

    DTRACE

        location of the "dtrace" program

    DTRACEFLAGS

        options to pass to the "dtrace" program

    FLEX

        Flex program

    LDFLAGS

        options to use when linking either executables or shared
        libraries

    LDFLAGS_EX

        additional options for linking executables only

    LDFLAGS_SL

        additional options for linking shared libraries only

    LLVM_CONFIG

        "llvm-config" program used to locate the LLVM installation.

    MSGFMT

        "msgfmt" program for native language support

    PERL

        Perl interpreter program. This will be used to determine the
        dependencies for building PL/Perl. The default is "perl".

    PYTHON

        Python interpreter program. This will be used to determine the
        dependencies for building PL/Python. Also, whether Python 2 or 3
        is specified here (or otherwise implicitly chosen) determines
        which variant of the PL/Python language becomes available. See
        the PL/Python documentation for more information. If this is not
        set, the following are probed in this order:
        python python3 python2.

    TCLSH

        Tcl interpreter program. This will be used to determine the
        dependencies for building PL/Tcl, and it will be substituted
        into Tcl scripts.

    XML2_CONFIG

        "xml2-config" program used to locate the libxml2 installation

    Sometimes it is useful to add compiler flags after-the-fact to the
    set that were chosen by "configure". An important example is that
    gcc's "-Werror" option cannot be included in the CFLAGS passed to
    "configure", because it will break many of "configure"'s built-in
    tests. To add such flags, include them in the COPT environment
    variable while running "make". The contents of COPT are added to
    both the CFLAGS and LDFLAGS options set up by "configure". For
    example, you could do

        make COPT='-Werror'

    or

        export COPT='-Werror'
        make

    NOTE:

    When developing code inside the server, it is recommended to use the
    configure options "--enable-cassert" (which turns on many run-time
    error checks) and "--enable-debug" (which improves the usefulness of
    debugging tools).

    If using GCC, it is best to build with an optimization level of at
    least "-O1", because using no optimization ("-O0") disables some
    important compiler warnings (such as the use of uninitialized
    variables). However, non-zero optimization levels can complicate
    debugging because stepping through compiled code will usually not
    match up one-to-one with source code lines. If you get confused
    while trying to debug optimized code, recompile the specific files
    of interest with "-O0". An easy way to do this is by passing an
    option to make: "make PROFILE=-O0 file.o".

    The COPT and PROFILE environment variables are actually handled
    identically by the PostgreSQL makefiles. Which to use is a matter of
    preference, but a common habit among developers is to use PROFILE
    for one-time flag adjustments, while COPT might be kept set all the
    time.

2.  BUILD

    To start the build, type either of:

        make
        make all

    (Remember to use GNU make.) The build will take a few minutes
    depending on your hardware. The last line displayed should be:

        All of PostgreSQL successfully made. Ready to install.

    If you want to build everything that can be built, including the
    documentation (HTML and man pages), and the additional modules
    ("contrib"), type instead:

        make world

    The last line displayed should be:

        PostgreSQL, contrib, and documentation successfully made. Ready to install.

    If you want to build everything that can be built, including the
    additional modules ("contrib"), but without the documentation, type
    instead:

        make world-bin

    If you want to invoke the build from another makefile rather than
    manually, you must unset MAKELEVEL or set it to zero, for instance
    like this:

        build-postgresql:
                $(MAKE) -C postgresql MAKELEVEL=0 all

    Failure to do that can lead to strange error messages, typically
    about missing header files.

3.  REGRESSION TESTS

    If you want to test the newly built server before you install it,
    you can run the regression tests at this point. The regression tests
    are a test suite to verify that PostgreSQL runs on your machine in
    the way the developers expected it to. Type:

        make check

    (This won't work as root; do it as an unprivileged user.) See the
    file "src/test/regress/README" and the documentation for detailed
    information about interpreting the test results. You can repeat this
    test at any later time by issuing the same command.

4.  INSTALLING THE FILES

    NOTE:

    If you are upgrading an existing system be sure to read the
    documentation, which has instructions about upgrading a cluster.

    To install PostgreSQL enter:

        make install

    This will install files into the directories that were specified in
    Step 1. Make sure that you have appropriate permissions to write
    into that area. Normally you need to do this step as root.
    Alternatively, you can create the target directories in advance and
    arrange for appropriate permissions to be granted.

    To install the documentation (HTML and man pages), enter:

        make install-docs

    If you built the world above, type instead:

        make install-world

    This also installs the documentation.

    If you built the world without the documentation above, type
    instead:

        make install-world-bin

    You can use make install-strip instead of make install to strip the
    executable files and libraries as they are installed. This will save
    some space. If you built with debugging support, stripping will
    effectively remove the debugging support, so it should only be done
    if debugging is no longer needed. install-strip tries to do a
    reasonable job saving space, but it does not have perfect knowledge
    of how to strip every unneeded byte from an executable file, so if
    you want to save all the disk space you possibly can, you will have
    to do manual work.

    The standard installation provides all the header files needed for
    client application development as well as for server-side program
    development, such as custom functions or data types written in C.
    (Prior to PostgreSQL 8.0, a separate make     install-all-headers
    command was needed for the latter, but this step has been folded
    into the standard install.)

    CLIENT-ONLY INSTALLATION:  If you want to install only the client
    applications and interface libraries, then you can use these
    commands:

        make -C src/bin install
        make -C src/include install
        make -C src/interfaces install
        make -C doc install

    "src/bin" has a few binaries for server-only use, but they are
    small.

UNINSTALLATION:  To undo the installation use the command "make
uninstall". However, this will not remove any created directories.

CLEANING:  After the installation you can free disk space by removing
the built files from the source tree with the command "make clean". This
will preserve the files made by the "configure" program, so that you can
rebuild everything with "make" later on. To reset the source tree to the
state in which it was distributed, use "make distclean". If you are
going to build for several platforms within the same source tree you
must do this and re-configure for each platform. (Alternatively, use a
separate build tree for each platform, so that the source tree remains
unmodified.)

If you perform a build and then discover that your "configure" options
were wrong, or if you change anything that "configure" investigates (for
example, software upgrades), then it's a good idea to do "make
distclean" before reconfiguring and rebuilding. Without this, your
changes in configuration choices might not propagate everywhere they
need to.

------------------------------------------------------------------------


Post-Installation Setup

------------------------------------------------------------------------

Shared Libraries

On some systems with shared libraries you need to tell the system how to
find the newly installed shared libraries. The systems on which this is
*not* necessary include FreeBSD, HP-UX, Linux, NetBSD, OpenBSD, and
Solaris.

The method to set the shared library search path varies between
platforms, but the most widely-used method is to set the environment
variable LD_LIBRARY_PATH like so: In Bourne shells ("sh", "ksh", "bash",
"zsh"):

    LD_LIBRARY_PATH=/usr/local/pgsql/lib
    export LD_LIBRARY_PATH

or in "csh" or "tcsh":

    setenv LD_LIBRARY_PATH /usr/local/pgsql/lib

Replace /usr/local/pgsql/lib with whatever you set "--libdir" to in Step
1. You should put these commands into a shell start-up file such as
"/etc/profile" or "~/.bash_profile". Some good information about the
caveats associated with this method can be found at
http://xahlee.info/UnixResource_dir/_/ldpath.html.

On some systems it might be preferable to set the environment variable
LD_RUN_PATH *before* building.

On Cygwin, put the library directory in the PATH or move the ".dll"
files into the "bin" directory.

If in doubt, refer to the manual pages of your system (perhaps "ld.so"
or "rld"). If you later get a message like:

    psql: error in loading shared libraries
    libpq.so.2.1: cannot open shared object file: No such file or directory

then this step was necessary. Simply take care of it then.

If you are on Linux and you have root access, you can run:

    /sbin/ldconfig /usr/local/pgsql/lib

(or equivalent directory) after installation to enable the run-time
linker to find the shared libraries faster. Refer to the manual page of
"ldconfig" for more information. On FreeBSD, NetBSD, and OpenBSD the
command is:

    /sbin/ldconfig -m /usr/local/pgsql/lib

instead. Other systems are not known to have an equivalent command.

------------------------------------------------------------------------

Environment Variables

If you installed into "/usr/local/pgsql" or some other location that is
not searched for programs by default, you should add
"/usr/local/pgsql/bin" (or whatever you set "--bindir" to in Step 1)
into your PATH. Strictly speaking, this is not necessary, but it will
make the use of PostgreSQL much more convenient.

To do this, add the following to your shell start-up file, such as
"~/.bash_profile" (or "/etc/profile", if you want it to affect all
users):

    PATH=/usr/local/pgsql/bin:$PATH
    export PATH

If you are using "csh" or "tcsh", then use this command:

    set path = ( /usr/local/pgsql/bin $path )

To enable your system to find the man documentation, you need to add
lines like the following to a shell start-up file unless you installed
into a location that is searched by default:

    MANPATH=/usr/local/pgsql/share/man:$MANPATH
    export MANPATH

The environment variables PGHOST and PGPORT specify to client
applications the host and port of the database server, overriding the
compiled-in defaults. If you are going to run client applications
remotely then it is convenient if every user that plans to use the
database sets PGHOST. This is not required, however; the settings can be
communicated via command line options to most client programs.

------------------------------------------------------------------------


Getting Started

The following is a quick summary of how to get PostgreSQL up and running
once installed. The main documentation contains more information.

1.  Create a user account for the PostgreSQL server. This is the user
    the server will run as. For production use you should create a
    separate, unprivileged account ("postgres" is commonly used). If you
    do not have root access or just want to play around, your own user
    account is enough, but running the server as root is a security risk
    and will not work.

        adduser postgres

2.  Create a database installation with the "initdb" command. To run
    "initdb" you must be logged in to your PostgreSQL server account. It
    will not work as root.

        root# mkdir /usr/local/pgsql/data
        root# chown postgres /usr/local/pgsql/data
        root# su - postgres
        postgres$ /usr/local/pgsql/bin/initdb -D /usr/local/pgsql/data

    The "-D" option specifies the location where the data will be
    stored. You can use any path you want, it does not have to be under
    the installation directory. Just make sure that the server account
    can write to the directory (or create it, if it doesn't already
    exist) before starting "initdb", as illustrated here.

3.  At this point, if you did not use the "initdb" -A option, you might
    want to modify "pg_hba.conf" to control local access to the server
    before you start it. The default is to trust all local users.

4.  The previous "initdb" step should have told you how to start up the
    database server. Do so now. The command should look something like:

        /usr/local/pgsql/bin/pg_ctl -D /usr/local/pgsql/data start

    To stop a server running in the background you can type:

        /usr/local/pgsql/bin/pg_ctl -D /usr/local/pgsql/data stop

5.  Create a database:

        /usr/local/pgsql/bin/createdb testdb

    Then enter:

        /usr/local/pgsql/bin/psql testdb

    to connect to that database. At the prompt you can enter SQL
    commands and start experimenting.

------------------------------------------------------------------------


What Now?

-   The PostgreSQL distribution contains a comprehensive documentation
    set, which you should read sometime. After installation, the
    documentation can be accessed by pointing your browser to
    "/usr/local/pgsql/doc/html/index.html", unless you changed the
    installation directories.

    The first few chapters of the main documentation are the Tutorial,
    which should be your first reading if you are completely new to SQL
    databases. If you are familiar with database concepts then you want
    to proceed with part on server administration, which contains
    information about how to set up the database server, database users,
    and authentication.

-   Usually, you will want to modify your computer so that it will
    automatically start the database server whenever it boots. Some
    suggestions for this are in the documentation.

-   Run the regression tests against the installed server (using "make
    installcheck"). If you didn't run the tests before installation, you
    should definitely do it now. This is also explained in the
    documentation.

-   By default, PostgreSQL is configured to run on minimal hardware.
    This allows it to start up with almost any hardware configuration.
    The default configuration is, however, not designed for optimum
    performance. To achieve optimum performance, several server
    parameters must be adjusted, the two most common being
    shared_buffers and work_mem. Other parameters mentioned in the
    documentation also affect performance.

------------------------------------------------------------------------


Supported Platforms

A platform (that is, a CPU architecture and operating system
combination) is considered supported by the PostgreSQL development
community if the code contains provisions to work on that platform and
it has recently been verified to build and pass its regression tests on
that platform. Currently, most testing of platform compatibility is done
automatically by test machines in the PostgreSQL Build Farm. If you are
interested in using PostgreSQL on a platform that is not represented in
the build farm, but on which the code works or can be made to work, you
are strongly encouraged to set up a build farm member machine so that
continued compatibility can be assured.

In general, PostgreSQL can be expected to work on these CPU
architectures: x86, x86_64, IA64, PowerPC, PowerPC 64, S/390, S/390x,
Sparc, Sparc 64, ARM, MIPS, MIPSEL, and PA-RISC. Code support exists for
M68K, M32R, and VAX, but these architectures are not known to have been
tested recently. It is often possible to build on an unsupported CPU
type by configuring with "--disable-spinlocks", but performance will be
poor.

PostgreSQL can be expected to work on these operating systems: Linux
(all recent distributions), Windows (Win2000 SP4 and later), FreeBSD,
OpenBSD, NetBSD, macOS, AIX, HP/UX, and Solaris. Other Unix-like systems
may also work but are not currently being tested. In most cases, all CPU
architectures supported by a given operating system will work. Look in
the section called "Platform-Specific Notes" below to see if there is
information specific to your operating system, particularly if using an
older system.

If you have installation problems on a platform that is known to be
supported according to recent build farm results, please report it to
<pgsql-bugs@lists.postgresql.org>. If you are interested in porting
PostgreSQL to a new platform, <pgsql-hackers@lists.postgresql.org> is
the appropriate place to discuss that.

------------------------------------------------------------------------


Platform-Specific Notes

This section documents additional platform-specific issues regarding the
installation and setup of PostgreSQL. Be sure to read the installation
instructions, and in particular the section called "Requirements" as
well. Also, check the file "src/test/regress/README" and the
documentation regarding the interpretation of regression test results.

Platforms that are not covered here have no known platform-specific
installation issues.

------------------------------------------------------------------------

AIX

PostgreSQL works on AIX, but AIX versions before about 6.1 have various
issues and are not recommended. You can use GCC or the native IBM
compiler "xlc".

------------------------------------------------------------------------

Memory Management

AIX can be somewhat peculiar with regards to the way it does memory
management. You can have a server with many multiples of gigabytes of
RAM free, but still get out of memory or address space errors when
running applications. One example is loading of extensions failing with
unusual errors. For example, running as the owner of the PostgreSQL
installation:

    =# CREATE EXTENSION plperl;
    ERROR:  could not load library "/opt/dbs/pgsql/lib/plperl.so": A memory address is not in the address space for the process.

Running as a non-owner in the group possessing the PostgreSQL
installation:

    =# CREATE EXTENSION plperl;
    ERROR:  could not load library "/opt/dbs/pgsql/lib/plperl.so": Bad address

Another example is out of memory errors in the PostgreSQL server logs,
with every memory allocation near or greater than 256 MB failing.

The overall cause of all these problems is the default bittedness and
memory model used by the server process. By default, all binaries built
on AIX are 32-bit. This does not depend upon hardware type or kernel in
use. These 32-bit processes are limited to 4 GB of memory laid out in
256 MB segments using one of a few models. The default allows for less
than 256 MB in the heap as it shares a single segment with the stack.

In the case of the plperl example, above, check your umask and the
permissions of the binaries in your PostgreSQL installation. The
binaries involved in that example were 32-bit and installed as mode 750
instead of 755. Due to the permissions being set in this fashion, only
the owner or a member of the possessing group can load the library.
Since it isn't world-readable, the loader places the object into the
process' heap instead of the shared library segments where it would
otherwise be placed.

The "ideal" solution for this is to use a 64-bit build of PostgreSQL,
but that is not always practical, because systems with 32-bit processors
can build, but not run, 64-bit binaries.

If a 32-bit binary is desired, set LDR_CNTRL to MAXDATA=0xn0000000,
where 1 <= n <= 8, before starting the PostgreSQL server, and try
different values and "postgresql.conf" settings to find a configuration
that works satisfactorily. This use of LDR_CNTRL tells AIX that you want
the server to have MAXDATA bytes set aside for the heap, allocated in
256 MB segments. When you find a workable configuration, "ldedit" can be
used to modify the binaries so that they default to using the desired
heap size. PostgreSQL can also be rebuilt, passing
configure      LDFLAGS="-Wl,-bmaxdata:0xn0000000" to achieve the same
effect.

For a 64-bit build, set OBJECT_MODE to 64 and pass CC="gcc -maix64" and
LDFLAGS="-Wl,-bbigtoc" to "configure". (Options for "xlc" might differ.)
If you omit the export of OBJECT_MODE, your build may fail with linker
errors. When OBJECT_MODE is set, it tells AIX's build utilities such as
"ar", "as", and "ld" what type of objects to default to handling.

By default, overcommit of paging space can happen. While we have not
seen this occur, AIX will kill processes when it runs out of memory and
the overcommit is accessed. The closest to this that we have seen is
fork failing because the system decided that there was not enough memory
for another process. Like many other parts of AIX, the paging space
allocation method and out-of-memory kill is configurable on a system- or
process-wide basis if this becomes a problem.

------------------------------------------------------------------------

Cygwin

PostgreSQL can be built using Cygwin, a Linux-like environment for
Windows, but that method is inferior to the native Windows build and
running a server under Cygwin is no longer recommended.

When building from source, proceed according to the Unix-style
installation procedure (i.e., ./configure;     make; etc.), noting the
following Cygwin-specific differences:

-   Set your path to use the Cygwin bin directory before the Windows
    utilities. This will help prevent problems with compilation.

-   The "adduser" command is not supported; use the appropriate user
    management application on Windows NT, 2000, or XP. Otherwise, skip
    this step.

-   The "su" command is not supported; use ssh to simulate su on Windows
    NT, 2000, or XP. Otherwise, skip this step.

-   OpenSSL is not supported.

-   Start "cygserver" for shared memory support. To do this, enter the
    command /usr/sbin/cygserver        &. This program needs to be
    running anytime you start the PostgreSQL server or initialize a
    database cluster ("initdb"). The default "cygserver" configuration
    may need to be changed (e.g., increase SEMMNS) to prevent PostgreSQL
    from failing due to a lack of system resources.

-   Building might fail on some systems where a locale other than C is
    in use. To fix this, set the locale to C by doing "export
    LANG=C.utf8" before building, and then setting it back to the
    previous setting after you have installed PostgreSQL.

-   The parallel regression tests (make check) can generate spurious
    regression test failures due to overflowing the listen() backlog
    queue which causes connection refused errors or hangs. You can limit
    the number of connections using the make variable MAX_CONNECTIONS
    thus:

        make MAX_CONNECTIONS=5 check

    (On some systems you can have up to about 10 simultaneous
    connections.)

It is possible to install "cygserver" and the PostgreSQL server as
Windows NT services. For information on how to do this, please refer to
the "README" document included with the PostgreSQL binary package on
Cygwin. It is installed in the directory "/usr/share/doc/Cygwin".

------------------------------------------------------------------------

macOS

To build PostgreSQL from source on macOS, you will need to install
Apple's command line developer tools, which can be done by issuing

    xcode-select --install

(note that this will pop up a GUI dialog window for confirmation). You
may or may not wish to also install Xcode.

On recent macOS releases, it's necessary to embed the "sysroot" path in
the include switches used to find some system header files. This results
in the outputs of the configure script varying depending on which SDK
version was used during configure. That shouldn't pose any problem in
simple scenarios, but if you are trying to do something like building an
extension on a different machine than the server code was built on, you
may need to force use of a different sysroot path. To do that, set
PG_SYSROOT, for example

    make PG_SYSROOT=/desired/path all

To find out the appropriate path on your machine, run

    xcrun --show-sdk-path

Note that building an extension using a different sysroot version than
was used to build the core server is not really recommended; in the
worst case it could result in hard-to-debug ABI inconsistencies.

You can also select a non-default sysroot path when configuring, by
specifying PG_SYSROOT to configure:

    ./configure ... PG_SYSROOT=/desired/path

This would primarily be useful to cross-compile for some other macOS
version. There is no guarantee that the resulting executables will run
on the current host.

To suppress the "-isysroot" options altogether, use

    ./configure ... PG_SYSROOT=none

(any nonexistent pathname will work). This might be useful if you wish
to build with a non-Apple compiler, but beware that that case is not
tested or supported by the PostgreSQL developers.

macOS's "System Integrity Protection" (SIP) feature breaks make check,
because it prevents passing the needed setting of DYLD_LIBRARY_PATH down
to the executables being tested. You can work around that by doing
make     install before make check. Most PostgreSQL developers just turn
off SIP, though.

------------------------------------------------------------------------

MinGW/Native Windows

PostgreSQL for Windows can be built using MinGW, a Unix-like build
environment for Microsoft operating systems, or using Microsoft's Visual
C++ compiler suite. The MinGW build procedure uses the normal build
system described in this chapter; the Visual C++ build works completely
differently and is described in the documentation.

The native Windows port requires a 32 or 64-bit version of Windows 2000
or later. Earlier operating systems do not have sufficient
infrastructure (but Cygwin may be used on those). MinGW, the Unix-like
build tools, and MSYS, a collection of Unix tools required to run shell
scripts like "configure", can be downloaded from http://www.mingw.org/.
Neither is required to run the resulting binaries; they are needed only
for creating the binaries.

To build 64 bit binaries using MinGW, install the 64 bit tool set from
https://mingw-w64.org/, put its bin directory in the PATH, and run
"configure" with the "--host=x86_64-w64-mingw32" option.

After you have everything installed, it is suggested that you run psql
under "CMD.EXE", as the MSYS console has buffering issues.

------------------------------------------------------------------------

Collecting Crash Dumps on Windows

If PostgreSQL on Windows crashes, it has the ability to generate
minidumps that can be used to track down the cause for the crash,
similar to core dumps on Unix. These dumps can be read using the Windows
Debugger Tools or using Visual Studio. To enable the generation of dumps
on Windows, create a subdirectory named "crashdumps" inside the cluster
data directory. The dumps will then be written into this directory with
a unique name based on the identifier of the crashing process and the
current time of the crash.

------------------------------------------------------------------------

Solaris

PostgreSQL is well-supported on Solaris. The more up to date your
operating system, the fewer issues you will experience.

------------------------------------------------------------------------

Required Tools

You can build with either GCC or Sun's compiler suite. For better code
optimization, Sun's compiler is strongly recommended on the SPARC
architecture. If you are using Sun's compiler, be careful not to select
"/usr/ucb/cc"; use "/opt/SUNWspro/bin/cc".

You can download Sun Studio from
https://www.oracle.com/technetwork/server-storage/solarisstudio/downloads/.
Many GNU tools are integrated into Solaris 10, or they are present on
the Solaris companion CD. If you need packages for older versions of
Solaris, you can find these tools at http://www.sunfreeware.com. If you
prefer sources, look at https://www.gnu.org/prep/ftp.

------------------------------------------------------------------------

configure Complains About a Failed Test Program

If "configure" complains about a failed test program, this is probably a
case of the run-time linker being unable to find some library, probably
libz, libreadline or some other non-standard library such as libssl. To
point it to the right location, set the LDFLAGS environment variable on
the "configure" command line, e.g.,

    configure ... LDFLAGS="-R /usr/sfw/lib:/opt/sfw/lib:/usr/local/lib"

See the ld man page for more information.

------------------------------------------------------------------------

Compiling for Optimal Performance

On the SPARC architecture, Sun Studio is strongly recommended for
compilation. Try using the "-xO5" optimization flag to generate
significantly faster binaries. Do not use any flags that modify behavior
of floating-point operations and errno processing (e.g., "-fast").

If you do not have a reason to use 64-bit binaries on SPARC, prefer the
32-bit version. The 64-bit operations are slower and 64-bit binaries are
slower than the 32-bit variants. On the other hand, 32-bit code on the
AMD64 CPU family is not native, so 32-bit code is significantly slower
on that CPU family.

------------------------------------------------------------------------

Using DTrace for Tracing PostgreSQL

Yes, using DTrace is possible. See the documentation for further
information.

If you see the linking of the "postgres" executable abort with an error
message like:

    Undefined                       first referenced
     symbol                             in file
    AbortTransaction                    utils/probes.o
    CommitTransaction                   utils/probes.o
    ld: fatal: Symbol referencing errors. No output written to postgres
    collect2: ld returned 1 exit status
    make: *** [postgres] Error 1

your DTrace installation is too old to handle probes in static
functions. You need Solaris 10u4 or newer to use DTrace.

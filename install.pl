#!/bin/perl

use strict;
use Carp;
use Getopt::Std;

use vars qw($opt_x $opt_c $opt_j $opt_i $opt_h);

my $usage_str = << "END_USAGE";

my $groupId = "waimea";

my $artifactId = "waimea";

my $version = "1.0";

my $jarFile = "$artifactId-$version-SNAPSHOT.jar";

Options:

    -x  :   Clean the project

    -c  :   Compile the project

    -j  :   Jar the project

    -i  :   Intall the project

END_USAGE

if (!getopts('xcjih')) {
    croak "Invalid options";
}

if ($opt_h) {
    print "$usage_str\n";
    exit 0;
}

if ($opt_x) {
    print "Cleaning...\n";
   `mvn clean`;
}


if ($opt_c) {
    print "Compiling...\n";
    `mvn compile`;
}

if ($opt_j) {
    print "Jarring...\n";
    `mvn jar:jar`;
}

if ($opt_i) {
    print "Installing...\n";
    `mvn install:install-file -Dfile=target/$jarFile -DgroupId=waimea -DartifactId=waimea -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true`;
}

exit 0;
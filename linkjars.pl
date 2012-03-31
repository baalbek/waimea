#!/usr/bin/perl

use strict;

my $repos = "/home/rcs/.m2/repository";

sub mjh {
    my ($prefix,$ver,$group_dir) = @_;
    if (!defined $group_dir) {
        $group_dir = $prefix;
    }
    my $jar = "$prefix-$ver.jar";
    my $repos_jar = "$repos/$group_dir/$prefix/$ver/$jar";
    my %result = ( jar => $jar, repos_jar => $repos_jar ); 
    return \%result;
}

my @jars = ( 
            mjh("junit","3.8.1"),
            mjh("commons-collections","3.2.1"),
            mjh("commons-lang","2.4"),
            mjh("concurrent","1.3.4"),
            mjh("log4j","1.2.16"),
            mjh("clojure","1.2.0","org/clojure"),
            mjh("clojure-contrib","1.2.0","org/clojure"),
            );

sub make_symlink {
    my ($jar_of,$target) = @_;
    if (!defined $target) {
        $target = "target";
    }
    my $jar = "$target/$jar_of->{jar}";
    my $repos_jar = $jar_of->{repos_jar};
    unless (-e $jar) {
        print "Creating symlink $jar...\n";
        symlink ($repos_jar, $jar);
    }
}


foreach my $jar (@jars) {
    make_symlink($jar,"src/main/clojure");
    make_symlink($jar,"src/test/clojure");
}



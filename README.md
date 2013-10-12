# Growth Analysis

## Introduction

In order to run this program, you need the followings

* Hadoop

## Building

Run 
  $ sbt assembly
to generate a 'fat jar' is now available as:

    target/scalding-example-project-0.0.4.jar

## Unit testing

The `assembly` command above runs the test suite - but you can also run this manually with:

    $ sbt test
    <snip>
    [info] + A WordCount job should
	[info]   + count words correctly
	[info] Passed: : Total 2, Failed 0, Errors 0, Passed 2, Skipped 0

## Run on local

Invoke the script 

  $ bash run.sh

to run on your machine. Change the variables inside the script to launch according to your desired parameters

Grisu clients
===========

This package contains example grid client implementations  which are built upon the [grisu client library](https://github.com/grisu/grisu/wiki/Grisu-client-library).

Currently it contains:

* **blender-client**: a client to submit [blender](http://blender.org) jobs to the grid. Supports spitting up one big render job into multiple smaller one to get results back quicker.
* **client-examples**: a collection of small code pieces that demonstrate certain features of the grisu client library
* **gricli**: a commandline shell to submit jobs/transfer files using the grisu backend
* **gridftp-tests**: test-suite to test gridftp performance (not maintained)
* **grid-tests**: test-suite for application end-to-end tests
* **grisu-archetypes**: maven archetypes for creating cli/swing grisu client stubs
* **grisu-client-jython**: a wrapper around the grisu client library that enables the use of python syntax to submit jobs
* **grisu-template-client**: a Java-Swing client that uses text-templates to render application-specific job creation input masks

Prerequisites
--------------------

In order to build Grisu from the svn sources, you need: 

- Sun Java Development Kit (version ≥ 6)
- [git](http://git-scm.com) 
- [Apache Maven](http://maven.apache.org) (version >=2)


Checking out sourcecode
-------------------------------------

`git clone git://github.com/grisu/grisu-clients.git`

Building Grisu using Maven
------------------------------------------

To build one of the above modules, cd into the module root directory of the module to build and execute: 

    cd grisu-clients
    mvn clean install


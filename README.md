# NWB, Sci2, and EpiC

This source tree contains several different tools, including Network Work
Bench, the Science of Science (Sci2) tool, and Epidemiology Cyberinfrastructure
(EpiC).

You can build Sci2 using a combination of Ant and Maven. The other two tools
have not been converted to use Maven yet, and have a fairly tricky build
process, which is not covered here.

The build is done using Tycho, which requires Maven version 3.

To build Sci2 from source, you will first need to build CIShell and cishell-plugins.

## Building CIShell (prerequisite)

CIShell is available from GitHub at https://github.com/CIShell/CIShell . Unfortunately, the
first time you build CIShell, it takes FOREVER as Maven downloads itself, the
Tycho plug-in builder, and a good part of Eclipse. But be patient! Anyway, You
should be able to build CIShell by running:

    git clone https://github.com/CIShell/CIShell
    cd CIShell
    mvn -Pbuild-nonpde clean install
    mvn -o clean install

This will install CIShell's libraries and things into your local Maven
repository, so that they can be used by the Sci2 build. In the future, we may be
able to provide CIShell's components in a public p2 repository, so that you can
build Sci2 without having to take this step.

## Building cishell-plugins (prerequisite)

Next install cishell-plugins which is available from GitHub at
https://github.com/cns-iu/cishell-plugins . This repository of plugins will also
take a while to build. You should be able to build the CIShell plugins by running:

    git clone https://github.com/cns-iu/cishell-plugins
    cd cishell-plugins
    mvn -Pbuild-nonpde clean install
    mvn -o clean install

This will install CIShell's common algorithms, plugins, and things into your local Maven
repository, so that they can be used by the Sci2 build. In the future, we may be
able to provide CIShell's components in a public p2 repository, so that you can
build Sci2 without having to take this step.

### Sci2 Maven Build

If all is well, this should be pretty easy. Simply change to the directory
containing this README file, and run

    mvn -Pbuild-nonpde clean install
    mvn -o clean install

This should build all the plug-ins in NWB, Sci2, and EpiC, and
create a runnable version of the Sci2 tool. This runnable tool is located in
sci2/deployment/edu.iu.sci2.releng/target/products/.

### Finishing the Sci2 Build with Ant

There is Ant build file that completes the Sci2 tool for deployment.

#### Adding Ant-Contrib

Before this will work, you will need to add the
[Ant-Contrib](http://ant-contrib.sourceforge.net/)
library to Ant. You do this by downloading
the library, unzipping it, and placing the jar in one of
[several locations](http://ant.apache.org/manual/install.html#optionalTasks),
the most straightforward of which is ANT_INSTALLATION/lib/. In Eclipse,
you can add the jar to Ant's classpath from "Window -> Preferences ->
Ant -> Runtime -> Classpath".

#### Running Ant

There is one ant build file you need to run. This ant script,
sci2/deployment/edu.iu.sci2.releng/postMavenTasks.xml, makes some minor changes
to prepare the build for deployment.

    ant -f sci2/deployment/edu.iu.sci2.releng/postMavenTasks.xml

So now you should have a complete build of Sci2!  Yay!

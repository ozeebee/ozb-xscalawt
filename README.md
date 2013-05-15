# ozb-xscalawt

Fork of pieceoftheloaf's [XScalaWT](http://pieceoftheloaf.github.io/XScalaWT/) project which is a Scala DSL for Eclipse RCP and SWT applications.

This fork mainly adds missing build functions for some UI elements.

## Build

While the original project uses Maven 3 to build, I have changed the build to use [sbt](http://www.scala-sbt.org/).

This project relies on various eclipse libraries which are not available in public repositories (as far as I know). So you have to install them in your local repository.
To this end, I have created the `install-eclipse-jars` task that downloads and install the required libs in the local ivy repository.

> The `install-eclipse-jars` task depends on another lib : [ozb-scala-utils](http://github.com/ozeebee/ozb-scala-utils) as declared in the [plugins.sbt](project/plugins.sbt) file

Steps:  

1. modify [build.sbt](build.sbt) and adapt the url of the swt lib to your platform (Mac OS X by default)

```
/** URL of the Eclipse update site */
eclipseUpdateSite := "http://eclipse.ialto.org/eclipse/updates/3.7/R-3.7.2-201202080800/"

/** URL to the swt lib to compile against (adapt it to your platform, mine is Mac OS X) */
swtNativeUrl <<= eclipseUpdateSite { _ + "plugins/org.eclipse.swt.cocoa.macosx_3.7.2.v3740f.jar" }

```
	
2. run sbt and install eclipse jars

	`> install-eclipse-jars withSources=true`

3. compile and publish as usual

## Using in other projects

Add a dependency in your build.sbt

	"org.ozb" %% "ozb-xscalawt" % "0.1"

organization := "org.ozb"

name := "ozb-xscalawt"

version := "0.2"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"org.eclipse.jface" % "jface" % "3.11.0",
	"org.eclipse.jface" % "jface-databinding" % "1.7.0",
	"org.eclipse.equinox" % "equinox-common" % "3.7.0",
	"org.eclipse.core" % "core-databinding" % "1.5.0",
	"org.eclipse.core" % "core-databinding-observable" % "1.5.0",
	"org.eclipse.core" % "core-databinding-beans" % "1.3.0",
	"org.eclipse.core" % "core-databinding-property" % "1.5.0",
	"org.eclipse.core" % "core-commands" % "3.7.0",
	// Mac OS X SWT lib
	"org.eclipse.swt"  % "swt-cocoa-macosx-x86_64" % "3.104.1"
	// Windows SWT lib
	//"org.eclipse.swt"  % "swt-win32-win32-x86_64" % "3.104.1"
	// Linux SWT lib
	//"org.eclipse.swt"  % "swt-gtk-linux-x86_64" % "3.104.1"
)

scalacOptions ++= Seq("-deprecation")

/** URL of the Eclipse update site */
eclipseUpdateSite := "http://download.eclipse.org/eclipse/updates/4.5/R-4.5.1-201509040015/"

/** URLs of the required eclipse libs that will be downloaded from the eclipse
 update site and installed in the local ivy repository */
eclipseLibs := Seq(
	// Eclipse Mars Release 1 (4.5.1)
	"org.eclipse.jface_3.11.0.v20150602-1400.jar",
	"org.eclipse.jface.databinding_1.7.0.v20150406-2148.jar",
	"org.eclipse.equinox.common_3.7.0.v20150402-1709.jar",
	"org.eclipse.core.databinding_1.5.0.v20150422-0725.jar",
	"org.eclipse.core.databinding.observable_1.5.0.v20150422-0725.jar",
	"org.eclipse.core.databinding.beans_1.3.0.v20150422-0725.jar",
	"org.eclipse.core.databinding.property_1.5.0.v20150422-0725.jar",
	"org.eclipse.core.commands_3.7.0.v20150422-0725.jar",
	// Mac OS X SWT lib
	"org.eclipse.swt.cocoa.macosx.x86_64_3.104.1.v20150825-0743.jar"
	// Windows SWT lib
	//"org.eclipse.swt.win32.win32.x86_64_3.104.1.v20150825-0743.jar"
	// Linux SWT lib
	//"org.eclipse.swt.gtk.linux.x86_64_3.104.1.v20150825-0743.jar"
)

// allow republishing
isSnapshot := true

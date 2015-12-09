organization := "org.ozb"

name := "ozb-xscalawt-example"

version := "0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"org.ozb" %% "ozb-xscalawt" % "0.2",
	// xscalawt dependencies
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

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

fork in run := true

// options for Mac OS X platform
javaOptions in run ++= Seq("-XstartOnFirstThread")


import Defaults._

//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0")
libraryDependencies += sbtPluginExtra(
    m = "com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0", // Plugin module name and version
    sbtV = "0.12",    // SBT version
    scalaV = "2.9.2"    // Scala version compiled the plugin
)


libraryDependencies += "org.ozb" %% "ozb-scala-utils" % "0.2"



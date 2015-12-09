import sbt._
import Keys._

object XScalaWTBuild extends Build {
	lazy val root = Project(
		id = "ozb-xscalawt", 
		base = file("."),
		settings = Seq(
			//updateTask
		) ++ InstallEclipseLibs.settings
	)

	val swtNativeUrl = SettingKey[String]("swt-native-url", "URL to the native library to compile against")	
	
	lazy val ivyJarPath = SettingKey[Option[String]]("ivy-jar-path", "Path to the Ivy jar file")
	lazy val eclipseUpdateSite = SettingKey[String]("eclipse-update-site", "Eclipse update site containing jars to install")
	lazy val eclipseLibs = SettingKey[Seq[String]]("eclipse-libs", "Eclipse libraries to install (may contain wildcard '*' character)")

	/** extends the update task to download the swt native lib and place it in the unmanaged libs directory */
	val updateTask = update <<= (update, swtNativeUrl, unmanagedBase) map { (updateReport, swtNativeUrl, unmanagedBase) =>
		//println("swtNativeUrl = " + swtNativeUrl)
		//println("unmanagedBase = " + unmanagedBase)
		val url = new java.net.URL(swtNativeUrl)
		val filename = url.getFile().split('/').last
		val targetFile = unmanagedBase / filename
		if (! targetFile.exists) {
			println("downloading [%s] to [%s]" format (url, targetFile))
			IO.download(url, targetFile)
		}
		updateReport
	}
}

object InstallEclipseLibs {
	import sbt.complete.Parser

	import XScalaWTBuild.ivyJarPath
	import XScalaWTBuild.eclipseUpdateSite
	import XScalaWTBuild.eclipseLibs

	lazy val settings = Seq(
		ivyJarPath := None, 
		installEclipseJarsTask
	)

	lazy val installEclipseJars = InputKey[Unit]("install-eclipse-jars", "Install Eclipse jars into local ivy repository")
	//lazy val installEclipseJarsParser = (state: State) => boolOpt("withSources")
	lazy val installEclipseJarsTask = installEclipseJars := {
		val withSources = boolOpt("withSources").parsed.map(_._2).getOrElse(false)
		val ivyJarPathOpt = ivyJarPath.value
		println("install-eclipse-jars")
		println(" withSources = " + withSources)
		println(" ivyJarPathOpt = " + ivyJarPathOpt)
		println(" eclipseUpdateSite = " + eclipseUpdateSite)
		println(" eclipseLibs = " + eclipseLibs)

		val ivyJar = ivyJarPathOpt.getOrElse(
				findIvyJar().map(_.absolutePath).getOrElse(sys.error("could not find ivy jar, please specify it with %s" format ivyJarPath.key.label)))
		println(" ivyJar = " + ivyJar)			

		val installedLibs = IO.withTemporaryDirectory { dir =>
			// download and install libs
			eclipseLibs.value map { lib =>
				val jarUrl = new java.net.URL(eclipseUpdateSite.value + "plugins/" + lib)
				val targetFile = dir / lib
				println(s" downloading [$jarUrl] to [$targetFile] ...")
				IO.download(jarUrl, targetFile)
				val eclipseLib = EclipseLib.fromJar(targetFile)
				if (withSources) {
					val srcJarUrl = new java.net.URL(jarUrl.toExternalForm.replace(lib, eclipseLib.srcName))
					val srcTargetFile = dir / eclipseLib.srcName
					println(" downloading source [%s] to [%s] ..." format (srcJarUrl, srcTargetFile))
					IO.download(srcJarUrl, srcTargetFile)
				}
				val input = IvyLocalRepositoryInstaller.Input(ivyJar, eclipseLib.org, eclipseLib.module, eclipseLib.revision, eclipseLib.jar.absolutePath, eclipseLib.srcJar)
				println("  input = " + input)
				IvyLocalRepositoryInstaller.install(input)
				eclipseLib
			}
		}
		println("\ninstalled libraries:")
		installedLibs foreach { l => 
			println("""%s"%s" %% "%s" %% "%s" %s """ format ("\t", l.org, l.module, l.revision, if (withSources) "withSources()" else ""))
		}
	}

    def findIvyJar(): Option[File] = {
    	val ivyDir = file(System.getProperty("user.home")) / ".ivy2/cache/org.apache.ivy/ivy/jars"
    	if (ivyDir.isDirectory) {
    		val jars = (singleFileFinder(ivyDir) * "*.jar").get
    		jars.headOption
    	} else {
    		None
    	}
    }

	// adapted from sbteclipse (com/typesafe/sbteclipse/core/Eclipse.scala)
	def boolOpt(key: String): Parser[Option[(String, Boolean)]] = {
		import sbt.complete.DefaultParsers._
		// for some reason, cannot use "true" | "false" ...
		val trueParser: Parser[String] = "true"
		(Space ~> key ~ ("=" ~> (trueParser | "false"))).? map { 
			_. map {
				case (k, v) => k -> v.toBoolean
			}
		}
	}

	object EclipseLib {
		private val LibPattern = """(.*)_.*""".r
		def fromJar(jar: File) = {
			val LibPattern(name) = jar.name
			val parts = name.split('.')
			val org = parts.take(3).mkString(".")
			val module = if (parts.length == 3)	parts(2) else parts.drop(2).mkString("-")
			//println("name=%s org=%s module=%s" format (jar.name, rev, org, module))
			EclipseLib(org, module, jar)
		}
	}
	
	case class EclipseLib(org: String, module: String, jar: File) {
		val name = jar.name
		lazy val srcName = name.replace("_", ".source_")
		
		private val RevPattern = """_(\d+\.\d+\.\d+)\.""".r
		
		lazy val revision = {
			val revision: Option[String] = for (RevPattern(rev) <- RevPattern findFirstIn name) yield rev
			revision.getOrElse(sys.error("could not determine revision for jar %s" + name))
		}
		
		def srcJar = {
			val src = jar.getParentFile / srcName
			if (src.canRead()) 
				Some(src.absolutePath)
			else 
				None
		}
	}
}

import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.xml.Elem
import scala.xml.PrettyPrinter
import sbt.IO
import java.io.File

/**
 * Extracted from ozb-scala-utils to remove dependency on this lib
 */
object IvyLocalRepositoryInstaller {

	case class Input(
		var ivyJar: String = null,
		var organisation: String = null,
		var module: String = null,
		var revision: String = null,
		var jar: String = null,
		var src: Option[String] = None
	)
	
	def install(input: Input) {
		// NOTE: using module name as artifact name
		val ivyModule = IvyModule(input.organisation, input.module, input.revision, 
			Seq(Artifact(input.module, "jar", "jar", input.jar)) ++ 
			input.src.map { srcjar =>
				Seq(Artifact(input.module + "-sources", getExt(srcjar), "src", srcjar))
			}.getOrElse(Seq.empty)
		)
		publish(input.ivyJar, ivyModule)		
	}
	
	def publish(ivyJar: String, ivyModule: IvyModule): Boolean = {
		println("installing (%s %% %s %% %s) in ivy local repository" format (ivyModule.organisation, ivyModule.module, ivyModule.revision))
		// create ivyXml
		var ivyXml = new PrettyPrinter(360, 4).format(ivyModule.toIvyXml)
		// create temporary ivy xml file
		IO.withTemporaryFile("ozb", ".xml") { file =>
			IO.write(file, ivyXml.toString())
			// publish
			if (ivyModule.artifacts.size > 1) {
				// copy artifacts file to temp dir and rename them to create a working publish pattern
				IO.withTemporaryDirectory { tempDir =>
					println("created temp dir " + tempDir)
					ivyModule.artifacts foreach { artifact =>
						IO.copyFile(new File(artifact.filePath), new File(tempDir, artifact.name + "." + artifact.artExt))
					}
					val publishPattern = tempDir.getPath() + "/[artifact].[ext]"
					publish(ivyJar, file.getPath(), ivyModule.revision, publishPattern)
				}
			}
			else {
				// if only one artifact, the publish pattern is the file path
				val publishPattern = ivyModule.artifacts(0).filePath
				publish(ivyJar, file.getPath(), ivyModule.revision, publishPattern)
			}
		}
	}

	def publish(ivyJar: String, ivyXmlFile: String, revision: String, pubPattern: String): Boolean = {
		val resolver = "local" // use local resolver
		//var pattern = "[organisation]/[module]/[revision]/[type]s/[artifact].[ext]" // use default pattern
		
		println("ivyXmlFile = " + ivyXmlFile)
		val processBuilder = Process(
				getJavaExecutablePath(), Seq(
					"-jar", ivyJar,
					"-publish", resolver,
					"-publishpattern", pubPattern,
					"-revision", revision,
					// "-status", "integration",
					"-overwrite",
					"-deliverto", ivyXmlFile, // if omitted, ivy will deliver the ivy.xml to the current directory, so we force it to deliver to our temporary file
					"-ivy", ivyXmlFile
					)
		)
		println("processBuilder = " + processBuilder)
		val plogger = ProcessLogger(line => {
			println(line)
		})
		val exitCode = processBuilder ! plogger
		
		println("  exitCode = " + exitCode)
		exitCode == 0
	}
	
	def getJavaExecutablePath(): String = System.getProperty("java.home") + ( 
		if (System.getProperty("os.name").startsWith("Windows"))
			"/bin/java.exe"
		else
			"/bin/java"
	)
	
	def getExt(name: String) = {
		val pos = name.lastIndexOf(".")
		if (pos == -1 || name.length() == pos+1)
			sys.error(s"cannot determine file extension for path ${name}")
		name.substring(pos+1)
	}
	
	case class IvyModule(
			organisation: String, 
			module: String, 
			revision: String,
			artifacts: Seq[Artifact]) {
		
		def toIvyXml: xml.Elem =
			<ivy-module version="2.0">
			<info organisation={organisation} module={module}/>
				<publications> { artifacts.map { artifact =>
						<artifact name={artifact.name} ext={artifact.artExt} type={artifact.artType}/>
				} }	</publications>
			</ivy-module>
	}
	
	case class Artifact(
			name: String, 
			artExt:String, 
			artType: String,
			filePath: String
	)
}

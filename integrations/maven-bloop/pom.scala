import org.sonatype.maven.polyglot.scala.model._
import scala.collection.immutable.Seq

implicit val scalaVersion = ScalaVersion("2.12.6")

object Deps {
  val scalaLibrary = "org.scala-lang" % "scala-library" % "2.12.6"
  val bloopConfig = "ch.epfl.scala" %% "bloop-config" % "1.0.0-M10+79-97acb2ec+20180606-1250"
  val mavenCore = "org.apache.maven" % "maven-core" % "3.5.2"
  val mavenPluginApi = "org.apache.maven" % "maven-plugin-api" % "3.5.2"
  val mavenPluginAnnotations = "org.apache.maven.plugin-tools" % "maven-plugin-annotations" % "3.5"
  val junit4 = "junit" % "junit" % "4.11"
  val mpCompiler = "org.apache.maven.plugins" % "maven-compiler-plugin" % "3.7.0"
  val mpScala = "net.alchim31.maven" % "scala-maven-plugin" % "3.3.3-SNAPSHOT"
  val mpInvoker = "org.apache.maven.plugins" % "maven-invoker-plugin" % "3.0.1"
  val mpPlugin = "org.apache.maven.plugins" % "maven-plugin-plugin" % "3.5.2"
}

Model(
  gav = "ch.epfl.scala" %% "maven-bloop" % "1.0-SNAPSHOT",
  packaging = "maven-plugin",
  name = "maven-bloop",
  url = "http://www.example.com",
  dependencies = Seq(
    Deps.scalaLibrary % "provided",
    Deps.bloopConfig,
    Deps.mavenCore % "provided",
    Deps.mavenPluginApi,
    Deps.mavenPluginAnnotations,
    Deps.mpScala,
    Deps.junit4 % "test"
  ),
  properties = Map(
    "maven.compiler.source" -> "1.8",
    "maven.compiler.target" -> "1.8",
    "project.build.sourceEncoding" -> "UTF-8"
  ),
  build = Build(
    plugins = Seq(
      // skip Java Compiler
      Plugin(
        Deps.mpCompiler,
        configuration = Config(
          skip = "true",
          skipMain = "true"
        )
      ),
      // Scala+Java Compiler
      Plugin(
        Deps.mpScala,
        executions = Seq(
          Execution(
            goals = Seq("add-source", "compile", "testCompile")
          )
        ),
        configuration = Config(
          recompileMode = "incremental",
          useZincServer = "true"
        )
      ),
      // Plugin config
      Plugin(
        Deps.mpPlugin,
        executions = Seq(
          Execution(id = "default-descriptor", phase = "process-classes", goals = Seq("descriptor")),
          Execution(id = "help-goal", goals = Seq("helpmojo"),
            configuration = Config(skipErrorNoDescriptorsFound = "true"))
        )
      ),
      // Integration test specific maven projects
      Plugin(
        Deps.mpInvoker,
        configuration = Config(
          addTestClassPath = "true",
          debug = "true",
          settingsFile = "src/it/settings.xml",
          localRepositoryPath = "${project.build.directory}/local-repo",
          postBuildHookScript = "verify",
          goals = "${project.groupId}:${project.artifactId}:${project.version}:bloopInstall"
        ),
        executions = Seq(
          Execution(
            id = "integration-test",
            goals = Seq("install", "run")
          )
        )
      )
    )
  ),
  modelVersion = "4.0.0"
)

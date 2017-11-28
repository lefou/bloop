package bloop.cli

import caseapp.{ExtraName, HelpMessage, Recurse}

object Commands {
  sealed trait Command {
    def cliOptions: CliOptions
  }
  case class About(
      @Recurse cliOptions: CliOptions = CliOptions.default
  ) extends Command

  case class Compile(
      @ExtraName("p")
      @HelpMessage("The project to compile.")
      project: String,
      @HelpMessage("If set, it compiles incrementally. By default, true.")
      incremental: Boolean = true,
      @Recurse cliOptions: CliOptions = CliOptions.default,
  ) extends Command

  case class Test(
      @ExtraName("p")
      @HelpMessage("The project to test.")
      project: String,
      @ExtraName("all")
      @HelpMessage("If set, also runs the tests in dependencies. Defaults to true.")
      aggregate: Boolean = false,
      @Recurse cliOptions: CliOptions = CliOptions.default
  ) extends Command

  case class Clean(
      @ExtraName("p")
      @HelpMessage("The projects to clean.")
      projects: List[String],
      @Recurse cliOptions: CliOptions = CliOptions.default,
  ) extends Command
}

inThisBuild(
  Seq(
    scalaVersion := "3.1.3",
    versionScheme := Some("early-semver"),

    // Github Workflow
    githubWorkflowPublishTargetBranches := Seq(), // Don't publish anywhere
    githubWorkflowBuild ++= Seq(
      WorkflowStep.Sbt(List("check"), name = Some("Check Formatting"))
    ),

    // Scalafix
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
  )
)

val commonSettings = Seq(
  scalacOptions -= "-Xfatal-warnings",
  libraryDependencies ++= Dependencies.all,
  publish := {},
  publish / skip := true,
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)

// Commands
addCommandAlias("build", "prepare; test")
addCommandAlias("testAll", "all test")
addCommandAlias("prepare", "fix; fmt")
addCommandAlias("fix", "all scalafix test:scalafix")
addCommandAlias(
  "fixCheck",
  "; scalafix --check ; test:scalafix --check",
)
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("check", "fixCheck; fmtCheck")

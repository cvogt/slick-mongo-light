import sbt._
import Keys._

object MyBuild extends Build{
  val repoKind = SettingKey[String]("repo-kind", "Maven repository kind (\"snapshots\" or \"releases\")")
  val projectName = "slick-mongo-light"
  lazy val aRootProject = Project(id = projectName, base = file("."),
    settings = Seq(
      name := projectName,
      scalaVersion := "2.11.5",
      description := "Composable Records and type-indexed Maps for Scala",
      libraryDependencies ++=   Seq(
        "com.typesafe.play" %% "play-json" % "2.3.4" % "optional",
        "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23" % "optional",
        "org.mongodb" %% "casbah" % "2.8.0" % "optional",
        "org.slf4j" % "slf4j-nop" % "1.6.4" % "test",
        "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.46.4" % "test",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      ),
      scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked"),
      //scalacOptions ++= Seq("-Xprint:patmat", "-Xshow-phases"),
      testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oFD"),
      parallelExecution := false, // <- until TMap thread-safety issues are resolved
      version := "0.1",
      organizationName := "Jan Christopher Vogt",
      organization := "org.cvogt",
      scalacOptions in (Compile, doc) <++= (version,sourceDirectory in Compile,name).map((v,src,n) => Seq(
        "-doc-title", n,
        "-doc-version", v,
        "-doc-footer", projectName+" is developed by Jan Christopher Vogt.",
        "-sourcepath", src.getPath, // needed for scaladoc to strip the location of the linked source path
        "-doc-source-url", "https://github.com/cvogt/"+projectName+"/blob/"+v+"/src/main€{FILE_PATH}.scala",
        "-implicits",
        "-diagrams", // requires graphviz
        "-groups"
      )),
      repoKind <<= (version)(v => if(v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"),
      //publishTo <<= (repoKind)(r => Some(Resolver.file("test", file("c:/temp/repo/"+r)))),
      publishTo <<= (repoKind){
        case "snapshots" => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        case "releases" =>  Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
      },
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      makePomConfiguration ~= { _.copy(configurations = Some(Seq(Compile, Runtime, Optional))) },
      licenses += ("Creative Commons Attribution-ShareAlike 4.0 International", url("https://creativecommons.org/licenses/by-sa/4.0/")),
      homepage := Some(url("http://github.com/cvogt/"+projectName)),
      startYear := Some(2015),
      pomExtra :=
        <developers>
          <developer>
            <id>cvogt</id>
            <name>Jan Christopher Vogt</name>
            <timezone>-5</timezone>
            <url>https://github.com/cvogt/</url>
          </developer>
        </developers>
          <scm>
            <url>git@github.com:cvogt/{projectName}.git</url>
            <connection>scm:git:git@github.com:cvogt/{projectName}.git</connection>
          </scm>
    )
  )
}

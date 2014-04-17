package sbtdartpub

import sbt._
import sbt.Keys._

object Plugin extends sbt.Plugin {


  object Keys {
    // pub build
    val `pub-build` = taskKey[Seq[File]]("runs pub build in dart folder.")
    val `pub-build-debug` = taskKey[Seq[File]]("runs pub build in debug mode.")
    val `pub-serve` = taskKey[Unit]("runs pub serve in dart folder.")
    val dartSourceFolder = settingKey[File]("folder for the Dart sources. Should contain pubspec.yaml.")
    val dartTargetFolder = settingKey[File]("folder where Dart build output is copied. By default in src/main/resources/public")
  }

  val defaultSettings = Seq(
    Keys.dartSourceFolder := file("src/main/dart"),
    Keys.dartTargetFolder := (resourceManaged in Compile).value / "public",
    cleanFiles <+= Keys.dartSourceFolder { base => base / "build" },


    Keys.`pub-build` := {
      val s: TaskStreams = streams.value
      pub_build(Keys.dartSourceFolder.value, Keys.dartTargetFolder.value, s.log).toSeq
    },

    Keys.`pub-build-debug` := {
      val s: TaskStreams = streams.value
      pub_build(Keys.dartSourceFolder.value, Keys.dartTargetFolder.value, s.log, Seq("pub", "build", "--mode", "debug")).toSeq
    },

    Keys.`pub-serve` := {
      val s: TaskStreams = streams.value
      // use "pub build" to build the dart package
      run(
        Process(Seq("pub", "serve", "--mode=debug", "--force-poll"), Keys.dartSourceFolder.value),
        s.log)
    },

    resourceGenerators in Compile <+= Def.task {

      val s: TaskStreams = streams.value

      val cachedFun = FileFunction.cached(s.cacheDirectory / "pub",
        FilesInfo.lastModified, /* inStyle */
        FilesInfo.exists) /* outStyle */ {
        (in: Set[File]) =>
          pub_build(Keys.dartSourceFolder.value, Keys.dartTargetFolder.value, s.log): Set[File]
      }

      // track all source folders under dart, except "build", since it is modified for each build
      val srcFolders = Keys.dartSourceFolder.value.listFiles.filter(_.name != "build")

      cachedFun(srcFolders.toSet).toSeq

    }
  )

  private def pub_build(dartSource: File, targetFolder: File, log: Logger, cmd: Seq[String] = Seq("pub", "build")): Set[File] = {


    log.info(s"Dart pub build in $dartSource")

    // use "pub build" to build the dart package
    run(
      Process(cmd, dartSource),
      log)

    IO.copyDirectory(dartSource / "build/web", targetFolder, true)
    def recursiveListFiles(f: File): Array[File] = {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
    }
    val copiedFiles = recursiveListFiles(targetFolder)

    log.info(s"Dart pub build files copied to $targetFolder")
    copiedFiles.toSet
  }

  private def run(proc: ProcessBuilder, log: Logger) = {
    val procLog = ProcLogger(log)
    for (line <- proc.lines(procLog)) log.info(line)
  }

  case class ProcLogger(log: Logger) extends ProcessLogger {

    override def info(s: => String): Unit = println(s)

    override def buffer[T](f: => T): T = f

    override def error(s: => String): Unit = log.error(s)
  }

}

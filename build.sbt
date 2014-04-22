import bintray.Keys._

sbtPlugin := true

name := "sbt-dart-pub"

organization := "com.github.tptodorov"

version := "0.0.1"

scalacOptions += "-deprecation"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

crossBuildingSettings

CrossBuilding.crossSbtVersions := Seq("0.11.3", "0.12", "0.13")

CrossBuilding.scriptedSettings

scriptedLaunchOpts <<= (scriptedLaunchOpts, version) { case (s,v) => s ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + v)
}

scriptedBufferLog := false

bintraySettings

packageLabels in bintray := Seq("dart", "compile")

publishMavenStyle := false

repository in bintray := "sbt-plugins"

bintrayOrganization in bintray := None

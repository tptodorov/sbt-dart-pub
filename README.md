sbt-dart-pub
============

SBT plugin for using dart build as part of SBT build process. The result of the dart build is included in the final package as resource files. 

The advantage of this method is that we use all pub configurations and transformations.

### Usage

Add to plugins.sbt:

	addSbtPlugin("com.github.tptodorov" % "sbt-dart-pub" % "0.0.1")


Add to build.sbt:

	sbtdartpub.Plugin.defaultSettings

By default the content of src/main/dart will be built and packaged as resources in /public folder

	package  # find your compiled dart folders in /public

Customizations are self-explanatory:
	
	sbtdartpub.Plugin.Keys.dartSourceFolder := file("src/main/dart")

	sbtdartpub.Plugin.Keys.dartTargetFolder := (resourceManaged in Compile).value / "public"



### Tasks

	pub-build   # to run build in your dart folder

	pub-serve	# to start serving your dart folder (development)


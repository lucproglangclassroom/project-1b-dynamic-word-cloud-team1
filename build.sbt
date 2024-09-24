name := "hello-scalatest-scala"

version := "0.3"

scalaVersion := "3.3.3"

scalacOptions += "@.scalacOptions.txt"

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"  % "3.2.19"  % Test,
  "org.scalacheck" %% "scalacheck" % "1.18.0" % Test,
  "com.lihaoyi" %% "mainargs" % "0.6.3",
  "org.apache.commons" % "commons-collections4" % "4.4"
)
enablePlugins(JavaAppPackaging)

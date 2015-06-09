name := """pubquiz"""

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies" at "http://nightlies.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"         % "2.4-M1",
  "com.typesafe.akka"      %% "akka-slf4j"         % "2.4-M1",
  "com.typesafe.akka"      %% "akka-remote"        % "2.4-M1",
  "com.typesafe.akka"      %% "akka-cluster"       % "2.4-M1",
  "ch.qos.logback"          % "logback-classic"    % "1.0.13",
  "io.spray"               %% "spray-can"          % "1.3.3",
  "io.spray"               %% "spray-routing"      % "1.3.3",
  "io.spray"               %% "spray-json"         % "1.3.2",
  "io.spray"               %% "spray-testkit"      % "1.3.3"     % "test",
  "com.typesafe.akka"      %% "akka-testkit"       % "2.4-M1"    % "test",
  "org.scalatest"          %% "scalatest"          % "2.2.5"      % "test",
  "com.jayway.restassured"  % "rest-assured"       % "2.4.0"          % "test",
  "com.github.dreamhead"    % "moco-core"          % "0.10.0"         % "test"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

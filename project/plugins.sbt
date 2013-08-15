resolvers += "twitter-repo" at "http://maven.twttr.com"

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.1.9")

addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.8.0")

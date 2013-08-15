package com.transcriptic.snowflake

import com.twitter.util.Future
import com.twitter.logging.Logger

import org.eclipse.jetty.server.{Server => JServer}
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object Server {

  val log = Logger()

  def main(args: Array[String]) = {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new JServer(port)
    val context = new WebAppContext()
    context setContextPath("/")
    context.setResourceBase("src/main/webapp")
    context.setInitParameter(ScalatraListener.LifeCycleKey, "com.transcriptic.snowflake.Bootstrap")
    context.addEventListener(new ScalatraListener)
    server.setHandler(context)

    server.start
    log.info("Servers started.")

    server.join
  }

}

package com.transcriptic.snowflake

import scala.concurrent.{Await, Future => AFuture}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.NativeJsonSupport
import org.scalatra.{ScalatraServlet, LifeCycle}
import org.scalatra.auth.strategy.BasicAuthStrategy
import javax.servlet.ServletContext

class Bootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new Controller, "/*")
  }
}

class Controller extends ScalatraServlet with NativeJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  val worker = new Worker(1L)

  before() {
    contentType = formats("json")
  }

  get("/id") {
    Map("success" -> true, "id" -> worker.nextId)
  }

}

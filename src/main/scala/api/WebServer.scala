package api

import actors.OrderRegistryActor
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object WebServer extends App with OrderRoutes {

  implicit val system: ActorSystem = ActorSystem("AkkaHttpServer")
  implicit val materializer = ActorMaterializer()

  val orderRegistryActor: ActorRef = system.actorOf(OrderRegistryActor.props, "orderRegistryActor")

  lazy val routes: Route = serviceRoute

  Http().bindAndHandle(routes, "localhost", 9001)

  println(s"Server online at http://localhost:9001/")

  Await.result(system.whenTerminated, Duration.Inf)
}
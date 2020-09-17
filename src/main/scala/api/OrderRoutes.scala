package api

import tracing.TraceConfig
import tracing.{AkkaHttpTraceDirectives, TraceContext}
import actors.OrderRegistryActor.{ActionExecuted, CancelOrder, GetOrder, GetOrders, SubmitOrder}
import actors.OrderRegistryActor.{Order, Orders}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.event.Logging
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


trait OrderRoutes extends JsonProtocol with AkkaHttpTraceDirectives {

  implicit def system: ActorSystem
  implicit lazy val exec: ExecutionContext = system.dispatcher
  implicit lazy val timeout = Timeout(5.seconds)
  lazy val log = Logging(system, classOf[OrderRoutes])
  lazy val tracer = TraceConfig.getTracer
  def orderRegistryActor: ActorRef

  val getOrdersRoute: Route = withTrace(tracer)("get-orders-request") { span =>
    path("orders") {
      pathEnd {
        get {
          val orders: Future[Orders] =
            (orderRegistryActor ? GetOrders(TraceContext(tracer, span))).mapTo[Orders]
          complete(orders)
        }
      }
    }
  }

  val getOrderRoute: Route = withTrace(tracer)("get-order-request") { span =>
    path("orders" / Segment)  { id =>
        get {
          val getOrder: Future[Option[Order]] =
            (orderRegistryActor ? GetOrder(id.toInt, TraceContext(tracer, span))).mapTo[Option[Order]]

          onComplete(getOrder){
            case util.Success(Some(order)) =>
              complete(order)
            case util.Success(None) =>
              complete((StatusCodes.NotFound, s" ${StatusCodes.NotFound.intValue} - Request not found!"))
            case util.Failure(ex) =>
              complete(HttpResponse(StatusCodes.InternalServerError))
          }
        }
      }
  }


  val submitOrderRoute: Route = withTrace(tracer)("submit-order-request") { span =>
    path("orders") {
      pathEnd {
        post {
          entity(as[Order]) { order =>
            val orderCreated: Future[ActionExecuted] =
              (orderRegistryActor ? SubmitOrder(order, TraceContext(tracer, span))).mapTo[ActionExecuted]

            onSuccess(orderCreated) { executedAction =>
              log.info("Submitted order ID [{}]: {}", order.id, executedAction.description)
              complete((StatusCodes.Created, executedAction.description))
            }
          }
        }
      }
    }
  }

  val deleteOrderRoute: Route =  withTrace(tracer)("cancel-order-request") { span =>
    path("orders" / Segment) { id =>
      delete {
        val cancelledOrder: Future[ActionExecuted] =
          (orderRegistryActor ? CancelOrder(id.toInt,  TraceContext(tracer, span))).mapTo[ActionExecuted]

        onSuccess(cancelledOrder) { actionExecuted =>
          actionExecuted.id match {
            case Some(_) => complete((StatusCodes.OK, actionExecuted.description))
            case None =>  complete((StatusCodes.NotFound, s" ${StatusCodes.NotFound.intValue} - Order not found!"))
          }
        }
      }
    }
  }

  val serviceRoute: Route = getOrdersRoute ~ getOrderRoute ~ submitOrderRoute ~ deleteOrderRoute

}
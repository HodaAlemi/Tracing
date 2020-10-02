package actors

import tracing.CustomTracing.buildChildSpan
import tracing.TraceContext
import akka.actor.{Actor, ActorLogging, Props}
import io.opentracing.{Span, Tracer}


object OrderRegistryActor {

  case class SubmitOrder(order: Order, traceContext: TraceContext)

  case class CancelOrder(id: Int, traceContext: TraceContext)

  case class GetOrder(id: Int, traceContext: TraceContext)

  case class GetOrders(traceContext: TraceContext)

  case class ActionExecuted(id: Option[Int], description: String)

  case class Order(id: Int, items: Seq[Item], totalPrice: Double, date: String)

  case class Orders(orders: Seq[Order])

  case class Item(id: Int, name: String, amount: Int, price: Double)

  def props: Props = Props[OrderRegistryActor]
}

class OrderRegistryActor extends Actor with ActorLogging {

  import OrderRegistryActor._

  var orders = Set.empty[Order]

  def receive: Receive = {

    case GetOrders(traceContext) =>
      val span = buildChildSpan(traceContext, "getOrdersFromDatabase", traceContext.span)
      sender() ! Orders(orders.toSeq)
      span.finish()

    case SubmitOrder(order, traceContext) =>
      val orderSubmissionSpan = buildChildSpan(traceContext, "submittingOrder", traceContext.span)
      orders += order
      sender() ! ActionExecuted(Some(order.id), s"OrderID ${order.id} submitted.")
      val actionExecutionSpan = buildChildSpan(traceContext, "executingAction", orderSubmissionSpan)
      actionExecutionSpan.finish()
      orderSubmissionSpan.finish()


    case GetOrder(id, traceContext) =>
      val span = buildChildSpan(traceContext, "getOrderFromDatabase", traceContext.span)
      sender() ! orders.find(_.id == id)
      span.finish()

    case CancelOrder(id, traceContext) =>
      val span = buildChildSpan(traceContext, "cancelOrderFromDatabase", traceContext.span)
      orders.find(_.id == id) match {
        case Some(order) =>
          val actionExecutionSpan = buildChildSpan(traceContext, "executingAction", span)
          orders -= order
          sender() ! ActionExecuted(Some(order.id), s"OrderID ${order.id} is cancelled.")
          actionExecutionSpan.finish()
        case _ => sender() ! ActionExecuted(None, s"OrderID not found.")
      }
      span.finish()

  }
}

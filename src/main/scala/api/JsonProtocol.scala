package api

import actors.OrderRegistryActor.{Order, Orders, ActionExecuted, Item}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait JsonProtocol extends SprayJsonSupport {

    import spray.json.DefaultJsonProtocol._

    implicit val itemJsonFormat = jsonFormat4(Item)
    implicit val orderJsonFormat = jsonFormat4(Order)
    implicit val ordersJsonFormat = jsonFormat1(Orders)
    implicit val actionExecutedJsonFormat = jsonFormat2(ActionExecuted)
  }
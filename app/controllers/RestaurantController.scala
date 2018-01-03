package controllers

import java.util.UUID
import javax.inject._

import play.api.mvc._
import play.api.libs.json.Json
import models.RestaurantFixtures
import models.{Data, Restaurant}


@Singleton
class RestaurantController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  var myRestaurants = RestaurantFixtures.restaurants

  def index(closed: Option[Int]=Some(1)) = Action {
    val result = closed match {
      case Some(0) => myRestaurants.filterNot(r => r.data.closed.getOrElse(false))
      case _ => myRestaurants
    }
    Ok(Json.toJson(result))
  }

  def create = Action { request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[Data] match {
          case dataVald if dataVald.isSuccess => {
            val restaurant = Restaurant(UUID.randomUUID().toString, dataVald.get)
            myRestaurants = myRestaurants :+ restaurant
            Created(Json.toJson(restaurant))
          }
          case _ => BadRequest("Invalid restaurant")
        }
      case _ => BadRequest("Invalid request format")
    }
  }

  def update(uuid: String) = Action { request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[Data] match {
          case dataVald if dataVald.isSuccess =>
            myRestaurants.find {rest =>
              rest.uuid == uuid
            } match {
              case Some(rest) =>
                val updatedRest = rest.copy(data = dataVald.get)
                myRestaurants = myRestaurants.map { rest =>
                  if (rest.uuid == uuid) updatedRest else rest
                }
                Ok(Json.toJson(updatedRest))
              case _ => NotFound("Restaurant not found with uuid: " + uuid)
            }
          case _ => BadRequest("Invalid restaurant")
        }
      case _ => BadRequest("Invalid request format")
    }
  }
}

package controllers

import models.{Restaurant, RestaurantFixtures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._


class RestaurantControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  val restaurantFixtures = RestaurantFixtures.restaurants
  val aRestaurantData = restaurantFixtures.head.data

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 200 on a good request" in  {
      route(app, FakeRequest(GET, "/api/restaurant")).map(status(_)) mustBe Some(OK)
    }

  }

  "RestaurantController" should {

    "create new restaurant" in {
      val fakeRequest = FakeRequest(POST, "/api/restaurant") withJsonBody Json.toJson(aRestaurantData)
      val create = route(app, fakeRequest).get
      val createdRestaurant = contentAsJson(create).as[Restaurant]
      val restaurantsList = contentAsJson(route(app, FakeRequest(GET, "/api/restaurant")).get).as[List[Restaurant]]

      status(create) mustBe Status.CREATED
      contentType(create) mustBe Some("application/json")
      createdRestaurant.data mustEqual aRestaurantData
      restaurantsList.contains(createdRestaurant) mustBe true
    }

    "update existing restaurant" in {
      val existingRestaurant = contentAsJson(
        route(app, FakeRequest(POST, "/api/restaurant")
          withJsonBody Json.toJson(aRestaurantData)).get
      ).as[Restaurant]
      val newData = existingRestaurant.data.copy(enName = Some("New Test Restaurant 1337"))
      val update = route(
        app, FakeRequest(PUT, s"/api/restaurant/${existingRestaurant.uuid}")
          withJsonBody Json.toJson(newData)
      ).get
      val updatedRestaurant = contentAsJson(update).as[Restaurant]
      val restaurantsList = contentAsJson(route(app, FakeRequest(GET, "/api/restaurant")).get).as[List[Restaurant]]

      status(update) mustBe Status.OK
      contentType(update) mustBe Some("application/json")
      updatedRestaurant.data mustEqual newData
      restaurantsList.contains(updatedRestaurant) mustBe true

    }

    "index restaurants" in {
      route(app, FakeRequest(POST, "/api/restaurant") withJsonBody Json.toJson(aRestaurantData))
      val index = route(app, FakeRequest(GET, "/api/restaurant")).get

      status(index) mustBe Status.OK
      contentType(index) mustBe Some("application/json")
      contentAsJson(index).as[List[Restaurant]].nonEmpty mustBe true
    }

    "index and filter closed restaurants" in {
      route(app, FakeRequest(POST, "/api/restaurant") withJsonBody Json.toJson(aRestaurantData.copy(closed = Some(true))))
      val index = route(app, FakeRequest(GET, "/api/restaurant?closed=0")).get
      val restaurantsList = contentAsJson(index).as[List[Restaurant]]

      status(index) mustBe Status.OK
      restaurantsList.exists(r => r.data.closed.get) mustBe false
    }

  }

}

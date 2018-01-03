package models

import play.api.libs.json.Json


final case class Data(
  enName: Option[String],
  arName: Option[String],
  state: Option[String] = Some("UNPUBLISHED"),
  routingMethod: Option[String],
  logo: Option[String],
  coverPhoto: Option[String],
  enDescription: Option[String],
  arDescription: Option[String],
  shortNumber: Option[String],
  facebookLink: Option[String],
  twitterLink: Option[String],
  youtubeLink: Option[String],
  website: Option[String],
  onlinePayment: Option[Boolean] = Some(false),
  client: Option[Boolean],
  pendingInfo: Option[Boolean],
  pendingMenu: Option[Boolean],
  closed: Option[Boolean] = Some(false)
)

object Data {
  implicit val format = Json.format[Data]
}

final case class Restaurant(uuid: String, data: Data)

object Restaurant {
  implicit val format = Json.format[Restaurant]
}

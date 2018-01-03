package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class MainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def untrail(path: String) = Action {
//    Matches any path that has no matching route and ends with trailing slash '/'
//    redirects to the requested path without the trailing slash
    MovedPermanently("/" + path)
  }
}

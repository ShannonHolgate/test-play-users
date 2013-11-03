package controllers

import play.api._
import play.api.mvc._

import views._
import models._
import org.joda.time.{Days, Interval, DateTime}

object Application extends Controller with Secured {

  def index = withUser { user => implicit request => {
      val username = user.username
      Ok(views.html.index(username))
    }
  }
}
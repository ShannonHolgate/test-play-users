package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import org.joda.time.{Interval, Days, DateTime}


/**
 * Created with IntelliJ IDEA.
 * User: Shannon
 * Date: 02/11/13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
object Login extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  def index = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession(Security.username -> user._1, "connected" -> DateTime.now.toString())
    )
  }

  def logout = Action {
    Redirect(routes.Login.index).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}

trait Secured extends Controller{

  def email(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Login.index)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(email, onUnauthorized) { user =>
      Action(request => {
        if (hasTimedOut(request)) {
          Redirect(routes.Login.index).withNewSession.flashing("Timeout" -> "You have been inactive for over 5 days")
        }
        else f(user)(request)
      })
    }
  }

  def withUser(f: User => Request[AnyContent] => Result) = withAuth { email => implicit request =>
    User.findByEmail(email).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }

  def hasTimedOut (request:Request[AnyContent]):Boolean= {
    request.session.get("connected").map ({
      connected =>
        //DateTime.now().getMillisOfDay.-(DateTime.parse(connected).getMillisOfDay).>(500)
        Days.daysBetween(DateTime.parse(connected).toDateMidnight() , DateTime.now().toDateMidnight()).getDays().>(5)
    }).getOrElse(true)
  }
}
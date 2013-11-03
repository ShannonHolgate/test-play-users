package controllers

import play.api.mvc.{Action, Security, Controller}
import play.api.data._
import play.api.data.Forms._
import models.User
import views.html
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: Shannon
 * Date: 03/11/13
 * Time: 21:56
 * To change this template use File | Settings | File Templates.
 */
object Register extends Controller{

  val registerForm = Form(
    tuple(
      "username" -> text,
      "email" -> text,
      "password" -> text
    ) verifying ("User Already exists", result => result match {
      case (username,email,password) => User.findByEmail(email).isEmpty
    })
  )

  def index = Action { implicit request =>
    Ok(html.register(registerForm)).withNewSession
  }

  def register = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.register(formWithErrors)),
      user => {
        User.create(user._1,user._2,user._3)
        Redirect(routes.Application.index).withSession(Security.username -> user._2, "connected" -> DateTime.now.toString())
      }
    )
  }
}

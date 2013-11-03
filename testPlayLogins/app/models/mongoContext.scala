package models

import com.novus.salat._
import play.api.Play.current
import play.api.Play

/**
 * Created with IntelliJ IDEA.
 * User: Shannon
 * Date: 02/11/13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */

package object mongoContext {
  implicit val context = {
    val context = new Context {
      val name = "global"
      override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_t")
    }
    context.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")
    context.registerClassLoader(Play.classloader)
    context
  }
}
package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.Evaluator

object PlayJson{
  import play.api.libs.json._
  val dialect = new Dialect{
    type Value = JsValue
    def scalar(v: Any) = v match {
      case null => JsNull
      case v:Int => JsNumber(v)
      case v:Long => JsNumber(v)
      case v: Double => JsNumber(v)
      case v: Float => JsNumber(v)
      case v: String => JsString(v)
      case v: Boolean => JsBoolean(v)
      //case v: org.joda.time.DateTime
    }
    def object_(values: (String, JsValue)*) = JsObject(values)
    def array(values: JsValue*) = JsArray(values)
  }
  implicit def embedJson(value: JsObject) = EmbeddedJson(value)
  implicit def intNumber(i: Int) = JsNumber(i)
  implicit def longNumber(i: Long) = JsNumber(i)
  implicit def floatNumber(i: Float) = JsNumber(i)
  implicit def doubleNumber(i: Double) = JsNumber(i)
  val evaluator = new Evaluator(dialect)
  implicit class ExpressionExtensions(val ex: Expression) extends AnyVal{
    def toJson = evaluator.toJson(ex)
    def string = Json.stringify(ex.toJson)
    def prettyString = Json.prettyPrint(ex.toJson)
  }  
}

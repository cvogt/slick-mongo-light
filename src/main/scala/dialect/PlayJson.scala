package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.Evaluator

object PlayJson{
  import play.api.libs.json._
  val dialect = new Dialect{
    type Value = JsValue
    def null_ = JsNull
    def int(i: Int) = JsNumber(i)
    def long(i: Long) = JsNumber(i)
    def float(i: Float) = JsNumber(i)
    def double(i: Double) = JsNumber(i)
    def boolean(a: Boolean) = JsBoolean(a)
    def string(a: scala.Predef.String) = JsString(a)
    def object_(values: (String, JsValue)*) = JsObject(values)
    def array(values: JsValue*) = JsArray(values)
  }
  implicit def embedJson(value: dialect.Value) = EmbeddedJson(value)
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

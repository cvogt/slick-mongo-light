package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions

import com.mongodb.casbah.Imports._

import org.cvogt.slick_mongo_light.expressions._

object Casbah{
  import play.api.libs.json._
  val dialect = new Dialect{
    type Value = Any
    def null_ = null
    def int(i: Int) = i
    def long(i: Long) = i
    def float(i: Float) = i
    def double(i: Double) = i
    def boolean(a: Boolean) = a
    def string(a: String) = a
    def object_(values: (String, Value)*) = MongoDBObject(values :_*)
    def array(values: Value*) = MongoDBList(values)
  }
  implicit def embedJson(value: dialect.Value) = EmbeddedJson(value)
  val evaluator = new Evaluator(dialect)
  implicit def implicitlyToJson(ex: Expression) = ex.toJson.asInstanceOf[DBObject]
  implicit class ExpressionExtensions(val ex: Expression) extends AnyVal{
    def toJson = evaluator.toJson(ex)
    //def string = Json.stringify(ex.toJson)
    //def prettyString = Json.prettyPrint(ex.toJson)
  }  
}

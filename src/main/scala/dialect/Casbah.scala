package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions

import com.mongodb.casbah.Imports._

import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.Evaluator

object Casbah{
  import play.api.libs.json._
  val dialect = new Dialect{
    type Value = Any
    def scalar(v: Any) = v match {
      case null => null
      case _:Int | _:Long => v
      case v: Double => v
      case v: Float => v
      case v: String => v
      case v: Boolean => v
      case d: org.joda.time.DateTime => d.toDate
      case l: org.joda.time.LocalDateTime => l.toDateTime.toDate
    }
    def object_(values: (String, Value)*) = MongoDBObject(values :_*)
    def array(values: Value*) = MongoDBList(values)
  }
  implicit def embedJson(value: MongoDBObject) = EmbeddedJson(value)
  val evaluator = new Evaluator(dialect)
  implicit def implicitlyToJson(ex: Expression) = ex.toJson.asInstanceOf[DBObject]
  implicit class ExpressionExtensions(val ex: Expression) extends AnyVal{
    def toJson = evaluator.toJson(ex)
    //def string = Json.stringify(ex.toJson)
    //def prettyString = Json.prettyPrint(ex.toJson)
  }  
}

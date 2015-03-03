package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.Evaluator

object ReactiveMongo{
  import reactivemongo.bson._
  val dialect = new Dialect{
    type Value = BSONValue
    def scalar(v: Any) = v match {
      case null => BSONNull
      case v: Int => BSONInteger(v)
      case v: Long => BSONLong(v)
      case v: Double => BSONDouble(v)
      case v: Float => BSONDouble(v)
      case v: String => BSONString(v)
      case v: Boolean => BSONBoolean(v)
      //case v: org.joda.time.DateTime
    }
    def object_(values: (String, BSONValue)*) = BSONDocument(values)
    def array(values: BSONValue*) = BSONArray(values)
  }
  implicit def embedJson(value: BSONDocument) = EmbeddedJson(value)
  implicit def int(i: Int) = BSONInteger(i)
  implicit def long(i: Long) = BSONLong(i)
  implicit def float(i: Float) = BSONDouble(i)
  implicit def double(i: Double) = BSONDouble(i)
  val evaluator = new Evaluator(dialect)
  implicit class ExpressionExtensions(val ex: Expression) extends AnyVal{
    def toJson = evaluator.toJson(ex)
    //def string = Json.stringify(ex.toJson)
    //def prettyString = Json.prettyPrint(ex.toJson)
  }  
}

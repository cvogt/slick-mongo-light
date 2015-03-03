package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.Evaluator

object ReactiveMongo{
  import reactivemongo.bson._
  val dialect = new Dialect{
    type Value = BSONValue
    def null_ = BSONNull
    def int(i: Int) = BSONInteger(i)
    def long(i: Long) = BSONLong(i)
    def float(i: Float) = BSONDouble(i)
    def double(i: Double) = BSONDouble(i)
    def boolean(a: Boolean) = BSONBoolean(a)
    def string(a: scala.Predef.String) = BSONString(a)
    def object_(values: (String, BSONValue)*) = BSONDocument(values)
    def array(values: BSONValue*) = BSONArray(values)
  }
  implicit def embedJson(value: dialect.Value) = EmbeddedJson(value)
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

package org.cvogt.slick_mongo_light.dialect
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._

trait Dialect{
  type Value
  def null_ : Value

  def int(i: Int): Value
  def long(i: Long): Value
  def float(i: Float): Value
  def double(i: Double): Value

  def boolean(a: Boolean): Value

  def string(a: scala.Predef.String): Value

  def object_(values: (String, Value)*): Value

  def array(values: Value*): Value
}

class Evaluator[D <: Dialect](val js: D){
  evaluator =>
  def splitFields(field: String) = field.split("\\.").reverse.toList
  def nestFields(field: String)(applyLast: String => js.Value) = {
    val fields = splitFields(field)
    applyNestedFields(
      fields.tail,
      applyLast(fields.head)
    )
  }
  def applyNestedFields(fields: List[String], ex: js.Value): js.Value
    = fields match {
        case Nil => ex
        case f :: tail => applyNestedFields( tail, js.object_( f -> ex ) )
      }

  private def anyToJson(v: Any): js.Value = v match {
    case v: Expression => toJson(v)
    case null => js.null_
    case v: Int => js.int(v)
    case v: Long => js.long(v)
    case v: Double => js.double(v)
    case v: Float => js.float(v)
    case v: String => js.string(v)
  }

  class ExpressionExtensions(val ex: Expression){
    def toJson: js.Value = evaluator.toJson(ex).asInstanceOf[js.Value]
  }

  def toJson(ex: Expression): js.Value = {
    implicit def ExpressionExtensions(ex: Expression) = new ExpressionExtensions(ex)
    ex match{
      case Constant(c) => anyToJson(c)
      case EmbeddedJson(json) => json.asInstanceOf[js.Value]
      case Sequence(s: Seq[_]) => js.array(s.map(anyToJson) :_*)
      case Object(pairs@_*) => js.object_(pairs.toMap.mapValues(anyToJson).toSeq :_*)
      case With(Field(field), ex) => applyNestedFields( splitFields(field), ex.toJson )
      case LogicalOperator(_,op,left,right) =>
        js.object_( op -> js.array( left.toJson, right.toJson ) )
      case ComparisonOperator("===",_,Field(field),right) =>
        nestFields(field){
          f => js.object_( f -> right.toJson )
        }
      case ComparisonOperator(_,op,Field(field),right) =>
        nestFields(field){
          f => js.object_( f -> js.object_( op -> right.toJson ) )
        }
      case ComparisonOperator(sop,op,right,Field(field)) => ComparisonOperator(sop,op,Field(field),right).toJson
      case ComparisonOperator(scalaName,_,_,_) =>
        throw new Exception(scalaName+" needs mongo field on either side (m\"...\")")
      case Field(field) => js.object_( field -> js.boolean(true) )
    }
  }
}

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

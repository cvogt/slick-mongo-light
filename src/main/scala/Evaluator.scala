package org.cvogt.slick_mongo_light
import scala.language.implicitConversions
import org.cvogt.slick_mongo_light.expressions._
import org.cvogt.slick_mongo_light.dialect.Dialect

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

  class ExpressionExtensions(val ex: Expression){
    def toJson: js.Value = evaluator.toJson(ex).asInstanceOf[js.Value]
  }

  def anyToJson(any: Any) = any match{
    case v: Expression => toJson(v)
    case other => js.scalar(other)
  }

  def toJson(ex: Expression): js.Value = {
    implicit def ExpressionExtensions(ex: Expression) = new ExpressionExtensions(ex)
    ex match{
      case Scalar(c) => try{
        js.scalar(c)
      } catch {
        case e:MatchError => throw new Exception("Type not supported by chosen dialect: "+c.getClass.getName,e)
      }
      case EmbeddedJson(json) => json.asInstanceOf[js.Value]
      case Sequence(s: Seq[_]) => js.array(s.map(anyToJson) :_*)
      case Object(pairs@_*) => js.object_(pairs.toMap.mapValues(anyToJson).toSeq :_*)
      case With(Field(field), ex) => applyNestedFields( splitFields(field), ex.toJson )
      //case UnaryOperator(_,op,expr) => js.object_( op -> expr )
      case PrefixOperator(_,op,left,right) =>
        js.object_( op -> js.array( left.toJson, right.toJson ) )
      case InfixOperator(_,op,Field(field),right) =>
        nestFields(field){
          f => js.object_( f -> js.object_( op -> right.toJson ) )
        }
      case InfixOperator(sop,op,right,Field(field)) =>
        InfixOperator(sop,op,Field(field),right).toJson
      case InfixOperator(scalaName,_,_,_) =>
        throw new Exception("Can't use "+scalaName+""" on two m"..."-fields. One needs to be a constant.""")
      case Field(field) => js.object_( field -> js.scalar(true) )
    }
  }
}

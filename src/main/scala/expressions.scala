package org.cvogt.slick_mongo_light.expressions

sealed trait Expression
case class Field(name: String) extends Expression
case class Constant[T](value: T) extends Expression
case class Sequence[T](value: Seq[T]) extends Expression
case class Object(pairs: (String, Expression)*) extends Expression
case class ComparisonOperator(scalaName: String, mongoName: String, left: Expression, right: Expression) extends Expression
case class LogicalOperator(scalaName: String, mongoName: String, left: Expression, right: Expression) extends Expression
case class EmbeddedJson[T](json: T) extends Expression
case class With(field: Field, ex: Expression) extends Expression

sealed abstract class Type(number: Int)
object Type{
  case object Double extends Type(1)
  case object String extends Type(2)
  case object Object extends Type(3)
  case object Array extends Type(4)
  case object BinaryData extends Type(5)
  @deprecated("See http://docs.mongodb.org/manual/reference/operator/query/type/","0.1")
  case object Undefined extends Type(6)
  case object ObjectId extends Type(7)
  case object Boolean extends Type(8)
  case object Date extends Type(9)
  case object Null extends Type(10)
  case object RegularExpression extends Type(11)
  case object JavaScript extends Type(13)
  case object Symbol extends Type(14)
  case object JavaScriptWithScope extends Type(15)
  case object Integer32Bit extends Type(16)
  case object Timestamp extends Type(17)
  case object Integer64Bit extends Type(18)
  case object MinKey extends Type(-1) // Mongo says to use -1 for queries, despite 255 being the number
  case object MaxKey extends Type(127)
}

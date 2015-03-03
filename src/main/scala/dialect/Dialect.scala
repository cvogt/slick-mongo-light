package org.cvogt.slick_mongo_light.dialect
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

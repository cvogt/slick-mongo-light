package org.cvogt.slick_mongo_light.dialect
trait Dialect{
  type Value
  def scalar(v: Any): Value
  def object_(values: (String, Value)*): Value
  def array(values: Value*): Value
}

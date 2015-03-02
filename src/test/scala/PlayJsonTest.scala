package org.cvogt.test.slick_mongo_light

import org.scalatest.FunSuite
import org.scalactic.TypeCheckedTripleEquals._

import org.cvogt.slick_mongo_light.Implicits._
import org.cvogt.slick_mongo_light.dialect.PlayJson._

import play.api.libs.json._

class PlayJsonTest extends FunSuite{
  import org.cvogt.slick_mongo_light.dialect.PlayJson.{dialect => js}
  test("basic"){
    val cases = Seq[(Expression,js.Value)](
      (m"foo" > 5) ->
        js.object_("foo" -> js.object_("$gt" -> 5)),
      (m"foo.bar" > 5) ->
        js.object_("foo" -> js.object_("bar" -> js.object_("$gt" -> 5))),
      (m"foo" -> {m"bar" > 5 && m"baz" === 5}) ->
        js.object_("foo" -> js.object_("$and" -> js.array(
          js.object_("bar" -> js.object_("$gt" -> 5)),
          js.object_("baz" -> 5)
        )))
    )
    cases.foreach{
      c => assert(c._1.toJson === c._2, "failed for: "+c.toString)
    }
  }
}

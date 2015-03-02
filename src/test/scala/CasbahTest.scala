package org.cvogt.test.slick_mongo_light

import org.scalatest.FunSuite
import org.scalactic.TypeCheckedTripleEquals._

import org.cvogt.slick_mongo_light.Implicits._
import org.cvogt.slick_mongo_light.dialect.Casbah._

import com.mongodb.casbah.Imports._
import scala.collection.JavaConversions._
import org.cvogt.slick_mongo_light.dialect.Casbah.{dialect => js}
      
class CasbahTest extends FunSuite{
  val port = 27018
  def client = MongoClient("localhost", port)
  test("connection"){
    EmbeddedMongo.use(port){
      val mongo = client
      val db = mongo.getDB("test")
      val col = db.createCollection("testCol", new BasicDBObject())
      col.save(new BasicDBObject("testDoc", 1))
      val res1 = col.find().iterator.toList.map(_.toList.toMap)
      assert(res1.forall(_.contains("testDoc" -> 1)))
      val res2 = col.find(m"testDoc" === 1).iterator.toList.map(_.toList.toMap)
      assert(res1.forall(_.contains("testDoc" -> 1)))

      try{
      // compare a top-level field
      col.find(m"someField" === 1)

      // compare a nested field
      col.find(m"someField.nestedField" === 1)

      // compare multiple fields
      col.find(m"someField" -> {
        m"nestedField1" === 1 && m"nestedField2" === "foo"
      })

      // compare multiple fields
      col.find(m"someField" -> {
        m"nestedField1" === 1 && MongoDBObject("nestedField2" -> MongoDBObject("$eq" -> "foo"))
      })

      // check existence in a list
      col.find(m"someField" in List(1,2,3))
      } catch {case e:Exception => }
    }
  }
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

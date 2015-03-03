package org.cvogt.test.slick_mongo_light

import org.scalatest.FunSuite
import org.scalactic.TypeCheckedTripleEquals._

import org.cvogt.slick_mongo_light.Implicits._
import org.cvogt.slick_mongo_light.dialect.Casbah._

import com.mongodb.casbah.Imports._
import scala.collection.JavaConversions._
import org.cvogt.slick_mongo_light.dialect.Casbah.{dialect => js}
import org.cvogt.slick_mongo_light.dialect.Casbah.embedJson
      
class CasbahTest extends FunSuite{
  val port = 27018
  test("connection"){
    EmbeddedMongo.use{ port =>
      val client = MongoClient("localhost", port)
      val mongo = client
      val db = mongo.getDB("test")
      val col = db.createCollection("testCol", new BasicDBObject())
      col.save(new BasicDBObject("testDoc", 1))
      val res1 = col.find().iterator.toList.map(_.toList.toMap)
      assert(res1.forall(_.contains("testDoc" -> 1)))
      val res2 = col.find(m"testDoc" === 1).iterator.toList.map(_.toList.toMap)
      assert(res2.forall(_.contains("testDoc" -> 1)))
      val res3 = col.find(m"testDoc".exists).iterator.toList.map(_.toList.toMap)
      assert(res3.forall(_.contains("testDoc" -> 1)))
      val res4 = col.find(m"foo".exists).iterator.toList.map(_.toList.toMap)
      assert(res4.isEmpty)
    }
    EmbeddedMongo.use{ port =>
      val client = MongoClient("localhost", port)
      val mongo = client
      val db = mongo.getDB("test")
      val col = db.createCollection("testCol", new BasicDBObject())
      col.save(
        new BasicDBObject("someField",1)
      )
      col.save(
        new BasicDBObject("someField",2)
      )

      // compare a top-level field
      val res = col.find(m"someField" === 1)

      // compare a nested field
      val a = col.find(m"someField.nestedField" === 1)

      // compare multiple nested fields
      val b = col.find(m"someField" -> {
        m"nestedField1" === 1 && m"nestedField2" === "foo"
      })

      // embed ordinary mongo queries
      val c = col.find(m"someField" -> {
        m"nestedField1" === 1 && embedJson(MongoDBObject("nestedField2" -> MongoDBObject("$eq" -> "foo")))
      })

      val d = col.find(MongoDBObject("nestedField2" -> MongoDBObject("$eqqwewqe" -> "foo")))

      // check existence in a list
      val e = col.find(m"someField" in List(1,2,3))

      // more examples
      val f = col.find(m"someField".exists)

      import org.cvogt.slick_mongo_light.expressions.Type
      val g = col.find(m"someField".isOfType(Type.Boolean))

      // compare a nested field
      val h = col.find(m"someField.nestedField" > new org.joda.time.DateTime)
    }
  }
}

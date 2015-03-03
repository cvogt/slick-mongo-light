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
      assert(res2.forall(_.contains("testDoc" -> 1)))
      val res3 = col.find(m"testDoc".exists).iterator.toList.map(_.toList.toMap)
      assert(res3.forall(_.contains("testDoc" -> 1)))
      val res4 = col.find(m"foo".exists).iterator.toList.map(_.toList.toMap)
      assert(res4.isEmpty)

      try{

        // compare a top-level field
        col.find(m"someField" === 1)

        // compare a nested field
        col.find(m"someField.nestedField" === 1)

        // compare multiple nested fields
        col.find(m"someField" -> {
          m"nestedField1" === 1 && m"nestedField2" === "foo"
        })

        // embed ordinary mongo queries
        col.find(m"someField" -> {
          m"nestedField1" === 1 && MongoDBObject("nestedField2" -> MongoDBObject("$eq" -> "foo"))
        })

        // check existence in a list
        col.find(m"someField" in List(1,2,3))

        // more examples
        col.find(m"someField".exists)

        import org.cvogt.slick_mongo_light.expressions.Type
        col.find(m"someField".isOfType(Type.Boolean))


      } catch {case e:Exception => } // <- just want to check compilation here, not exceptions
    }
  }
}

package org.cvogt.test.slick_mongo_light

import org.scalatest.FunSuite
import org.scalactic.TypeCheckedTripleEquals._

import org.cvogt.slick_mongo_light.Implicits._
import org.cvogt.slick_mongo_light.dialect.ReactiveMongo._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent._
import scala.concurrent.duration.Duration

import reactivemongo.api._
import reactivemongo.bson._

class ReactiveMongoTest extends FunSuite{
  val driver = new MongoDriver
  
  test("connection"){
    Await.result(EmbeddedMongo.future{ port =>
      val connection = driver.connection(List("localhost:"+port))
      val db = connection.db("somedatabase")
      val col = db.collection("somecollection")

      (for{
        _ <- col.insert(BSONDocument("testDoc" -> 5))
        res1 <- col.find(BSONDocument()).cursor[BSONDocument].collect[List]()
        res1 <- col.find((m"testDoc" === 1).toJson.asInstanceOf[BSONDocument]).cursor[BSONDocument].collect[List]()
      } yield {
        import scala.language.reflectiveCalls
        val m1 = res1.map(_.elements.toMap.mapValues{case t:{def value:Any} => t.value}.toList)
        assert(m1.forall(_.contains("testDoc" -> 1)))
        val m2 = res1.map(_.elements.toMap.mapValues{case t:{def value:Any} => t.value}.toList)
        assert(m2.forall(_.contains("testDoc" -> 1)))
      }
    )}, Duration.Inf)
  }
  import org.cvogt.slick_mongo_light.dialect.ReactiveMongo.{dialect => js}
}

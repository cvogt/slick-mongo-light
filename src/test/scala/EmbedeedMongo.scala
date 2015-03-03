package org.cvogt.test.slick_mongo_light

import de.flapdoodle.embed.mongo._
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution._
import de.flapdoodle.embed.process.config.io._
import de.flapdoodle.embed.process.runtime._
import java.util.logging.Logger
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object EmbeddedMongo{
  private def config(port: Int) = new MongodConfigBuilder()
        .version(Version.Main.PRODUCTION)
        .net(new Net(port, false))
        .build();

  private val logger = Logger.getLogger(getClass().getName());
  private val runtimeConfig = new RuntimeConfigBuilder()
      .defaultsWithLogger(Command.MongoD, logger)
      .processOutput(ProcessOutput.getDefaultInstanceSilent())
      .build();

  def use(f: Int => Unit): Unit = Await.result(future(port => Future(f(port))), Duration.Inf)

  def future(f: Int => Future[_]): Future[Unit] = {
    val port = Network.getFreeServerPort()
    val dbex = 
      MongodStarter
        .getInstance(runtimeConfig)
        .prepare(config(port))
    val db = dbex.start
    val res = f(port)
    res.onComplete{
      _ =>
      db.stop
      dbex.stop      
    }
    res.map(_ => ())
  }
}

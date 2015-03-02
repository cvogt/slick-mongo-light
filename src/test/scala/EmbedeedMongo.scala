package org.cvogt.test.slick_mongo_light

import de.flapdoodle.embed.mongo._
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution._
import de.flapdoodle.embed.process.config.io._
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

  def use(port: Int)(f: => Unit): Unit = Await.result(future(port)(Future(f)), Duration.Inf)

  def future(port: Int)(f: => Future[_]): Future[Unit] = {
    val dbex = 
      MongodStarter
        .getInstance(runtimeConfig)
        .prepare(config(port))
    for{
      db <- Future(dbex.start)
      _ <- Future(f.recover{ case _ => ()}) // to swallow exceptions
      _ <- Future(db.stop)
      _ <- Future(dbex.stop)
    } yield()
  }
}

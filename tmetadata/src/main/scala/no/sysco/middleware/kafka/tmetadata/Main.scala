package no.sysco.middleware.kafka.tmetadata

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import no.sysco.middleware.kafka.tmetadata.application.{KafkaTopicsMetadataService, KafkaTopicsObserverActor}
import no.sysco.middleware.kafka.tmetadata.rest.HttpRoutes

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


object Main extends App with HttpRoutes {

  implicit val system = ActorSystem("akka-reactive-kafka-app")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config = Config.loadConfig()
//  val log = Logging(system, this.getClass.getName)

  val service = new KafkaTopicsMetadataService(config)
  override def kafkaService = service

  // Observer actor
  val observer = system.actorOf(KafkaTopicsObserverActor.props(config, service), "kafka-topics-observer")

  //Start the akka-http server and listen for http requests
  val akkaHttpServer = startAkkaHTTPServer(config.rest.host, config.rest.port)

  service.startStreams()


  private def startAkkaHTTPServer(host: String, port: Int): Future[ServerBinding] = {
    println(s"Waiting for http requests at http://$host:$port/")
    Http().bindAndHandle(routes, host, port)
  }

  private def shutdownApplication(): Unit = {
    scala.sys.addShutdownHook({
      println("Terminating the Application...")
      akkaHttpServer.flatMap(_.unbind())
      system.terminate()
      Await.result(system.whenTerminated, 30 seconds)
      println("Application Terminated")
    })
  }


}

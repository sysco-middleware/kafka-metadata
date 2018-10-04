package no.sysco.middleware.metadata.api.infrastructure

import java.util.concurrent.CountDownLatch

import org.apache.kafka.streams.KafkaStreams

object TMetadataKafkaAdministrator {

  def addShutdownHook(streams: KafkaStreams, latch: CountDownLatch, appName: String): Unit = {
    // attach shutdown handler to catch control-c
    Runtime.getRuntime.addShutdownHook(new Thread(s"$appName-shutdown-hook") {
      override def run(): Unit = {
        streams.close()
        latch.countDown()
      }
    })

    try {
      streams.start()
      latch.await()
    } catch {
      case e: Throwable =>
        System.exit(1)
    }
    System.exit(0)
  }
}

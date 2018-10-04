package no.sysco.middleware.kafka.metadata.topic.utils

import no.sysco.middleware.kafka.metadata.topic.rest.{ Team, TopicMetadata, TopicVendor }

import scala.io.Source

object Utils {

  def jsonFromFile(filename: String): String = Source.fromFile(filename).mkString
  def metadataPojo(withSla: Boolean): TopicMetadata = {
    TopicMetadata(
      "some name",
      "some description",
      List(
        Team("team name1", "department name1"),
        Team("team name2", "department name2")),
      TopicVendor("STATTNETT"),
      "XML",
      "some scope",
      "some config",
      if (withSla) {
        Option("some sla")
      } else {
        Option.empty
      })
  }

}

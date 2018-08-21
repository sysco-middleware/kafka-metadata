package no.sysco.middleware.kafka.tmetadata.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

//* description
//* producers (teams)
//* format (e.g. avro, xml, json)
//* version
//* scope (public,private)
//* configs (retention, etc)
//* SLAs

// Validation http://fruzenshtein.com/akka-http-model-validation-field-by-field/
final case class TopicMetadata(
                                topicName: String,
                                description: String,
                                producers: List[Team],
                                topicVendor: TopicVendor,
                                format: String,
                                scope: String,
                                config: String,
                                sla: Option[String]) {
  require(!topicName.isEmpty, "topic_name must not be empty")

  override def toString: String = TopicVendorProtocol.json(this)
}

final case class Team(name: String, department: String) {
  require(!name.isEmpty, "team.name must not be empty")
  require(!department.isEmpty, "team.departament must not be empty")
}

final case class TopicVendor(companyName: String) {
  require(!companyName.isEmpty, "company_name must not be empty")
}


trait TopicVendorProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit def topicMetadataJsonFormat: RootJsonFormat[TopicMetadata] =
    jsonFormat(
      TopicMetadata,
      "topic_name",
      "description",
      "producers",
      "topic_vendor",
      "format",
      "scope",
      "config",
      "sla")
  implicit def teamJsonFormat: RootJsonFormat[Team] = jsonFormat(Team, "name", "department")
  implicit def topicVendorJsonFormat: RootJsonFormat[TopicVendor] = jsonFormat(TopicVendor, "company_name")


}

object TopicVendorProtocol extends TopicVendorProtocol {
  import spray.json._

  private[rest] def json(topicMetadata: TopicMetadata): String = topicMetadata.toJson.toString
}
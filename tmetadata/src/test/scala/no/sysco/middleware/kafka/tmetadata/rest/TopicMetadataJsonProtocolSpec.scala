package no.sysco.middleware.kafka.tmetadata.rest

import no.sysco.middleware.kafka.tmetadata.utils.Utils
import spray.json.{DefaultJsonProtocol, DeserializationException, JsonParser}
import org.scalatest._
import org.scalatest.{Matchers, WordSpec}


class TopicMetadataJsonProtocolSpec extends WordSpec with Matchers with TopicMetadataJsonProtocol {

  import spray.json._


  "TopicVendorProtocol in use, " when {

    "json => pojo. " should {

      "Team return proper json " in {
        val json = Utils.jsonFromFile("tmetadata/src/test/resources/team.json")
        val parsedJson = JsonParser(json).convertTo[Team]
        parsedJson shouldBe a [Team]
        parsedJson should not be null
      }

      "Team parsing failed, no department properties" in {
        val json =
          """
            | {
            |   "name": "yes"
            | }
          """.stripMargin
        val thrown = intercept[DeserializationException] {JsonParser(json).convertTo[Team]}
        assert(thrown.getMessage === "Object is missing required member 'department'")
      }

      "TopicMetadata return proper pojo " in {
        val json = Utils.jsonFromFile("tmetadata/src/test/resources/topic-metadata.json")
        val parsedJson = JsonParser(json).convertTo[TopicMetadata]

        parsedJson shouldBe a [TopicMetadata]
        parsedJson should not be null
      }
    }

    "pojo => json. " should {

      "TopicMetadata return proper json " in {
        val pojo = Utils.metadataPojo(true)
        val json = pojo.toJson.toString
        json should not be null
        assert(json.contains("STATTNETT"))
      }

    }

  }

}

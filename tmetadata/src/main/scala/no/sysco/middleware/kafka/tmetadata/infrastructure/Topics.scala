package no.sysco.middleware.kafka.tmetadata.infrastructure

object Topics {
  val METADATA: String = "topics_metadata"
  val METADATA_STORAGE: String = s"${Topics.METADATA}_storage"
}

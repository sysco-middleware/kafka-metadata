package no.sysco.middleware.kafka.tmetadata.infrastructure

object Topics {
  val METADATA: String = "__topics"
  val METADATA_STORAGE: String = s"${Topics.METADATA}_storage"
}

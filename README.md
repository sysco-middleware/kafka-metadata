# [WIP] KTM
KTM stands for Kafka Topic Metadata.

REST api is aiming to expand kafka-topics metadata, by storing it in another topic.

## Requirement

| Requirement   | Version       | 
| ------------- |:-------------:| 
| Java          | 8             | 
| sbt           | 1.1.2         |
| scala         | 2.12.6        |


## TODO
1. Kafka Streams app in actor
2. ActorObserver


## [SBT](https://www.scala-sbt.org/1.x/docs/Command-Line-Reference.html)

Compile  
`sbt compile`  

Run the project  
`sbt run`  

Package the project  
`sbt package`  

Produce fat-jar  
`sbt assembly`  

Exec test and produce code-coverage report    
`sbt test coverageReport`  

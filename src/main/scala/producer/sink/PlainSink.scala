package producer.sink

import akka.Done
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.Future

class PlainSink(producerSettings: ProducerSettings[Array[Byte], String], kafkaProducer: KafkaProducer[Array[Byte], String])(implicit materializer: Materializer) {
  val done: Future[Done] = Source(1 to 100)
    .map(_.toString)
    .map { elem =>
      println(elem)

      new ProducerRecord[Array[Byte], String]("topic1", elem)
    }
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))
}

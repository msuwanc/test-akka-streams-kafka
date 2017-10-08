package producer.sink

import akka.Done
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.Future

class CommitableSink(consumerSettings: ConsumerSettings[Array[Byte], String], producerSettings: ProducerSettings[Array[Byte], String])(implicit materializer: Materializer) {
  val done: Future[Done] = {
    Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
      .map { msg =>
        println(s"topic1 -> topic2: $msg")
        ProducerMessage.Message(new ProducerRecord[Array[Byte], String](
          "topic2",
          msg.record.value
        ), msg.committableOffset)
      }
      .runWith(Producer.commitableSink(producerSettings))
  }
}

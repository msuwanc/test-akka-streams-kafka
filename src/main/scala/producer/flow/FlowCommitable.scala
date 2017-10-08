package producer.flow

import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.producer.ProducerRecord

class FlowCommitable(consumerSettings: ConsumerSettings[Array[Byte], String], producerSettings: ProducerSettings[Array[Byte], String])(implicit materializer: Materializer) {
  val done =
    Consumer.committableSource(consumerSettings, Subscriptions.topics("topic1"))
      .map { msg =>
        println(s"topic1 -> topic2: $msg")
        ProducerMessage.Message(new ProducerRecord[Array[Byte], String](
          "topic2",
          msg.record.value
        ), msg.committableOffset)
      }
      .via(Producer.flow(producerSettings))
      .mapAsync(producerSettings.parallelism) { result =>
        result.message.passThrough.commitScaladsl()
      }
      .runWith(Sink.ignore)
}

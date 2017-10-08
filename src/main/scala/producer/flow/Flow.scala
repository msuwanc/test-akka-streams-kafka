package producer.flow

import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord

class Flow(producerSettings: ProducerSettings[Array[Byte], String])(implicit materializer: Materializer) {
  val done = Source(1 to 100)
    .map { n =>
      // val partition = math.abs(n) % 2
      val partition = 0
      ProducerMessage.Message(new ProducerRecord[Array[Byte], String](
        "topic1", partition, null, n.toString
      ), n)
    }
    .via(Producer.flow(producerSettings))
    .map { result =>
      val record = result.message.record
      println(s"${record.topic}/${record.partition} ${result.offset}: ${record.value}" +
        s"(${result.message.passThrough})")
      result
    }
    .runWith(Sink.ignore)
}

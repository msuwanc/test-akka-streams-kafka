import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ProducerMessage, Subscriptions}
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import producer.sink.PlainSink

object Main extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("such-system")
  implicit val materializer: Materializer = ActorMaterializer()

  val consumerSetting: consumer.Settings = new consumer.Settings
  val producerSetting: producer.Settings = new producer.Settings

  val plainSink: PlainSink = new PlainSink(producerSetting.producerSettings, producerSetting.kafkaProducer)

  Consumer.committableSource(consumerSetting.consumerSettings, Subscriptions.topics("topic1"))
    .map { msg =>
      println(s"topic1 -> topic2: $msg")
      ProducerMessage.Message(new ProducerRecord[Array[Byte], String](
        "topic2",
        msg.record.value
      ), msg.committableOffset)
    }
    .runWith(Producer.commitableSink(producerSetting.producerSettings))
}

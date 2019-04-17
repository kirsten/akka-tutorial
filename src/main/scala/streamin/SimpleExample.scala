package streamin

import java.nio.file.Paths

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import scala.concurrent.{ExecutionContextExecutor, Future}

object SimpleExample extends App {
  implicit val system: ActorSystem = ActorSystem("StreamingTutorial")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // The first type `Int` is the type of element that the `Source` emits.
  // The second type signals the auxiliary value that running the source
  // will produce.
  // The NotUsed type is appropriate when no auxiliary value is produced
  // by the Source.
  val source: Source[Int, NotUsed] = Source(1 to 100)

  // `runForeach` returns a `Future[Done]` which resolves when the stream finishes.
  val done: Future[Done] = source.runForeach(println)(materializer)

  // The scan operator runs a computation over the whole stream.
  // Nothing is actually computed yet, this is a description of
  // what we want to have computed once we run the stream.
  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)

  // Convert the resulting series of numbers into a stream of
  // ByteString objects describing lines in a text file.
  // Stream is run by attaching a file as the receiver of the data.
  // AKA the sink!
  val result: Future[IOResult] = factorials.map { num =>
    ByteString(s"$num\n")
  }.runWith(FileIO.toPath(Paths.get("factorials.txt")))
}


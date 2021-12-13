package se.thanh.catsphores.chapter4

import scala.concurrent.duration.*

import cats.effect.std.{Console, Queue, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*
import cats.{Applicative, Monad, Parallel}

/** In one common pattern, some threads are producers and some are consumers. Pro- ducers create
  * items of some kind and add them to a data structure; consumers remove the items and process
  * them.
  *
  * There are several synchronization constraints that we need to enforce to make this system work
  * correctly:
  *
  *   - While an item is being added to or removed from the buffer, the buffer is in an inconsistent
  *     state. Therefore, threads must have exclusive access to the buffer.
  *
  * If a consumer thread arrives while the buffer is empty, it blocks until a producers adds a new
  * item.
  */

object ProducerConsumer extends IOApp {

  def run(args: List[String]): IO[ExitCode] = program[IO](10).as(ExitCode.Success)

  def program[F[_]: Temporal](
    total: Int
  )(
    using F: Console[F],
    P: Parallel[F],
  ): F[Unit] = {

    def produce(events: Queue[F, Unit]): F[Unit] =
      for {
        _ <- F.println("producing")
        - <- Temporal[F].sleep(1.millisecond)
        _ <- events.offer(())
        _ <- F.println("done producing")
      } yield ()

    def consume(events: Queue[F, Unit]): F[Unit] =
      for {
        _ <- F.println("consuming")
        _ <- Temporal[F].sleep(500.millisecond)
        _ <- events.take
        _ <- F.println("done consuming")
      } yield ()

    for {
      queue <- Queue.bounded[F, Unit](3)
      producer = produce(queue).replicateA(total)
      consumer = consume(queue).replicateA(total)
      _ <- (consumer, producer).parTupled
    } yield ()
  }

}

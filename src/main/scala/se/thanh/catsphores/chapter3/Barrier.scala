package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.effect.kernel.Deferred
import cats.effect.std.{Console, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*

/** Consider again the Rendezvous problem. A limitation of the solution we presented is that it does
  * not work with more than two threads. Generalize the rendezvous solution. Every thread should run
  * the following code:
  *
  * rendezvous critical point
  *
  * The synchronization requirement is that no thread executes critical point until after all
  * threads have executed rendezvous.
  *
  * Our solution is also called preloaded turnstile which allows all threads run concurrently
  *
  * Page 21
  */

object Barrier extends IOApp {
  def run(args: List[String]): IO[ExitCode] = program(10L).as(ExitCode.Success)

  def program(total: Long): IO[Unit] =
    for {
      mutex <- Deferred[IO, Unit]
      signal <- Semaphore[IO](0)
      barrier = Barrier(total, mutex, signal)
      tasks = List.range(0L, total).map(n => BTask(n, mutex, signal).use())
      allTasks = barrier.use() :: tasks
      _ <- allTasks.parSequence.void
    } yield ()

}

class BTask[F[_]: Temporal](
  number: Long,
  barrier: Deferred[F, Unit],
  signal: Semaphore[F],
)(
  using F: Console[F]
) {

  def use(): F[Unit] =
    for {
      _ <- F.println(s"Task $number started")
      - <- Temporal[F].sleep(number.millisecond)
      _ <- signal.release
      y <- signal.available
      _ <- F.println(s"Availability: $y")
      _ <- barrier.get
      _ <- F.println(s"Task $number after critical point")
    } yield ()

}

class Barrier[F[_]: Temporal](
  tasks: Long,
  mutex: Deferred[F, Unit],
  signal: Semaphore[F],
)(
  using F: Console[F]
) {

  def use(): F[Unit] =
    for {
      _ <- F.println("Barrier started")
      _ <- signal.acquireN(tasks)
      _ <- mutex.complete(())
      _ <- F.println("Barrier completed")
    } yield ()

}

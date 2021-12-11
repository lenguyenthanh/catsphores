package se.thanh.catsphores.semaphores

import scala.concurrent.duration.*

import cats.effect.kernel.Deferred
import cats.effect.std.{Console, CyclicBarrier, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*

/** By using CyclicBarrier we can implement Reusable Barrier problem and preloaded turnstile at the
  * same time
  */
object CatsBarrier extends IOApp {
  def run(args: List[String]): IO[ExitCode] = program(10).as(ExitCode.Success)

  def program(total: Int): IO[Unit] =
    for {
      barrier1 <- CyclicBarrier[IO](total)
      barrier2 <- CyclicBarrier[IO](total)
      tasks = List.range(0, total).map(n => CTask(n, 10, barrier1, barrier2).use())
      _ <- tasks.parSequence.void
    } yield ()

}

class CTask[F[_]: Temporal](
  number: Int,
  repeat: Int,
  barrier1: CyclicBarrier[F],
  barrier2: CyclicBarrier[F],
)(
  implicit F: Console[F]
) {

  def use(): F[Unit] = one().replicateA(repeat).void

  def one(): F[Unit] =
    for {
      _ <- F.println(s"Task $number started")
      _ <- barrier1.await
      _ <- F.println(s"Task $number after critical point")
      _ <- barrier2.await
    } yield ()

}

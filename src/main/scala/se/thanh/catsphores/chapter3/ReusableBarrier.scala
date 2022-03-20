package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.Monad
import cats.effect.kernel.Deferred
import cats.effect.std.{Console, CyclicBarrier, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*

/** Reusable Barrier
  *
  * Often a set of cooperating threads will perform a series of steps in a loop and synchronize at a
  * barrier after each step. For this application we need a reusable barrier that locks itself after
  * all the threads have passed through.
  *
  * Puzzle: Rewrite the barrier solution so that after all the threads have passed through, the
  * turnstile is locked again.
  *
  * Solution: By using CyclicBarrier we can solve reusable Barrier problem and preloaded turnstile
  * at the same time
  *
  * Page 31
  */

object ReusableBarrier extends IOApp {
  def run(args: List[String]): IO[ExitCode] = program(10).as(ExitCode.Success)

  def program(total: Int): IO[Unit] =
    for {
      barrier1 <- CyclicBarrier[IO](total)
      barrier2 <- CyclicBarrier[IO](total)
      tasks = List.range(0, total).map(n => CTask(n, 10, barrier1, barrier2).use())
      _ <- tasks.parSequence.void
    } yield ()

}

class CTask[F[_]: Monad](
  no: Int,
  repeat: Int,
  barrier1: CyclicBarrier[F],
  barrier2: CyclicBarrier[F],
)(
  using F: Console[F]
) {

  def use(): F[Unit] = once().replicateA(repeat).void

  def once(): F[Unit] =
    for {
      _ <- F.println(s"Task $no started")
      _ <- barrier1.await
      _ <- F.println(s"Task $no after critical point")
      _ <- barrier2.await
    } yield ()

}

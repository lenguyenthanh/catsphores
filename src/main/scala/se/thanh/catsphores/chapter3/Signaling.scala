package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.Monad
import cats.effect.std.{Console, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*
import cats.instances.int

import se.thanh.catsphores.Debug.*

/** Possibly the simplest use for a semaphore is signaling, which means that one thread send a
  * signal to another thread to indicate that something has happend.
  *
  * Signaling makes it possible to guarantee that a section of code in one thread will run before a
  * section of code in another thread; in other words, it solves the serialization problem.
  *
  * This is basically the same with Serialization with messages
  *
  * In this example, we run two processes in parallel (so non-determinism), but we can garantee some
  * action in B happen before some action in A by using sinaling technique.
  *
  * Page 11
  */

object Signaling extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      s <- Semaphore[IO](0)
      - <- (threadB(s), threadA(s)).parTupled
    } yield ExitCode.Success

  def threadA[F[_]: Temporal](signal: Semaphore[F])(using F: Console[F]): F[Unit] =
    for {
      _ <- F.println("waiting for 1 second")
      _ <- Temporal[F].sleep(1.second)
      _ <- F.println("Happen before b")
      _ <- F.println("waiting for 1 second")
      _ <- Temporal[F].sleep(1.second)
      _ <- signal.release
    } yield ()

  def threadB[F[_]: Monad](signal: Semaphore[F])(using F: Console[F]): F[Unit] =
    for {
      _ <- signal.acquire
      _ <- F.println("Happen after a")
    } yield ()

}

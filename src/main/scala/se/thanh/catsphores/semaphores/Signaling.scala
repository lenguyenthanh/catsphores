package se.thanh.catsphores.semaphores

import scala.concurrent.duration.*

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
  */

object Signaling extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      s <- Semaphore[IO](0)
      - <- (threadB(s), threadA(s)).parTupled
    } yield ExitCode.Success

  def threadA[F[_]: Temporal](signal: Semaphore[F])(using F: Console[F]): F[Unit] =
    for {
      _ <- F.println("Happen before b1")
      _ <- signal.release.delayBy(1.seconds)
    } yield ()

  def threadB[F[_]: Temporal](signal: Semaphore[F])(using F: Console[F]): F[Unit] =
    for {
      _ <- signal.acquire
      _ <- F.println("Happen after a1")
    } yield ()

}

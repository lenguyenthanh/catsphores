package se.thanh.catsphores.semaphores

import scala.concurrent.duration.*

import cats.effect.std.Semaphore
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/** Redezvous: The idea is that two threads redezvous at a point of execution and neither is allowed
  * to proceed until both have arrived.
  *
  * We want two guarantee that a1 happen before b2 and b1 happen before a2
  */
object Rendezvous extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      a <- Semaphore[IO](0)
      b <- Semaphore[IO](0)
      - <- (threadB(a, b), threadA(a, b)).parTupled
    } yield ExitCode.Success

  def threadA(a: Semaphore[IO], b: Semaphore[IO]): IO[Unit] =
    for {
      _ <- IO("statement a1").debug()
      _ <- a.release
      _ <- b.acquire
      _ <- IO("statement a2").debug()
    } yield ()

  def threadB(a: Semaphore[IO], b: Semaphore[IO]): IO[Unit] =
    for {
      _ <- IO.sleep(1.seconds)
      _ <- IO("statement b1").debug()
      _ <- b.release
      _ <- a.acquire
      _ <- IO("statement b2").debug()
    } yield ()

}

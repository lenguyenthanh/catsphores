package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.effect.std.Semaphore
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/** Rendezvous: The idea is that two threads rendezvous at a specific point of execution and neither
  * is allowed to proceed until both have arrived.
  *
  * Problem: We want two guarantee that a1 happen before b2 and b1 happen before a2 Solution: Create
  * two semaphores, named a and b, and initialize them both to zero. As the names suggest, a
  * indicates whether Thread A has arrived at the rendezvous, and b likewise.
  *
  * Page 13
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

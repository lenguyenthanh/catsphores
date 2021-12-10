package se.thanh.catsphores.chapter1

import scala.concurrent.duration.*

import cats.effect.kernel.Deferred
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

object SerializationWithMessages extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      call <- Deferred[IO, Unit]
      _ <- (alice(call), bob(call)).parTupled
    } yield ExitCode.Success

  def alice(call: Deferred[IO, Unit]): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO("Alice eats breakfast").debug()
      _ <- IO("Work").debug()
      _ <- IO("Alice eats lunch").debug()
      _ <- call.complete(())
    } yield ()

  def bob(call: Deferred[IO, Unit]): IO[Unit] =
    for {
      _ <- IO("Bob eats breakfast").debug()
      _ <- call.get
      _ <- IO("Bob eats lunch").debug()
    } yield ()

}

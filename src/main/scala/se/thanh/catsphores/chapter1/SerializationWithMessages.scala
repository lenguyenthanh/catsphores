package se.thanh.catsphores.chapter1

import scala.concurrent.duration.*

import cats.effect.kernel.Deferred
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/** Serialization with messages Problem: Alice and Bob want to make sure that Bob eats lunch after
  * Alice. Solution:
  *   - Alice eats lunch first and then send a message to Bob
  *   - Bob waits for Alice's message before eating lunch.
  *
  * This solution is called Serialization with messages
  *
  * Page 3
  */

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

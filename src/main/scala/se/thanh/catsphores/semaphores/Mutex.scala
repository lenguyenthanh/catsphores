package se.thanh.catsphores.semaphores

import scala.concurrent.duration.*

import cats.effect.std.Semaphore
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/** Often the code that needs to be protected is called the critical section, I suppose because it
  * is critically important to prevent concurrent access.
  */
object Mutex extends IOApp {

  var count = 0

  def run(args: List[String]): IO[ExitCode] =
    for {
      s <- Semaphore[IO](1)
      _ <- increaseBy(s, 1000)
      _ <- IO(count).debug()
    } yield (ExitCode.Success)

  def increaseBy(s: Semaphore[IO], repeat: Int) = increase(s).parReplicateA(repeat)

  def increase(s: Semaphore[IO]): IO[Unit] =
    for {
      _ <- s.acquire
      _ <- IO.sleep(1.millisecond)
      _ = (count = count + 1) // critical section
      _ <- s.release
    } yield ()

}

package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.effect.std.Semaphore
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/** A common usecase for Semaphores is to enforce mutual exclusion (Mutex)
  *
  * Often the code that needs to be protected is called the critical section, I suppose because it
  * is critically important to prevent concurrent access.
  *
  * Problem: multiple processes can access to a shared mutable value, use Semaphore to to enforce
  * mutual exclusion to that value.
  *
  * Solution: Create a semaphore named mutex that is initialized to 1. A value of one means that a
  * thread may proceed and access the shared variable; a value of zero means that it has to wait for
  * another thread to release the mutex.
  *
  * Cats Effect has native support for Mutex via
  * [Deferred](https://typelevel.org/cats-effect/docs/std/deferred)
  *
  * Page 16
  */

object Mutex extends IOApp {

  var count = 0

  def run(args: List[String]): IO[ExitCode] =
    for {
      s <- Semaphore[IO](1)
      _ <- increaseBy(s, 1000)
      _ <- IO(count).debug()
    } yield ExitCode.Success

  def increaseBy(s: Semaphore[IO], repeat: Int) = increase(s).parReplicateA(repeat)

  // .permit.use uses `acquire` and `release` in `bracket`
  def increase(s: Semaphore[IO]): IO[Unit] = s
    .permit
    .use { _ =>
      for {
        _ <- IO.sleep(1.millisecond)
        _ = count = count + 1 // critical section
      } yield ()
    }

  // inncorrectly uses Semaphore. Since increase can be cancelled between acquire and released
  // it can leak the permit and it’ll never be released.
  // Thanks [Simon](https://github.com/nomisRev) for the correction
  def faultyIncrease(s: Semaphore[IO]): IO[Unit] =
    for {
      _ <- s.acquire
      _ <- IO.sleep(1.millisecond)
      _ = count = count + 1 // critical section
      _ <- s.release
    } yield ()

}

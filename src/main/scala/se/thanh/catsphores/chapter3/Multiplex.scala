package se.thanh.catsphores.chapter3

import scala.concurrent.duration.*

import cats.effect.std.{Console, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*

/** Generalize the previous solution (mutex) so that it allows multiple threads to run in the
  * critical section at the same time, but it enforces an upper limit on the number of concurrent
  * threads. In other words, no more than n threads can run in the critical section at the same
  * time.
  *
  * This pattern is called a multiplex.
  *
  * In real life, the multiplex problem occurs at busy nightclubs where there is a maximum number of
  * people allowed in the building at a time, either to maintain fire safety or to create the
  * illusion of exclusivity.
  *
  * In this example we have 6 processes try to access a precous resource which has a upper limit of
  * number concurrent access is 2. This example is taken from [Semaphore
  * example](https://typelevel.org/cats-effect/docs/std/semaphore)
  *
  * Page 19
  */

object Multiplex extends IOApp {

  def run(args: List[String]): IO[ExitCode] = program.as(ExitCode.Success)

  val program: IO[Unit] =
    for {
      s <- Semaphore[IO](2)
      r1 = new PreciousResource[IO]("R1", s)
      r2 = new PreciousResource[IO]("R2", s)
      r3 = new PreciousResource[IO]("R3", s)
      r4 = new PreciousResource[IO]("R4", s)
      r5 = new PreciousResource[IO]("R5", s)
      r6 = new PreciousResource[IO]("R6", s)
      _ <- List(r1.use, r2.use, r3.use, r4.use, r5.use, r6.use).parSequence.void
    } yield ()

}

class PreciousResource[F[_]: Temporal](name: String, s: Semaphore[F])(implicit F: Console[F]) {

  def use: F[Unit] =
    for {
      x <- s.available
      _ <- F.println(s"$name >> Availability: $x")
      _ <- s.acquire
      y <- s.available
      _ <- F.println(s"$name >> Started | Availability: $y")
      _ <- s.release.delayBy(3.seconds)
      z <- s.available
      _ <- F.println(s"$name >> Done | Availability: $z")
    } yield ()

}

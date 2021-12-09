package se.thanh.catsphores.chapter1

import cats.implicits.*
import cats.effect.{IO, IOApp}
import cats.effect.ExitCode
import se.thanh.catsphores.Debug.*
import cats.effect.kernel.Ref

// Mutual exclusive
object MutualExclusive extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      count <- Ref[IO].of(0L)
      _ <- (threadA(count), threadB(count)).parTupled
      result <- count.get
      _ <- IO.println(result)
    } yield ExitCode.Success
  }

  def threadA(count: Ref[IO, Long]): IO[Unit] =
      count.update(_ + 1).replicateA_(100)

  def threadB(count: Ref[IO, Long]): IO[Unit] =
      count.update(_ + 1).replicateA_(100)

}

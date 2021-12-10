package se.thanh.catsphores.chapter1

import cats.effect.kernel.Ref
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

// Mutual exclusive
object MutualExclusive extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      count <- Ref[IO].of(0L)
      _ <- (threadA(count), threadB(count)).parTupled
      result <- count.get
      _ <- IO.println(result)
    } yield ExitCode.Success

  def threadA(count: Ref[IO, Long]): IO[Unit] = count.update(_ + 1).replicateA_(100)

  def threadB(count: Ref[IO, Long]): IO[Unit] = count.update(_ + 1).replicateA_(100)

}

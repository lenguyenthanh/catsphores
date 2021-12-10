package se.thanh.catsphores.chapter1

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

object NonDeterminism extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    def yes = IO("yes").debug()
    def no = IO("no").debug()
    (yes, no).parTupled.as(ExitCode.Success)
  }

}

package se.thanh.catsphores.chapter1

import cats.implicits.*
import cats.effect.{IO, IOApp}
import cats.effect.ExitCode
import se.thanh.catsphores.Debug.*

object NonDeterminism extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    def yes = IO("yes").debug()
    def no = IO("no").debug()
    (yes, no).parTupled.as(ExitCode.Success)
  }
}

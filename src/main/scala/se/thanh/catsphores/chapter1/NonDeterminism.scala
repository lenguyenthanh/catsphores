package se.thanh.catsphores.chapter1

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*

import se.thanh.catsphores.Debug.*

/**
 * Non-determinism
 * it means it is not possible to tell, by looking at
 * the program, what will happen when it executes.
 *
 * In this example, yes and no run concurrently,
 * the order of execution depends on the scheduler.
 * The output can be `yes no` or `no yes`.
 * */

object NonDeterminism extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    def yes = IO("yes").debug()
    def no = IO("no").debug()
    (yes, no).parTupled.as(ExitCode.Success)
  }

}

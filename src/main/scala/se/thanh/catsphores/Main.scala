package se.thanh.catsphores

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  def run: IO[Unit] = IO.println("Hello catsphores")
}

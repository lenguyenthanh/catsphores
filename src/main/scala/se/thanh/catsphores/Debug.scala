package se.thanh.catsphores

import cats.effect.*

object Debug {

  extension [A](io: IO[A]) {

    def debug(): IO[A] =
      for {
        a <- io
        tn = Thread.currentThread.getName
        _ = println(s"[${tn}] $a")
      } yield a

  }

}

package se.thanh.catsphores.semaphores

import scala.concurrent.duration.*

import cats.effect.kernel.Deferred
import cats.effect.std.{Console, CyclicBarrier, Queue, Semaphore}
import cats.effect.syntax.all.*
import cats.effect.{ExitCode, IO, IOApp, Temporal}
import cats.implicits.*
import cats.{Applicative, Parallel}

/** For example, imagine that threads represent ballroom dancers and that two kinds of dancers,
  * leaders and followers, wait in two queues before entering the dance floor. When a leaders
  * arrives, it checks to see if there is a follower waiting. If so, they can both proceed.
  * Otherwise it waits. Similarly, when a follower arrives, it checks for a leader and either
  * proceeds or waits, accordingly.
  *
  * To make things more interesting, let’s add the additional constraint that each leader can invoke
  * dance concurrently with only one follower, and vice versa. In other words, you got to dance with
  * the one that brought you
  */

object ExclusiveQueue extends IOApp {
  def run(args: List[String]): IO[ExitCode] = program(10).as(ExitCode.Success)

  def program(total: Int): IO[Unit] =
    for {
      leaderQueue <- Queue.bounded[IO, Unit](1000)
      followerQueue <- Queue.bounded[IO, Unit](1000)
      leaderSignal <- Semaphore[IO](0)
      followerSignal <- Semaphore[IO](0)
      leaders = Leaders(total, leaderQueue, leaderSignal).one().parReplicateA(total)
      followers = Followers(total, followerQueue, followerSignal).one().parReplicateA(total)
      operator = Operator(leaderQueue, followerQueue, leaderSignal, followerSignal)
        .use()
        .replicateA(total)
      _ <- (leaders, followers, operator).parTupled
    } yield ()

}

class Operator[F[_]: Temporal](
  leaderQueue: Queue[F, Unit],
  followerQueue: Queue[F, Unit],
  leaderSignal: Semaphore[F],
  followerSignal: Semaphore[F],
)(
  using F: Applicative[F],
  P: Parallel[F],
) {

  def use(): F[Unit] =
    for {
      _ <- (leaderQueue.take, followerQueue.take).parTupled
      _ <- (leaderSignal.release, followerSignal.release).parTupled
    } yield ()

}

class Leaders[F[_]: Temporal](
  number: Int,
  queue: Queue[F, Unit],
  signal: Semaphore[F],
)(
  using F: Console[F]
) {

  def use(): F[Unit] = one().replicateA(number).void

  def one(): F[Unit] =
    for {
      _ <- F.println(s"A leader joined")
      - <- Temporal[F].sleep(number.millisecond)
      _ <- queue.offer(())
      _ <- signal.acquire
      _ <- F.println(s"The leader started dancing")
    } yield ()

}

class Followers[F[_]: Temporal](
  number: Int,
  queue: Queue[F, Unit],
  signal: Semaphore[F],
)(
  using F: Console[F]
) {

  def use(): F[Unit] = one().replicateA(number).void

  def one(): F[Unit] =
    for {
      _ <- F.println(s"A follower joined")
      _ <- queue.offer(())
      _ <- signal.acquire
      _ <- F.println(s"The follower started dancing")
    } yield ()

}

package lectures.part5_type_system

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Tomohiro on 20 juillet 2019.
  */

object HigherKindedTypes extends App {
  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B) : MyList[B]
  }
  trait MyOption[T] {
    def flatMap[B](f: T => B) : MyOption[B]
  }
  trait MyFuture[T] {
    def flatMap[B](f: T => B) : MyFuture[B]
  }

  // combine/multiply List(1,2) x List("a", "b") => List(1a, 1b, 2a, 2b)

  //  def multiply[A, B](a: List[A], b: List[B]) : List[(A, B)] = {
  //    for {
  //      x <- a
  //      y <- b
  //    } yield (x, y)
  //  }
  //  def multiply[A, B](a: Option[A], b: Option[B]) : Option[(A, B)] = {
  //    for {
  //      x <- a
  //      y <- b
  //    } yield (x, y)
  //  }
  //  def multiply[A, B](a: Future[A], b: Future[B]) : Future[(A, B)] = {
  //    for {
  //      x <- a
  //      y <- b
  //    } yield (x, y)
  //  }

  // use Higher Kinded Types

  trait Monad[F[_], A] { // Higher Kinded Type Class
    def flatMap[B](f: A => F[B]) : F[B]
    def map[B](f: A => B) : F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }

  implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  def multiply[F[_], A, B](implicit monadA: Monad[F, A], monadB: Monad[F, B]): F[(A, B)] = {
    for {
      a <- monadA
      b <- monadB
    } yield (a, b)
  }
  /*
    monadA.flatMap(a => monadB.map(b => (a,b)))
   */

  val monadList = new MonadList(List(1, 2, 3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  // Monad[List, Int] => List[Int]

  monadList.map(_ * 2) // List[Int]
  // Monad[List, Int] => List[Int]

  println(multiply(new MonadList(List(1, 2)), new MonadList(List("a", "b"))))
  println(multiply(List(1, 2), List("a", "b")))

  multiply(new MonadOption[Int](Some(2)), new MonadOption[String](Some("Scala")))
  multiply(Some(2), Some("Scala"))

}

package exercices

import java.util.NoSuchElementException

import scala.annotation.tailrec

/**
  * Created by Tomohiro on 12 juillet 2019.
  */

/*
    Exercices: implement a lazily evaluated, singly linked STREAM of elements.


    Stream : a special type of collection where the head of the stream is always evaluated and always available,
    but the tail is always lazily evaluated and available on demand


    naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
    naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
    naturals.foreach(println) // will crash - infinite!
    naturals.map(_ * 2) // stream of all even numbers (potentially infinite)
   */
abstract class MyStream[+A] {
  def isEmpty : Boolean
  def head: A
  def tail : MyStream[A]

  def #::[B >: A](element: B) : MyStream[B] // prepend operator
  def ++[B >: A](anotherStream : => MyStream[B]) : MyStream[B] // concatenate two streams

  def foreach(f: A => Unit) : Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]) : MyStream[B]
  def filter(predicate: A => Boolean) : MyStream[A]

  def take(n: Int) : MyStream[A] // takes the first n elements out of this stream
  def takeAsList(n: Int) : List[A] = take(n).toList()

  /*
    [1, 2, 3].toList([]) =
    [2, 3].toList([1]) =
    [3].toList([1, 2]
   */
  @tailrec
  final def toList[B >: A](acc : List[B] = Nil) : List[B] =
    if(isEmpty) acc
    else tail.toList(acc :+ head)
}

object MyStream {
  def from[A](start: A)(generator: A => A) : MyStream[A] = //new Stream[A](start, generator)
    new Cons[A](start, MyStream.from(generator(start))(generator))
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException
  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)
  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()
  override def map[B](f: Nothing => B): MyStream[B] = this
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this
}

class Cons[A](h: A, t: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false
  override val head: A = h
  override lazy val tail: MyStream[A] = t // call by need

  override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)
  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
  /*
    s = new Cons(1, ?)
    mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_ + 1))
        ... mapped.tail
   */
  override def map[B](f: A => B): MyStream[B] =
    new Cons(f(head), tail map f) // preserve lazy evaluation
  // (tail map f).#::(f(head))
  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ (tail flatMap f)
  override def filter(predicate: A => Boolean): MyStream[A] = {
    if(predicate(head))
      new Cons(head, tail filter predicate)
//      (tail filter predicate).#::(head)
    else
      tail filter predicate // preserve lazy eval
  }

  override def take(n: Int): MyStream[A] =
    if(n <= 0) EmptyStream
    else if(n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n-1))

}

object StreamPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)

  // map, flatmap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).toList())


  /**
   * Exercices on Streams
   */

  println(" =========== Exercice 1 ============")
  // 1 - stream of Fibonacci numbers

  /*
    fibonacci(n) = fibonacci(n-1) + fibonacci(n-2)
      with fibonacci(1) = fibonacci(2) = 1
   */
  /*
  def fibonacci(n: Int, stream : MyStream[Int]) : Int = {
    if(n == 1 || n == 2) new Cons(1, EmptyStream) #:: stream

    if(n > 1)
      fibonacci(n - 1, fibonacci(n - 2) #:: stream.tail)
  }

   */
  def fibonacci(first: Int, second: Int) : MyStream[Int] = {
    new Cons(first, fibonacci(second, first + second))


    //    val streams = MyStream.from(first)(x => fibonacci(x, x+1).toList().sum)

  }

  println(fibonacci(1,1).take(10).toList())


  println(" =========== Exercice 2 ============")
  // 2 - stream of prime numbers with Eratosthenes' sieve
  /*
    [ 2 3 4 5 ... ]
    filter out all numbers divisible by 2
    [ 2 3 5 7 9 11 ... ]
    filter out all numbers divisible by 3
    [ 2 3 5 7 11 13 17 ... ]
    filter out all numbers divisible by 5
      ...
   */

  def eratosthenes(numbers: MyStream[Int]) : MyStream[Int] =
    if(numbers.isEmpty) numbers
    else
      new Cons(numbers.head, eratosthenes(numbers.tail filter (_ % numbers.head != 0)))

  println(eratosthenes(MyStream.from(2)(_ + 1)).take(100).toList())


}
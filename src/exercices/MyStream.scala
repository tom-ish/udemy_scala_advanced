package exercices

import java.util.NoSuchElementException

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
  def ++[B >: A](anotherStream : MyStream[B]) : MyStream[B] // concatenate two streams

  def foreach(f: A => Unit) : Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]) : MyStream[B]
  def filter(predicate: A => Boolean) : MyStream[A]

  def take(n: Int) : MyStream[A] // takes the first n elements out of this stream
  def takeAsList(n: Int) : List[A]
}

object MyStream {
  def from[A](start: A)(generator: A => A) : MyStream[A] = //new Stream[A](start, generator)
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException
  override lazy val tail: MyStream[Nothing] = throw NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Stream[B](element, EmptyStream)
  override def ++[B >: Nothing](anotherStream: MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()
  override def map[B](f: Nothing => B): MyStream[B] = EmptyStream
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = EmptyStream
  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = EmptyStream

  override def take(n: Int): MyStream[Nothing] = EmptyStream
  override def takeAsList(n: Int): List[Nothing] = List()
}

class Stream[A] (h: A, t: MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false
  override def head: A = h
  override lazy val tail: MyStream[A] = t

  override def #::[B >: A](element: B): MyStream[B] =
    if(tail.isEmpty) new Stream(element, this)
    else tail.#::(element)

  override def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] =
    tail ++ (anotherStream.#::(head))

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
  override def map[B](f: A => B): MyStream[B] = (tail map f).#::(f(head))
  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = (tail flatMap f) ++ f(head)
  override def filter(predicate: A => Boolean): MyStream[A] = {
    val filteredTail = tail filter predicate
    if(predicate(head)) filteredTail.#::(head)
    else filteredTail
  }

  override def take(n: Int): MyStream[A] = ???
  override def takeAsList(n: Int): List[A] = ???
}
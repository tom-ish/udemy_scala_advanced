package exercices

import scala.annotation.tailrec

/**
  * Created by Tomohiro on 11 juillet 2019.
  */

trait MySet[A] extends (A => Boolean) {

  /*
    Exercices - Implements a functional set
   */
  def apply(element: A): Boolean = contains(element)

  def contains(element: A) : Boolean
  def +(element: A) : MySet[A] // add
  def ++(anotherSet: MySet[A]) : MySet[A] // union

  def map[B](f: A => B) : MySet[B]
  def flatMap[B](f: A => MySet[B]) : MySet[B]
  def filter(predicate: A => Boolean) : MySet[A]
  def foreach(f: A => Unit) : Unit


  /*
    EXERCICE #2
      - removing an element
      - difference with another set
      - intersection with another set
   */

  def -(element: A) : MySet[A]
  def --(anotherSet: MySet[A]) : MySet[A]
  def &(anotherSet: MySet[A]) : MySet[A]

  // EXERCICE #3 - implements a unary_! = NEGATION of a set

  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(element: A): Boolean = false
  override def +(element: A): MySet[A] = new NonEmptySet[A](element, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B) : MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]) : MySet[B] = new EmptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  // Part 2
  override def -(element: A): MySet[A] = this
  override def --(anotherSet: MySet[A]): MySet[A] = this // difference
  override def &(anotherSet: MySet[A]): MySet[A] = this // intersection

  // Part 3
  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

  // all elements of type A which satisfy a property
  // { x in A | property(x) }

class PropertyBasedSet[A](property : A => Boolean) extends MySet[A] {
  override def contains(element: A): Boolean = property(element)

    // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  override def +(element: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == element)

  // { x in A | property(x) } ++ set = { x in A | property(x) || set contains x }
  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  override def map[B](f: A => B): MySet[B] = politelyFail
  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  override def foreach(f: A => Unit): Unit = politelyFail
  override def filter(predicate: A => Boolean): MySet[A] =
    new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(element: A): MySet[A] = filter(x => x != element)
  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  override def unary_! : MySet[A] =
    new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole !")
}



class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A]{

  override def contains(element: A): Boolean =
    element == head || tail.contains(element)

  override def +(element: A): MySet[A] =
    if(this contains element) this
    else new NonEmptySet(element, this)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] =
    (tail map f) + f(head)
//    tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)
//     tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if(predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }


  // Part 2
  /*
    val r = List(1,2,3).remove(2)
        =
   */

  override def -(element: A): MySet[A] = {
    if(element == head) tail
    else tail - element + head
  }

  // difference
  override def --(anotherSet: MySet[A]): MySet[A] =
    filter(x => !anotherSet(x))

  // intersection
  // is the same thing than filtering
  override def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet)


  // Part 3
  override def unary_! : MySet[A] =
    new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {
  /*
    val s = MySet(1, 2, 3)
       = buildSet(seq(1, 2, 3), [])
       = buildSet(seq(2, 3), [] + 1)
       = buildSet(seq(3), [1] + 2)
       = buildSet(seq(), [1, 2] + 3)
       = [1, 2, 3]
   */
  def apply[A](values : A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq : Seq[A], acc : MySet[A]) : MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s + 5 ++ MySet(-1, -2, 4) + 3 flatMap (x => MySet(x,  x * 10)) filter (_ % 2 == 0) foreach println


  val negative = !s // s.unary_! = all the naturals not equal to 1, 2, 3, 4
  println(negative(2))
  println(negative(6))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))

}
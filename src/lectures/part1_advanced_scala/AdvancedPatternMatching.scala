package lectures.part1_advanced_scala

/**
  * Created by Tomohiro on 10 juillet 2019.
  */

object AdvancedPatternMatching extends App {
  val numbers = List(1)

  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
    - constants
    - wildcard
    - case classes
    - tuples
    - some special magic like above
   */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if(person.age < 21) None
      else Some(person.name, person.age)

    def unapply(age: Int) : Option[String] =
      Some(if(age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(name, age) => s"Hi my name is $name and I am $age yo"
  }

  println(greeting)

  val status = bob.age match {
    case Person(status) => s"My legal status is $status"
  }


  /*
      Exercices
   */

  val n: Int = 45
  val mathProperty = n match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }

  object even {
    def unapply(x: Int): Boolean = x % 2 == 0
  }

  object singleDigit {
    def unapply(x: Int): Boolean = x > -10 && x < 10
  }

  println(mathProperty)

  // infix pattern
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
//    case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head : A = ???
    def tail : MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]) : Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }
}

package exercices

import lectures.part4_implicits.TypeClasses.User

/**
  * Created by Tomohiro on 17 juillet 2019.
  */

object EqualityPlayground extends App {

  /**
    * Equality
    */
  trait Equal[T] {
    def apply(a: T, b: T) : Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }


  /*
    Exercise : implement the Type Class pattern for the equality
   */

  object Equal {
    def apply[T](a: T, b: T)(implicit instance : Equal[T]) = instance.apply(a, b)
  }


  val john = User("John", 32, "john@rockthejvm.com")
  val anotherJohn = User("John", 51, "anotherjohn@rockthejvm.com")
  println(Equal.apply(john, anotherJohn))
  println(Equal(john, anotherJohn))

  // AD-HOC polymorphism


  /*
    Exercise - improve the Equal Type Class with an implicit conversion class
      === (anotherValue : T)
      !== (anotherValue : T)
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]) : Boolean = equalizer.apply(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]) : Boolean = !equalizer.apply(value, anotherValue )
  }

  println(john === anotherJohn)
  /*
    john.===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJohn)(NameEquality)
   */
  /*
    TYPE SAFE
   */
  println(john == 43)
  //  println(john === 43) // TYPE SAFE
}

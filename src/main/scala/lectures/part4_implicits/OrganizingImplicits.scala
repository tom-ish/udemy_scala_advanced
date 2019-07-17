package lectures.part4_implicits

/**
  * Created by Tomohiro on 17 juillet 2019.
  */

object OrganizingImplicits extends App {

  implicit def reverseOrdering : Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit val normalOrdering : Ordering[Int] = Ordering.fromLessThan(_ < _)

  println(List(1,4,3,5,2).sorted)

  // scala.Predef

  /*
    Implicits (used as implicit parameters):
      - val/var
      - object
      - accessor methods = defs with no parenthesis
   */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

//  object Person {
//    implicit def alphabeticalOrdering : Ordering[Person] = Ordering.fromLessThan (
//      (first, second) => first.name.compareTo(second.name) < 0)
//  }

//  implicit def ageOrdering : Ordering[Person] = Ordering.fromLessThan (
//    (first, second) => first.age < second.age)

//  println(persons.sorted)

  /*
    Implicits scope
    - normal scope = LOCAL SCOPE
    - imported scope
    - companion objects of all types involved in the method signature
      - List
      - Ordering
      - all the types involved = A or any supertype
   */
  // def sorted[B :> A](implicit ord: Ordering[B]) : List[B]


  object AlphabeticNameOrdering {
    implicit def alphabeticalOrdering : Ordering[Person] = Ordering.fromLessThan (
          (first, second) => first.name.compareTo(second.name) < 0)
  }

  object AgeOrdering {
    implicit def ageOrdering : Ordering[Person] = Ordering.fromLessThan (
      (first, second) => first.age < second.age)
  }

  import AgeOrdering._
  println(persons.sorted)



  /*
    Exercise

    - totalPrice = most used (50%)
    - by unit count = 25%
    - by unit price = 25%

   */

  case class Purchase(nUnit: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering : Ordering[Purchase] = Ordering.fromLessThan(
      (a, b) => a.nUnit * a.unitPrice < b.nUnit * b.unitPrice )
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering : Ordering[Purchase] = Ordering.fromLessThan(_.nUnit < _.nUnit)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering : Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }
}

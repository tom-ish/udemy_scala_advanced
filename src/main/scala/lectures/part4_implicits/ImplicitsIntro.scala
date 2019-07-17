package lectures.part4_implicits

/**
  * Created by Tomohiro on 17 juillet 2019.
  */

object  ImplicitsIntro extends App {

  val pair = "Daniel" -> 555
  val intPair = 1 -> 2

  case class Person(name : String) {
    def greet = s"Hey,  my name is $name"
  }

  implicit def fromStringToPerson(str : String) : Person = Person(str)

  println("Yo".greet) // println(fromStringToPerson("Yo").greet)

//  class A {
//    def greet: Int = 2
//  }
//  implicit def fromStringToA(str: String) : A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) : Int = x + amount
  implicit val defaultAmount = 10

  increment(2)
  // NOT the same as default args

}

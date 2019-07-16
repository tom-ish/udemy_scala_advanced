package lectures.part2_advanced_functional_programming

/**
  * Created by Tomohiro on 11 juillet 2019.
  */

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] <===> Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if(x == 2) 56
    else if(x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // {1, 2, 5} => Int

   val aPartialFunction : PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }// partial function value

  println(aPartialFunction(2))
  // println(aPartialFunction(54234))  // program crashes: MatchError


  // Partial Functions utilities
  println(aPartialFunction.isDefinedAt(65))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  // orElse
  val partialFunctionChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(partialFunctionChain(2))
  println(partialFunctionChain(45))


  // Partial functions extends normal functions
  val aTotalFunction : Int => Int = {
    case 1 => 99
  }

  // High Order Functions accept Partial functions as well
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  /*
    Note : Partial Functions can only have ONE parameter type
   */

  /**
    * Exercices
    *
    * 1 - Construct a Partial Function instance yourself (anonymous class)
    * 2 - Dumb Chatbot as a Partial Function
    */



  val myPartialFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }

    override def isDefinedAt(x : Int): Boolean =
      x == 1 || x == 2 || x == 5
  }

  println(myPartialFunction(1))
//  println(myPartialFunction(55))


  // Chatbot
  val chatbot : PartialFunction[String, String] = {
    case "hello" => "Hi, my name is HAL9000"
    case "Yo" => "Konnichiwa"
    case "call mom" => "unable to find your phone without your credit card"
  }

  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}

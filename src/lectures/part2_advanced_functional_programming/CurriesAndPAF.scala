package lectures.part2_advanced_functional_programming

/**
  * Created by Tomohiro on 11 juillet 2019.
  */

object CurriesAndPAF extends App {

  // curried function
  val superAdder : Int => Int => Int = x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + x
  println(add3(5))
  println(superAdder(3)(5)) // curried function


  def curriedAdder(x: Int)(y: Int) : Int = x + y // curried method

  val add4 : Int => Int = curriedAdder(4)
  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int) = x + 1
  List(1,2,3).map(inc) // ETA-expansion   ===> List(1,2,3).map(x => inc(x))


  // Partial function applications
  val add5 = curriedAdder(5)_ // Int => Int

  // EXERCICES
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many implementations of add7 as possible using the above
  val add7_1a = (x: Int) => simpleAddFunction(7, x)
  val add7_1b = (x: Int) => simpleAddMethod(7, x)
  val add7_1c = (x: Int) => curriedAddMethod(7)(x)

  val add7_2 = simpleAddFunction.curried(7)
  val add7_26 = simpleAddFunction(7, _: Int) // works as well

  val add7_3 = curriedAddMethod(7) _    // PAF
  val add7_4 = curriedAddMethod(7)(_)   // PAF = alternative syntax

  val add7_5 = simpleAddMethod(7, _ : Int) // alternative syntax for turning methods into function values
            // y => simpleAddMethod(7, y)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hi my name is ", _: String, ", how are you?")
                  // x: String => concatenator(hello, x, howareyou)
  println(insertName("Tomo"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
                  // (x, y) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Tomo", " Scala is awesome"))



  // EXERCICES
  /*
      1. Process a list of numbers and return their string representation with different formats
         Use the %2.4f, %8.6f and %14.12f with a curried formatter function
   */
  println("%4.2f".format(Math.PI))

  def curriedFormatter(f : String)(n : Double) : String = f.format(n)

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  println(curriedFormatter("%4.2f")(2))

  val simpleFormat = curriedFormatter("%4.2f") _ // lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(preciseFormat))

  /*
      2. difference between :
         - functions vs methods
         - parameters : by-name vs 0-lambda
   */
  def byName(n: => Int) : Int = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenthesisMethod(): Int = 42
  /*
    calling byName and byFunction
    - int
    - method
    - parenthesisMethod
    - lambda
    - PartiallyAppliedFunction
   */
  byName(23) // ok
  byName(method) // ok
  byName(parenthesisMethod()) // ok
  byName(parenthesisMethod) // ok but be aware ===> byName(parenthesisMethod())
//  byName(() => 42) // not ok
  byName((() => 42)()) // ok
//  byName(parenthesisMethod _) // not ok

//  byFunction(45) // not ok
//  byFunction(method) // not ok!!!!!! does not do ETA-expansion!
  byFunction(parenthesisMethod) // compiler does ETA-expansion
  byFunction(() => 46) //works
  byFunction(parenthesisMethod _) // also works, but warning - unnecessary
}

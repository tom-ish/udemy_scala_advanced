package lectures.part1_advanced_scala

import scala.util.Try

/**
  * Created by Tomohiro on 10 juillet 2019.
  */

object DarkSugars extends App {

  // syntax sugar #1 : method with a single param
  def singleArgMethod(arg: Int) : String = s"$arg little ducks..."

  val description = singleArgMethod {
    // write some complex code
    42
  }

  val aTryInstance = Try { // java's try { ... }
    throw new RuntimeException
  }

  List(1,2,3).map { x =>
    x + 1
  }

  // syntax sugar#2 : single abstract method
  trait Action {
    def act(x: Int) : Int
  }

  val anInstance: Action = new Action{
    override def act(x: Int): Int =  x+1
  }

  val aFunkyInstance: Action = (x : Int) => x+1

  // examples : Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Thread Java-style")
  })

  val aSweeterThread = new Thread(() => println("Thread Scala-style"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(x: Int) : Unit
  }

  val anAbstractInstance : AnAbstractType = (a: Int) => println(a)

  // syntax sugar #3 : the :: and #:: methods are special
  val prependedList = 2 :: List(3,4)
  // 2.::(List(3,4))
  // List(3,4).::(2)
  // ?!

  1 :: 2 :: 3 :: List(4, 5)
  List(4,5).::(3).::(2).::(1) // is equivalent

  class MyStream[T] {
     def -->: (value: T) : MyStream[T] = this // actual implementation here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4 : multi-word method naming

  class TeenGirl(name:String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly)")
  lilly `and then said` "Scala is so sweet"

  // syntax sugar #5 : infix types
  class Composite[A, B] {
//    val composite: Composite[Int, String] = ???
    val composite: A Composite B = ???
  }

  class -->[A, B]
  val towards: Int --> String = ???

  // syntax sugar #6 : update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collection
  // remember apply() AND update()

  // syntax sugar #7 : setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0
    def member: Int = internalMember
    def member_=(value: Int): Unit =
      internalMember = value // "setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)
}

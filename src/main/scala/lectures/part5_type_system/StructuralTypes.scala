package lectures.part5_type_system

/**
  * Created by Tomohiro on 20 juillet 2019.
  */

object  StructuralTypes extends App {

  // Structural types

  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close() : Unit = println("yeah yeah I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  // def closeQuietly(closeable: JavaCloseable OR HipsterCloseable) // ?!

  type UnifiedCloseable = {
    def close() : Unit
  }

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })

  closeQuietly(new HipsterCloseable)



  // TYPE REFINEMENT


  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advancedCloseable: AdvancedCloseable) : Unit = advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterCloseable)


  // using structural types as standalone types
  def altClose(closeable: { def close(): Unit }): Unit = closeable.close()


  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("Vrooom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // static duck typing

  // CAVEAT: based on reflection

  /*
    Exercise
   */

  // 1.
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head : Brain = new Brain
  }
  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: { def head : T }) : Unit = println(somethingWithAHead.head)
  /*
    is f compatible with CBL and with a human? Yes.
   */

  case object CBNil extends CBL[Nothing] {
    override def head: Nothing = ???
    override def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human) // ?! T = Brain

  // 2.
  object HeadEqualizer {
    type Headable[T] = {
      def head : T
    }

    def ===[T](a: Headable[T], b: Headable[T]) : Boolean = a.head == b.head
  }
  /*
    is HeadEqualizer compatible with CBL and with a human? Yes.
   */
  val brainzList = CBCons(new Brain, CBNil)
  val stringList = CBCons("Brainz", CBNil)
  HeadEqualizer.===(brainzList, new Human)
  // problem:
  HeadEqualizer.===(new Human, stringList) // not type safe

}

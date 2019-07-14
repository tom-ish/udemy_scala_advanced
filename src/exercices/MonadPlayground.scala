package exercices

/**
  * Created by Tomohiro on 14 juillet 2019.
  */

object MonadPlayground extends App {

  /*
      EXERCICE :
      1 - implement a Lazy[T] monad = computation which will only be executed when it's needed
        unit / apply
        flatMap

      2 - Monads = unit + flatMap
          Monads = unit + map + flatten

          Monad[T] {
            def flatMap[B](f: T => Monad[B]) : Monad[B] = .... (implemented)

            def map[B](f: T => B) : Monad[B] = ???
            def flatten(m: Monad[Monad[T]]) : Monad[T] = ???

            (have List in mind)
          }
   */

  class Lazy[+A](value : => A) {
    // call by need
    private lazy val internalValue = value
    def use: A = internalValue
    def flatMap[B](f: (=> A) => Lazy[B]) : Lazy[B] = f(internalValue)

    def map[B](f: (=> A) => B) : Lazy[B] = flatMap(x => Lazy(f(x))) //Lazy(f(internalValue))
    def flatten[A](l: Lazy[Lazy[A]]) : Lazy[A] = l.flatMap((x : Lazy[A]) => x)
  }

  object Lazy {
    def apply[A](value : => A) : Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    42
  }

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })
  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })
  flatMappedInstance.use
  flatMappedInstance2.use


  /*
    Left identity
      unit.flatMap(f) => f(v)
      Lazy(v).flatMap(f) => f(v)

    Right identity
      lazy.flatMap(unit) = lazy
      lazy(v).flatMap(x => Lazy(x)) =  Lazy(v)

    Associativity
      Lazy(v).flatMap(f).flatMap(g) = lazy.flatMap(x => f(x).flatMap(g))
      Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g)
      Lazy(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   */

  /**
    * 2 - Monads = unit + flatMap
    * Monads = unit + map + flatten
    *
    * Monad[T] {
    *   def flatMap[B](f: T => Monad[B]) : Monad[B] = .... (implemented)
    *
    *   def map[B](f: T => B) : Monad[B] = flatMap(x => unit(f(x))) // Monad[B]
    *   def flatten(m: Monad[Monad[T]]) : Monad[T] = m.flatMap((x : Monad[T]) => x)
    *
    *   (have List in mind)
    *   // List(1,2,3).map(_ * 2) = List(1,2,3).flatMap(x => List(x * 2)) = List(2,4,6)
    *   // List(List(1,2), List(3,4)).flatten() = List(List(1,2), List(3,4)).flatMap(x => x) = List(1,2,3,4)
    * }
    */
}

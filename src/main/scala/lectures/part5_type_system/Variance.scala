package lectures.part5_type_system

/**
  * Created by Tomohiro on 18 juillet 2019.
  */

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage : CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]
  //  val icage : ICage[Animal] = new ICage[Cat]
  //  val x: Int = "hello world"

  // hell no - opposite = contravariance
  class XCage[-T]
  val xcage : XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION

  //  class ContravariantCage[-T](val animal: T)
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
        => doesn't compile !!!
   */

  //  class CovariantVariableCage[+T](var animal: T) // types of vars are in CONTRAVARIANT POSITION
  /*
    val ccage: CCage[Animal] = new CCage[Cat](new Cat)
    ccage.animal = new Crocodile
   */
  //  class ContravariantVariableCage[-T](var animal: T) // also in COVARIANT POSITION
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
        => doesn't compile !!!
   */

  class InvariantVariableCage[T](var animal: T) // OK



  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // method arguments are in CONTRAVARIANT POSITION
  //  }
  /*
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.addAnimal(new Cat)

    FORBIDDEN => trying to add Dog Cat in CCage of Dog
   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc : AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  //  acc.addAnimal(new Dog) // Not Good
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >: A](element: B) : MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // return types
  class PetShop[-T] {
    //    def get(isItaPuppy: Boolean) : T = ??? // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
      val catShop = new PetShop[Animal] {
        def get(isItaPuppy: Boolean) : Animal = new Cat
      }

      val dogShop : PetShop[Dog] = catShop
      dogShop.get(true)  ==> gives an EVIL CAT
     */

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S) : S = defaultAnimal
  }

  val shop : PetShop[Dog] = new PetShop[Animal]
  //  val evilCat = shop.get(true, new Cat)

  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    BIG RULES :
    - Method arguments are in CONTRAVARIANT Position
    - Method return types are in COVARIANT Position
   */


  /**
    * Exercise:
    * 1. Invariant, Covariant, Contravariant
    *   Parking[T](things: List[T]) {
    *     park(vehicle: T)
    *     impound(vehicles: List[T])
    *     checkVehicles(condition: String): List[T]
    *   }
    *
    * 2. used someone else's API: IList[T]
    * 3. Parking = Monad!
    *       - flatMap
    */

  // 1 - Parking
  //    Invariant
  class IParking[T](things: List[T]) {
    def park(vehicle: T) : IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(condition: String): List[T] = things

    def flatMap[S](f: T => IParking[S]) : IParking[S] = ???
  }

  //    Covariant
  class CParking[+T](things: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(condition: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]) : CParking[S] = ???
  }

  //    Contravariant
  class XParking[-T](things: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](condition: String): List[S] = ???

    def flatMap[R <: T, S](f: R => XParking[S]) : XParking[S] = ???
  }

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle


  // 2 - Parking with IList[T] Invariant
  class IList[T]

  class CParking2[+T](things: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking2[S] = ???
    def checkVehicles(condition: String): List[T] = ???
  }

  class XParking2[-T](things: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def imbound[S <: T](vehicles: IList[S]) : XParking2[S] = ???
    def checkVehicles[S <: T](condition: String) : IList[S]  = ???
  }

}

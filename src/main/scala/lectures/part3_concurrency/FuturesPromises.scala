package lectures.part3_concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

// important for futures
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Tomohiro on 16 juillet 2019.
  */

object FuturesPromises extends App {

  def calculateMeaningOfLife() : Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculate the meaning of life on another Thread
  } // (global) which is passed by the compiler

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the Future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  } // SOME thread

  Thread.sleep(3000)



  case class Profile(id: String, name: String) {
    def poke(anotherProfile : Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // "database"
    val names = Map (
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String) : Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile) : Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // client : mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
//  mark.onComplete {
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriend(markProfile)
//      bill.onComplete {
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//    case Failure(e) => e.printStackTrace()
//  }



  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)


  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))




  // Online Banking App
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM Banking"

    def fetchUser(name : String) : Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double) : Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double) : String = {
      // fetch the user form the DB
      // create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions => pimp my library
    }
  }

  println(BankingApp.purchase("Daniel", "iPhone12","RockTheJVMStore", 3000))



  // Promises

  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread 1 - "Consumer"
  future.onComplete {
    case Success(r) => println("[Consumer] I have received " + r)
  }

  // thread 2 - "Producer"
  val producer = new Thread(() => {
    println("[Producer] crunching numbers...")
    Thread.sleep(500)

    // "fulfilling a Promise
    promise.success(42)
    println("[Producer] done")
  })

  producer.start()
  Thread.sleep(1000)




  // EXERCICES
  /*
    1) fulfill a Future IMMEDIATELY with a value
    2) def inSequence(first, second) => (FutureA OK then ==> FutureB)
    3) def first(futureA, futureB) => new Future with the first value of the two Futures
    4) def last(futureA, futureB) => new Future with the last value
    5) def retryUntil[T](action: () => Future[T], condition : T => Boolean) : Future[T]
   */

  // 1) fulfill immediately
  def fulfillImmediately[T](value : T) : Future[T] = Future(value)

  // 2) inSequence
  def inSequence[A, B](first : Future[A], second : Future[B]): Future[B] =
    first.flatMap(_ => second)

  // 3) first out of two Futures
  def first[T](futureA : Future[T], futureB : Future[T]) : Future[T] = {
    val promise = Promise[T]

//    def tryComplete(promise : Promise[T], result : Try[T]) = result match {
//      case Success(rslt) => try {
//        promise.success(rslt)
//      } catch {
//        case _ =>
//      }
//      case Failure(t) => try {
//        promise.failure(t)
//      } catch {
//        case _ =>
//      }
//    }

    futureA.onComplete(promise.tryComplete(_))
    futureB.onComplete(promise.tryComplete(_))
    promise.future
  }

  // 4) last out of two Futures
  def last[T](futureA : Future[T], futureB : Future[T]) : Future[T] = {
    // promise 1 : which both Future will try to complete
    // promise 2 : which the LAST Future will complete
    val bothPromise = Promise[T]
    val lastPromise = Promise[T]

    val checkAndComplete = (result : Try[T]) =>
      if(!bothPromise.tryComplete(result))
        lastPromise.complete(result)


    futureA.onComplete(checkAndComplete)
    futureB.onComplete(checkAndComplete)

    lastPromise.future
  }


  val fast = Future {
    Thread.sleep(100)
    42
  }
  val slow = Future {
    Thread.sleep(200)
    45
  }
  first(fast, slow).foreach(f => println("FIRST : " + f))
  last(fast, slow).foreach(l => println("LAST : " + l))

  def retryUntil[T](action : () => Future[T], condition : T => Boolean) : Future[T] = {
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => println("settled at : " + result))
  Thread.sleep(10000)
}

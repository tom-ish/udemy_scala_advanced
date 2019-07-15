package lectures.part3_concurrency

import java.util.concurrent.{Executor, Executors}

/**
  * Created by Tomohiro on 14 juillet 2019.
  */

object Intro extends App {

  /*
    interface Runnable {
      public void run()
    }
   */
  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val aThread = new Thread(runnable)

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // create a JVM thread => OS thread
  runnable.run() // doesn't do anything in parallel !
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
//  threadHello.start()
//  threadGoodbye.start()
  // different runs produce different results


  // Executors
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("done after 1 second")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("done after 2 seconds")
//  })

//  pool.shutdown()
//  pool.execute(() => println("should not appear"))

//  pool.shutdownNow()



  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })
//
//    thread1.start()
//    thread2.start()
//    println(x)
  }

  // for(_ <- 1 to 1000) runInParallel
  // race condition


  class BankAccount(@volatile var amount: Int) {
    override def toString : String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println("I've bought " + thing)
//    println("My account is now " + account)
  }
//
//  for(_ <- 1 to 1000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account, "Shoes", 3000))
//    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if(account.amount != 43000) println("HAHA : " + account.amount)
//    println()
//  }

  /*
    thread1 (shoes) : 50000
      - account = 50000 - 3000 = 47000
    thread2 (iPhone): 50000
      - account = 50000 - 4000 = 46000
    overwrites the memory of account.amount
   */

  // option #1 : use synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
      println("I've bought " + thing)
      println("My account is now " + account)

    }
  }

  // option #2 : use @volatile

  /**
    * EXERCICES
    */

  /*
      1 - Construct 50 "inception" threads
            Thread1 -> thread2 -> thread3 -> ...
            println("hello from thread #3")
          in REVERSE ORDER
   */
  def createInceptionThread(n: Int, index : Int = 1) : Unit = {
    if(index < n) {
      val inceptionThread = new Thread(() => {
        createInceptionThread(n, index + 1)
      })
      inceptionThread.start()
      inceptionThread.join()
      println("hello from thread #" + index)
    }
  }

  def inceptionThreads(maxThreads: Int, index: Int = 1) : Thread =
    new Thread(() => {
      if(index < maxThreads) {
        val newThread = inceptionThreads(maxThreads, index+1)
        newThread.start()
        newThread.join()
      }
      println("hello from thread #" + index)
  })

//   createInceptionThread(50)
  inceptionThreads(50).start()

  /*
      2 - a) what is the biggest value possible for x? 100
          b) what is the smallest value possible for x? 1

          thread1 : x = 0
          thread2 : x = 0
              ...
          thread100 : x = 0

          for all threads : x = 1 and write it back to x
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  threads.foreach(_.join())
  println(x)

  /*
      3 - sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join()
  println(message)
  /*
        what's the value of message? "Scala is awesome
        is it guaranteed? NOOOO
        why? why not?

        (main thread)
          message = "Scala sucks"
          awesomeThread.start()
          sleep() - relieves execution
        (awesome thread)
          sleep() - relieves execution
        (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
        (OS gives the CPU back to the MAIN thread)
          println("Scala sucks")
        (OS gives the CPU back to awesomeThread
          message = "Scala is awesome"
   */

  // how do we fix this?
  // synchronizing does NOT work
}

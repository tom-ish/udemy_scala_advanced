package lectures.part3_concurrency

import scala.collection.mutable
import scala.util.Random

/**
  * Created by Tomohiro on 15 juillet 2019.
  */

object ThreadCommunication extends App {

  /*
    the producer-consumer problem

    producer -> [ x ] -> consumer
   */

  class SimpleContainer {
    private var value : Int = 0

    def isEmpty : Boolean = value == 0
    def set(newValue: Int) = value = newValue
    def get = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProducerConsumer() : Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      while(container.isEmpty) {
        println("[Consumer] actively waiting...")
      }

      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[Producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // naiveProducerConsumer()


  // wait and notify
  def smartProducerConsumer() : Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      container.synchronized {
        container.wait()
      }

      // container must have some value
      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] Hard at work...")
      Thread.sleep(2000)

      val value = 42

      container.synchronized {
        println("[Producer] I am producing the value " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //smartProducerConsumer()




  def producerConsumerLargeBuffer() : Unit = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          if(buffer.isEmpty) {
            println("[Consumer] Buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[Consumer] consumed : " + x)

          // todo
          // Hey producer, there's empty space available, are you lazy ?!
          buffer.notify()
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          if(buffer.size == capacity) {
            println("[Producer] Buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[Producer] producing " + i)
          buffer.enqueue(i)

          // todo
          // Hey consumer, new food for you !
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  //producerConsumerLargeBuffer()



  /*
    Producer Consumer Level 3

        producer1 -> [ ? ? ? ] -> consumer1
        producer2 ----^     ^---> consumer2
   */


  class Consumer(id : Int, buffer : mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          /*
            producer produces value, two Cons are waiting
            notifies ONE consumer, notifies on buffer
            notifies the other consumer
           */
          while (buffer.isEmpty) {
            println(s"[Consumer $id] Buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue() // OOps.!
          println(s"[Consumer $id] consumed : " + x)

          buffer.notify()
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer : mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[Producer $id] Buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[Producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }

  }

  def multipleProducerConsumers(nConsumers: Int, nProducers: Int) : Unit = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

 // multipleProducerConsumers(3, 3)




  /*
    Exercices
    1) Think of an example where notifiyAll acts in a different way than notifiy ?
    2) Create a deadlock
    3) Create a livelock
   */

  // 1) NotifyAll()
  def testNotifyAll() : Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[Thread $i] waiting...")
        bell.wait()
        println(s"[Thread $i] hooray !")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[ANNOUCER] Rock'n Roll !!!")
      bell.synchronized {
        bell.notifyAll() // NOTE THE DIFFERENCE WITH NOTIFY()
      }
    }).start()
  }

  //RtestNotifyAll()


  // 2) Deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this : I am bowing to $other")
        other.rise(this)
        println(s"$this : my friend $other has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this : I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide() : Unit =
      if(side == "right") side = "left"
      else side = "right"

    def pass(other: Friend) : Unit = {
      while(this.side == other.side) {
        println(s"$this : Oh, but please, $other, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  //  new Thread(() => sam.bow(pierre)).start() // sam's lock,   | then pierre's lock
  //  new Thread(() => pierre.bow(sam)).start() // pierre's lock | then sam's lock


  // 3) Livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}

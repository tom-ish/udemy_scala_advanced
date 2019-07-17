package lectures.part3_concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

/**
  * Created by Tomohiro on 16 juillet 2019.
  */

object ParallelUtils extends App {

  // 1 - Parallel Collections
  val parList = List(1,2,3).par

  val aParVector = ParVector[Int](1, 2, 3)

  /*
    Seq
    Vector
    Array
    Map - Hash, Trie
    Set - Hash, Trie
   */

  def measure[T](operation : => T) : Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure(list.map(_+1))
  val parallelTime = measure(list.par.map(_+1))

  println("Serial time is : " + serialTime)
  println("Parallel time is : " + parallelTime)


  /*
    Map-Reduce model
      - split the elements into chunks - Splitter
      - operation
      - recombine - Combiner
   */

  // map, flatMap, filter, foreach, reduce, fold
  println(List(1,2,3).reduce(_ - _))
  println(List(1,2,3).par.reduce(_ - _))

  // synchronization
  var sum = 0
  List(1,2,3).par.foreach(sum += _)
  println(sum) // race condition


  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
    alternatives
    - ThreadPoolTaskSupport - deprecated
    - ExecutionContextTaskSupport(ec : ExecutionContext)
   */

  aParVector.tasksupport = new TaskSupport {
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???

    override val environment: AnyRef = ???
  }


  // 2 - atomic operations and references

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write

  atomic.getAndSet(5) // thread-safe combo

  atomic.compareAndSet(38, 56)
  // if the value is 38, then set to 56
  // reference equality

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread-safe acculation
  atomic.getAndAccumulate(12, _ + _)
}

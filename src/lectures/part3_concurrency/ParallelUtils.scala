package lectures.part3_concurrency

import scala.collection.parallel.CollectionConverters._

/**
  * Created by Tomohiro on 16 juillet 2019.
  */

object ParallelUtils extends App {

  // 1 - Parallel Collections
  val parList = List(1,2,3).par

  val aParVector = ParVector[Int](1, 2, 3)
}

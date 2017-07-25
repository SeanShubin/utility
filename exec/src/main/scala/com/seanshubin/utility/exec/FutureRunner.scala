package com.seanshubin.utility.exec

import scala.concurrent.Future

trait FutureRunner {
  def runInFuture[T](block: => T): Future[T]
}

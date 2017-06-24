package com.seanshubin.utility.exec

import java.util.concurrent.TimeUnit

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class PrototypeForStubbingExecutionContext extends FunSuite {
  test("foo") {
    val runnables: ArrayBuffer[Runnable] = new ArrayBuffer()
    implicit val executionContext = new ExecutionContext {
      override def execute(runnable: Runnable): Unit = {
        runnables.append(runnable)
      }

      override def reportFailure(cause: Throwable): Unit = {
        println("reportFailure")
        cause.printStackTrace()
      }
    }
    val x = Future {
      println("b")
      throw new RuntimeException("boo!")
      println("c")
    }(executionContext)
    runnables(0).run()
    Await.ready(x, Duration(1, TimeUnit.SECONDS))
    println(x)
  }
}

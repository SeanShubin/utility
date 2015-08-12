package com.seanshubin.utility.system

import java.util.stream.{DoubleStream, IntStream, LongStream}

/*
This is part of a "contract" and "contract integration" pattern
The sole purpose of these types of classes is to integrate the environment outside of our control with a contract (trait/interface/protocol)
What belongs here are methods that are a one-to-one pass through to the system implementation
What does NOT belong here is logic, wrappers, helpers, utilities, method chaining, etc.
If you need any of that, put them in a separate cass that delegates to this one
The contract can be used when implementing fakes, mocks, and stubs
Proper abstractions can be implemented with full test coverage by delegating to these low level wrappers
*/
trait RandomContract {
  def nextBytes(bytes: Array[Byte])

  def nextInt: Int

  def nextInt(bound: Int): Int

  def nextLong: Long

  def nextBoolean: Boolean

  def nextFloat: Float

  def nextDouble: Double

  def nextGaussian: Double

  def ints(streamSize: Long): IntStream

  def ints: IntStream

  def ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream

  def ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream

  def longs(streamSize: Long): LongStream

  def longs: LongStream

  def longs(streamSize: Long, randomNumberOrigin: Long, randomNumberBound: Long): LongStream

  def longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream

  def doubles(streamSize: Long): DoubleStream

  def doubles: DoubleStream

  def doubles(streamSize: Long, randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream

  def doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream
}

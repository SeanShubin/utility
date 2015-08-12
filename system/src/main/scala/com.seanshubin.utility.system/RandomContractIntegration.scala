package com.seanshubin.utility.system

import java.util.Random
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
class RandomContractIntegration(random: Random) extends RandomContract {
  override def nextBytes(bytes: Array[Byte]): Unit = random.nextBytes(bytes)

  override def nextBoolean: Boolean = random.nextBoolean

  override def ints: IntStream = random.ints

  override def ints(streamSize: Long): IntStream = random.ints(streamSize)

  override def ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
    random.ints(streamSize, randomNumberOrigin, randomNumberBound)

  override def ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream =
    random.ints(randomNumberOrigin, randomNumberBound)

  override def nextDouble: Double = random.nextDouble()

  override def nextLong: Long = random.nextLong()

  override def doubles(streamSize: Long): DoubleStream = random.doubles(streamSize)

  override def doubles(streamSize: Long, randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream =
    random.doubles(streamSize, randomNumberOrigin, randomNumberBound)

  override def doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream =
    random.doubles(randomNumberOrigin, randomNumberBound)

  override def doubles: DoubleStream = random.doubles()

  override def nextFloat: Float = random.nextFloat()

  override def nextGaussian: Double = random.nextGaussian()

  override def longs(streamSize: Long): LongStream = random.longs(streamSize)

  override def longs(streamSize: Long, randomNumberOrigin: Long, randomNumberBound: Long): LongStream =
    random.longs(streamSize, randomNumberOrigin, randomNumberBound)

  override def longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream =
    random.longs(randomNumberOrigin, randomNumberBound)

  override def longs: LongStream = random.longs

  override def nextInt: Int = random.nextInt()

  override def nextInt(bound: Int): Int = random.nextInt(bound)
}

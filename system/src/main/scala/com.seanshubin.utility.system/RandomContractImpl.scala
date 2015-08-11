package com.seanshubin.utility.system

import java.util.Random
import java.util.stream.{DoubleStream, IntStream, LongStream}

class RandomContractImpl(random: Random) extends RandomContract {
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

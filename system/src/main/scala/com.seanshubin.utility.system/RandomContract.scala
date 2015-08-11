package com.seanshubin.utility.system

import java.util.stream.{DoubleStream, IntStream, LongStream}

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

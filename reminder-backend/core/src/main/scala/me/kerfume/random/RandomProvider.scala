package me.kerfume.random

trait RandomProvider {
  def alphanumeric(size: Int): String
  def nextInt(): Int = nextInt(Int.MaxValue)
  def nextInt(max: Int): Int
}

object RandomProviderDefault extends RandomProvider {
  private val underlying = scala.util.Random

  override def alphanumeric(size: Int): String =
    underlying.alphanumeric.take(10).mkString
  override def nextInt(max: Int): Int = underlying.nextInt(max)
}

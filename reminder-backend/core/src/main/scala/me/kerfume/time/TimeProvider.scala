package me.kerfume.time

import java.time.OffsetDateTime

trait TimeProvider {
  def now(): OffsetDateTime
}

object TimeProviderDefault extends TimeProvider {
  override def now(): OffsetDateTime = OffsetDateTime.now()
}

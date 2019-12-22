package me.kerfume.twitter.oauth

import java.net.URLEncoder

sealed trait EncodableString {
  val value: String
  def encode: EncodableString.Encoded
}
object EncodableString {
  case class UnEncoded(value: String) extends EncodableString {
    def encode: Encoded = Encoded(
      URLEncoder.encode(value, "UTF-8")
    )
  }
  case class Encoded(value: String) extends EncodableString {
    def encode: Encoded = this
  }

  def pure(value: String): UnEncoded = UnEncoded(value)
}

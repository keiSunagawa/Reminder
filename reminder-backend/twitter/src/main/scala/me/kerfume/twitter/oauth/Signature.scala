package me.kerfume.twitter.oauth

import java.net.URLEncoder

import me.kerfume.crypt.HMac
import sttp.model.{Method, Uri}

import scala.util.chaining._

object Signature {
  type ParamKey = String

  def createSignature(
      method: Method,
      uri: Uri,
      params: Map[ParamKey, EncodableString],
      secret: Secret,
      accessTokenSecret: Option[String]
  ): EncodableString.UnEncoded = {
    val sigData = genSigData(
      methodString = EncodableString.pure(method.method).encode,
      urlString = EncodableString.pure(uri.toString).encode,
      paramsString = genParamsString(params).encode
    )
    val sigKey = accessTokenSecret match {
      case Some(ats) => secret.sigKey(ats)
      case None      => secret.sigKey
    }
    HMac.genHMACBase64(sigKey, sigData.value).pipe(EncodableString.pure)
  }

  private def genParamsString(
      params: Map[ParamKey, EncodableString]
  ): EncodableString = {
    params.toList
      .map {
        case (k, v) =>
          s"${k}=${v.encode.value}"
      }
      .sorted
      .mkString("&")
  }.pipe(EncodableString.pure)

  private def genSigData(
      methodString: EncodableString.Encoded,
      urlString: EncodableString.Encoded,
      paramsString: EncodableString.Encoded
  ): EncodableString.UnEncoded = {
    val unEncodedSigData =
      s"${methodString.value}&${urlString.value}&${paramsString.value}"
    EncodableString.pure(unEncodedSigData)
  }

  case class Secret(
      consumerApiSecret: String
  ) {
    def sigKey: String = sigKey("")
    def sigKey(accessTokenSecret: String): String =
      s"${urlEncode(consumerApiSecret)}&${accessTokenSecret}"
  }

  private def urlEncode(origin: String): String = {
    URLEncoder.encode(origin, "UTF-8")
  }
}

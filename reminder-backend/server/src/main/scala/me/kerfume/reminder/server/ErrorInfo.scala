package me.kerfume.reminder.server

import sttp.model.{StatusCode, Uri}
import sttp.tapir.{Codec, CodecFormat, EndpointOutput}

sealed trait ErrorInfo
object ErrorInfo {
  case class BadRequest(msg: String) extends ErrorInfo
  object BadRequest {
    implicit val codecPlaneText
        : Codec[BadRequest, CodecFormat.TextPlain, String] =
      Codec.stringPlainCodecUtf8.map(BadRequest(_))(_.msg)
  }
  case class Redirect(uri: Uri) extends ErrorInfo
  object Redirect {
    implicit val codecPlaneText
        : Codec[Redirect, CodecFormat.TextPlain, String] =
      Codec.stringPlainCodecUtf8.map(
        s => Redirect(Uri.parse(s).toOption.get) // FIXME unsafe
      )(_.uri.toString)
  }

  import sttp.tapir._

  def errorInfoOutput: EndpointOutput[ErrorInfo] =
    oneOf[ErrorInfo](
      statusMapping(StatusCode.BadRequest, plainBody[BadRequest]),
      statusMapping(
        StatusCode.MovedPermanently,
        header("Cache-Control", "no-cache") and header[Redirect]("Location")
      )
    )
}

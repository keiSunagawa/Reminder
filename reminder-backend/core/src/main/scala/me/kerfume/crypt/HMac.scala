package me.kerfume.crypt

import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMac {
  def genHMACBase64(sharedSecret: String, preHashString: String): String = {
    val keyBytes = sharedSecret.getBytes
    val secretKey = new SecretKeySpec(keyBytes, "HmacSHA1")

    val mac: Mac = Mac.getInstance("HmacSHA1")
    mac.init(secretKey)

    val text: Array[Byte] = preHashString.getBytes
    new String(Base64.getEncoder.encodeToString(mac.doFinal(text))).trim
  }
}

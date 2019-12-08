package me.kerfume.rpc

import io.grpc.{ManagedChannelBuilder, ManagedChannel}

object ClientSupport {
  def getChannel(host: String, port: Int): ManagedChannel =
    ManagedChannelBuilder
      .forAddress("localhost", 50051)
      .usePlaintext()
      .build()
}

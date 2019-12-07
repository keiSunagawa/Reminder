package me.kerfume
import scala.concurrent.Future

object RPCexample {
  def main(args: Array[String]) {
    if (args.isEmpty) {
      ClientRunner.run()
    } else {
      val server = ServerRunner.run()
      server.start()
      server.awaitTermination()
    }
  }
}

import com.example.protos.MyService.{GreeterGrpc, HelloRequest, HelloReply}
import io.grpc.{Server, ServerBuilder, ManagedChannelBuilder}
import scala.concurrent.ExecutionContext.global

object ServerRunner {
  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def sayHello(req: HelloRequest) = {
      val reply = HelloReply(message = "Hello " + req.name)
      Future.successful(reply)
    }
  }

  def run(): Server = {
    ServerBuilder
      .forPort(9999)
      .addService(GreeterGrpc.bindService(new GreeterImpl, global))
      .build
  }
}
object ClientRunner {
  def run(): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress("localhost", 9999)
      .usePlaintext(true)
      .build
    val request = HelloRequest(name = "World")
    val blockingStub = GreeterGrpc.blockingStub(channel)
    val reply: HelloReply = blockingStub.sayHello(request)
    println(reply)
  }
}

defmodule ReminderRpcServer do
  alias Com.Kerfume.Remind.Protos

  use GRPC.Server, service: Protos.RemindListService.Service

  @spec add(Protos.RemindOfDate.t(), GRPC.Server.Stream.t()) :: Protos.AddResult.t()
  def add(request, _stream) do
    IO.inspect(request)
    Remind.RemindList.add(request)

    Protos.AddResult.new(state: :OK)
  end

  @spec list(Google.Protobuf.Empty.t(), GRPC.Server.Stream.t()) :: Protos.RemindList.t()
  def list(_empty, _stream) do
    xs = Remind.RemindList.list
    Protos.RemindList.new(reminds: xs)
  end
end

# Define your endpoint
defmodule Reminder.Endpoint do
  use GRPC.Endpoint

  intercept GRPC.Logger.Server
  run ReminderRpcServer
end

defmodule ReminderApp do
  use Application

  def start(_type, _args) do
    import Supervisor.Spec

    children = [
      supervisor(GRPC.Server.Supervisor, [{Reminder.Endpoint, 50051}]),
      {Remind.RemindList, []}
    ]

    opts = [strategy: :one_for_one, name: ReminderApp]
    Supervisor.start_link(children, opts)
  end

  def stop(_) do
    IO.inspect "#{__MODULE__}.stop"
  end
end

# Define your endpoint
defmodule Reminder.Endpoint do
  use GRPC.Endpoint

  intercept GRPC.Logger.Server
  run ReminderRpcServer
end

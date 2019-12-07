# Define your endpoint
defmodule Helloworld.Endpoint do
  use GRPC.Endpoint

  intercept GRPC.Logger.Server
  run Helloworld.Greeter.Server
end
# In the start function of your Application
defmodule HelloworldApp do
  use Application

  def start(_type, _args) do
    import Supervisor.Spec

    children = [
      supervisor(GRPC.Server.Supervisor, [{Helloworld.Endpoint, 50051}])
    ]

    opts = [strategy: :one_for_one, name: HelloworldApp]
    Supervisor.start_link(children, opts)
  end
end

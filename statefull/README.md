# Statefull

**TODO: Add description**

## Installation

If [available in Hex](https://hex.pm/docs/publish), the package can be installed
by adding `statefull` to your list of dependencies in `mix.exs`:

```elixir
def deps do
  [
    {:statefull, "~> 0.1.0"}
  ]
end
```

Documentation can be generated with [ExDoc](https://github.com/elixir-lang/ex_doc)
and published on [HexDocs](https://hexdocs.pm). Once published, the docs can
be found at [https://hexdocs.pm/statefull](https://hexdocs.pm/statefull).


## create grpc code
```
$ docker run -it elixer-pbuf-compiler:latest bash
$ docker run -v <your_protbuf_projectdir>:/pb/ -it elixer-pbuf-compiler:latest bash
$ <in container> protoc --elixir_out=plugins=grpc:./lib/ ./protbuf/*.proto
```

## run by repl
```
$ iex -S mix
Erlang/OTP 22 [erts-10.5.5] [source] [64-bit] [smp:12:12] [ds:12:12:10] [async-threads:1] [hipe]


13:16:08.375 [info]  Running Reminder.Endpoint with Cowboy using http://0.0.0.0:50051
Interactive Elixir (1.9.4) - press Ctrl+C to exit (type h() ENTER for help)
iex(1)> Google.Protobuf.Empty.new()
%Google.Protobuf.Empty{}
iex(2)> {:ok, channel} = GRPC.Stub.connect("localhost:50051")
{:ok,
 %GRPC.Channel{
   accepted_compressors: [],
   adapter: GRPC.Adapter.Gun,
   adapter_payload: %{conn_pid: #PID<0.235.0>},
   codec: GRPC.Codec.Proto,
   compressor: nil,
   cred: nil,
   host: "localhost",
   interceptors: [],
   port: 50051,
   scheme: "http"
 }}
iex(3)> channel |> Com.Kerfume.Remind.Protos.RemindListService.Stub.list(Google.Protobuf.Empty.new())

13:16:53.343 [info]  Handled by ReminderRpcServer.list

13:16:53.344 [info]  Response :ok in 718µs
{:ok, %Com.Kerfume.Remind.Protos.RemindList{reminds: []}}
```

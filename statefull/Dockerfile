FROM elixir:1.9.4

ADD . /src

RUN cd /src && \
        mix local.hex --force && \
        mix local.rebar --force && \
        mix deps.get -y && \
        mix compile

RUN export PATH=$PATH:/root/.mix/escripts && mix escript.install hex protobuf
ENTRYPOINT cd /src && mix grpc.server

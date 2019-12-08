FROM elixir:1.9.4

RUN apt-get update && apt-get install autoconf automake libtool curl make g++ unzip

RUN git clone https://github.com/protocolbuffers/protobuf.git \
        && cd protobuf \
        && git submodule update --init --recursive \
        && ./autogen.sh

RUN cd protobuf && ./configure \
        && make \
        && make check \
        && make install \
        && ldconfig # refresh shared library cache.

# RUN mix escript.install hex protobuf
RUN echo "export PATH=$PATH:/root/.mix/escripts" >> ~/.bash_profile

ENTRYPOINT bash

defmodule Remind.RemindList do
  use GenServer, shutdown: 10_000

  @strage_path "/tmp/reminder/persistent"

  ### GenServer API

  @doc """
  GenServer.init/1コールバック
  """
  def init(_state) do
    initialList = Remind.Loader.load_list(@strage_path)
    # call terminate by receive SIGTERM
    Process.flag(:trap_exit, true)

    IO.puts("starting ReminserList process....")
    {:ok, initialList}
  end

  def terminate(_reason, state) do
    IO.puts("terminate ReminserList process....")

    Remind.Loader.save_list(@strage_path, state)

    IO.puts("terminate done.")
  end

  @doc """
  GenServer.handle_call/3コールバック
  """
  def handle_call(:list, _from, state) do
    {:reply, state, state}
  end

  def handle_call(:save, _from, state) do
    # TODO save処理中のリクエストのロック & 破棄
    Remind.Loader.save_list(@strage_path, state)
    {:reply, :ok, state}
  end


  @doc """
  GenServer.handle_cast/2コールバック
  """
  def handle_cast({:add, value}, state) do
    {:noreply, [value|state]}
  end

  ### for Client

  def start_link(state \\ []) do
    GenServer.start_link(__MODULE__, state, name: __MODULE__)
  end

  def list, do: GenServer.call(__MODULE__, :list)
  def add(value), do: GenServer.cast(__MODULE__, {:add, value})

  def save do
    IO.puts("save call...")
    GenServer.call(__MODULE__, :save)
  end
end

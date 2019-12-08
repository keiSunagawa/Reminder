defmodule Remind.RemindList do
  use GenServer

  ### GenServer API

  @doc """
  GenServer.init/1コールバック
  """
  def init(state), do: {:ok, state}

  @doc """
  GenServer.handle_call/3コールバック
  """
  def handle_call(:list, _from, state) do
    {:reply, state, state}
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
end

defmodule StatefullTest do
  use ExUnit.Case
  doctest Statefull

  test "greets the world" do
    assert Statefull.hello() == :world
  end
end

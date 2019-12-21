defmodule Remind.Loader do
  @file_name "remind_list.json"

  def load_list(path) do
    if File.exists?(full_path(path)) do
      File.read!(full_path(path))
      |> String.split("\n")
      |> Enum.filter(fn str -> String.length(str) != 0 end)
      |> Enum.map(fn json ->
        model = Poison.decode!(json, as: %Com.Kerfume.Remind.Protos.RemindOfDate{})
        if model.status != nil do
          %{model | status: status_to_atom(model.status) }
        else
          %{model | status: :TODO }
        end
      end)
    else
      []
    end
  end

  def save_list(path, xs) do
    File.mkdir_p(path)
    str = Enum.map(xs, fn x ->
      {:ok, json} = Poison.encode(x)
      json
    end) |> Enum.join("\n")
    File.open(full_path(path), [:write, :utf8], fn file ->
      IO.inspect(str)
      IO.write(file, str)
    end)
  end

  defp full_path(path), do: path <> "/" <> @file_name

  defp status_to_atom("TODO"), do: :TODO
  defp status_to_atom("RESOLVED"), do: :RESOLVED
  defp status_to_atom("UNRESOLVED"), do: :UNRESOLVED
end

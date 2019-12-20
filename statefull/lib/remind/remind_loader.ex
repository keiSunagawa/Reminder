defmodule Remind.Loader do
  @file_name "remind_list.json"

  def load_list(path) do
    if File.exists?(full_path(path)) do
      File.read!(full_path(path))
      |> String.split("\n")
      |> Enum.filter(fn str -> String.length(str) != 0 end)
      |> Enum.map(fn json -> Poison.decode!(json, as: %Com.Kerfume.Remind.Protos.RemindOfDate{}) end)
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
end

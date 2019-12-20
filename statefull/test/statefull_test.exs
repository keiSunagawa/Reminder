defmodule StatefullTest do
  use ExUnit.Case

  test "Remin.Loader" do
    expects = [
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 1, title: "task A", trigger: "2018-10-12"},
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 2, title: "task B", trigger: "2018-11-22"},
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 3, title: "日本語", trigger: "2020-01-01"}
    ]

    Remind.Loader.save_list("/tmp/kerfume/reminder", expects)
    actuals = Remind.Loader.load_list("/tmp/kerfume/reminder")
    assert actuals == expects
  end
end

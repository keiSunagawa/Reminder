defmodule StatefullTest do
  use ExUnit.Case

  test "Remin.Loader shoud save and load" do
    expects = [
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 1, title: "task A", trigger: "2018-10-12", status: :TODO},
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 2, title: "task B", trigger: "2018-11-22", status: :UNRESOLVED},
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 3, title: "日本語", trigger: "2020-01-01", status: :RESOLVED}
    ]

    Remind.Loader.save_list("/tmp/kerfume/reminder", expects)
    actuals = Remind.Loader.load_list("/tmp/kerfume/reminder")
    assert actuals == expects
  end

  test "Remin.Loader status filled by load" do
    origin = [
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 1, title: "task A", trigger: "2018-10-12"}
    ]

    expects = [
      %Com.Kerfume.Remind.Protos.RemindOfDate{seqNum: 1, title: "task A", trigger: "2018-10-12", status: :TODO}
    ]


    Remind.Loader.save_list("/tmp/kerfume/reminder", origin)
    actuals = Remind.Loader.load_list("/tmp/kerfume/reminder")
    assert actuals == expects
  end
end

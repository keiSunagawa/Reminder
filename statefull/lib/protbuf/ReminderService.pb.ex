defmodule Com.Kerfume.Remind.Protos.RemindOfDate do
  @moduledoc false
  use Protobuf, syntax: :proto3

  @type t :: %__MODULE__{
          seqNum: non_neg_integer,
          title: String.t(),
          trigger: String.t()
        }
  defstruct [:seqNum, :title, :trigger]

  field :seqNum, 1, type: :uint64
  field :title, 2, type: :string
  field :trigger, 3, type: :string
end

defmodule Com.Kerfume.Remind.Protos.AddResult do
  @moduledoc false
  use Protobuf, syntax: :proto3

  @type t :: %__MODULE__{
          state: atom | integer
        }
  defstruct [:state]

  field :state, 1, type: Com.Kerfume.Remind.Protos.ActorState, enum: true
end

defmodule Com.Kerfume.Remind.Protos.RemindList do
  @moduledoc false
  use Protobuf, syntax: :proto3

  @type t :: %__MODULE__{
          reminds: [Com.Kerfume.Remind.Protos.RemindOfDate.t()]
        }
  defstruct [:reminds]

  field :reminds, 1, repeated: true, type: Com.Kerfume.Remind.Protos.RemindOfDate
end

defmodule Com.Kerfume.Remind.Protos.ActorState do
  @moduledoc false
  use Protobuf, enum: true, syntax: :proto3

  field :OK, 0
  field :ERROR, 1
  field :PROGRESS, 2
end

defmodule Com.Kerfume.Remind.Protos.RemindListService.Service do
  @moduledoc false
  use GRPC.Service, name: "com.kerfume.remind.protos.RemindListService"

  rpc :Add, Com.Kerfume.Remind.Protos.RemindOfDate, Com.Kerfume.Remind.Protos.AddResult
  rpc :List, Google.Protobuf.Empty, Com.Kerfume.Remind.Protos.RemindList
end

defmodule Com.Kerfume.Remind.Protos.RemindListService.Stub do
  @moduledoc false
  use GRPC.Stub, service: Com.Kerfume.Remind.Protos.RemindListService.Service
end

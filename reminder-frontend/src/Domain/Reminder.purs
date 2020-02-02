module Domain.Reminder
       ( Remind
       , Reminds
       )where

import Kerfume.Prelude

type Remind = { id :: Int, title :: String, limit :: String }

type Reminds = List Remind

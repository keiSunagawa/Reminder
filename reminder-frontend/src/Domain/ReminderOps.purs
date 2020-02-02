module Domain.ReminderOps
       ( resolve
       )where

import Kerfume.Prelude
import Domain.Reminder

resolve ::  Int -> Reminds -> Reminds
resolve id xs = filter (not <<< match) xs
  where
    match x = x.id == id

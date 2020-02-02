module App.Impure
       (resolveRemind
       , fetchReminds
       ) where

import Effect
import Effect.Aff
import Effect.Exception
import Kerfume.Prelude
import Domain.Reminder

import Effect.Console (logShow)
import Effect.Random (randomInt)

resolveRemind :: Int -> Effect Unit
resolveRemind id = do
  i <- randomInt 1 10
  if i < 8 then pure unit else throwException $ error "ops"
  logShow $ "resolve id: " <> (show i) <> " ok"

fetchReminds :: Aff Reminds
fetchReminds = pure $ {id: 1, title: "a", limit: "12:00"} : {id: 2, title: "bb", limit: "15:00"} : Nil

module App.Impure
       (resolveRemind
       , fetchReminds
       ) where

import Control.Monad.Except
import Domain.Reminder
import Effect
import Effect.Aff
import Effect.Exception
import Kerfume.Prelude

import Affjax as AX
import Affjax.ResponseFormat as ResponseFormat
import Data.Bifunctor (lmap)
import Data.Generic.Rep (class Generic)
import Data.Generic.Rep.Show (genericShow)
import Data.HTTP.Method (Method(..))
import Effect.Console (logShow)
import Effect.Random (randomInt)
import Endpoint (apiEndpoint)
import Foreign.Generic (defaultOptions, genericDecodeJSON)

resolveRemind :: Int -> Effect Unit
resolveRemind id = do
  i <- randomInt 1 10
  if i < 8 then pure unit else throwException $ error "ops"
  logShow $ "resolve id: " <> (show i) <> " ok"

-- fetchReminds :: Aff Reminds
-- fetchReminds = pure $ {id: 1, title: "a", limit: "12:00"} : {id: 2, title: "bb", limit: "15:00"} : Nil

fetchReminds :: Aff Reminds
fetchReminds = do
  xs <- getList
  pure $ fromFoldable $ unwrap xs

newtype RemindJson = RemindJson { values :: Array Remind }
derive instance genericMyRecord :: Generic RemindJson _
instance showMyRecord :: Show RemindJson where show = genericShow
unwrap :: RemindJson -> Array Remind
unwrap (RemindJson x) = x.values

getList :: Aff (RemindJson)
getList = do
  res <- req
  case (getBody res) of
    Left e -> throwError e
    Right xs -> pure xs
  where
    req = AX.request AX.defaultRequest
                                       { url = apiEndpoint
                                       , method = Left GET
                                       , responseFormat = ResponseFormat.string
                                       , withCredentials = true
                                       }
    getBody :: Either AX.Error (AX.Response String) -> Either Error (RemindJson)
    getBody (Right res) = lmap raise decode0
      where
        decode0  = runExcept $ (genericDecodeJSON (defaultOptions { unwrapSingleConstructors = true }) res.body :: _ RemindJson)
        raise e = error "decode failed." -- TODO detail error
    getBody (Left e) = throwError $ error $ AX.printError e

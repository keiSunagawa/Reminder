module App.Impure
       (resolveRemind
       , fetchReminds
       ) where

import Control.Monad.Except
import Data.String.CodeUnits
import Data.String.Regex
import Data.String.Regex.Flags
import Domain.Reminder
import Effect
import Effect.Aff
import Effect.Class
import Effect.Exception
import Kerfume.Prelude

import Affjax as AX
import Affjax.ResponseFormat as ResponseFormat
import Data.String.Pattern as P
import Data.Array as A
import Data.Bifunctor (lmap)
import Data.Generic.Rep (class Generic)
import Data.Generic.Rep.Show (genericShow)
import Data.HTTP.Method (Method(..))
import Effect.Console (logShow)
import Effect.Random (randomInt)
import Endpoint (apiEndpoint)
import Foreign.Generic (defaultOptions, genericDecodeJSON)

foreign import goHref :: String -> String

resolveRemind :: Int -> Aff Unit
resolveRemind id = void $ AX.request AX.defaultRequest
                              { url = apiEndpoint <> "/" <> "resolve" <> "/" <> (show id)
                              , method = Left GET
                              , responseFormat = ResponseFormat.string
                              , withCredentials = true
                              }

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
  b <- liftEffect $ getBody res
  case b of
    Left e -> throwError e
    Right xs -> pure xs
  where
    req = AX.request AX.defaultRequest
                                       { url = apiEndpoint <> "/" <> "list"
                                       , method = Left GET
                                       , responseFormat = ResponseFormat.string
                                       , withCredentials = true
                                       }
    getBody :: Either AX.Error (AX.Response String) -> Effect (Either Error (RemindJson))
    getBody (Right res) = if contains (P.Pattern "go redirect") res.body
                          then do
                            case goRedirect res.body of
                              Right x -> logShow x
                              Left e -> throwError $ error e
                            pure $ lmap raise decode0
                          else pure $ lmap raise decode0
      where
        decode0  = runExcept $ (genericDecodeJSON (defaultOptions { unwrapSingleConstructors = true }) res.body :: _ RemindJson)
        raise e = error "decode failed." -- TODO detail error
    getBody (Left e) = throwError $ error $ AX.printError e


goRedirect str = do
  p <- r
  get p
  where
    r = regex "go redirect: (.*)" noFlags
    get p = case split p str of
      [] -> Left "none"
      xs -> case (A.index xs 1) of
        Just u -> Right (goHref u)
        Nothing -> Left "mis index"

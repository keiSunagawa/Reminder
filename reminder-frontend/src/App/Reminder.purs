module App.Reminder(component) where

import App.Impure
import Domain.Reminder
import Effect.Aff
import Kerfume.Prelude
import Reminder.HTML

import Domain.ReminderOps as RO
import Halogen as H
import Halogen.HTML as HH
import Halogen.HTML.Events as HE
import Halogen.HTML.Properties as HP

type State = { enabled :: Boolean, reminds :: Reminds }

data Action = Toggle
            | Resolve Int
            | FetchReminds

component :: forall q i o. H.Component HH.HTML q i o Aff
component =
  H.mkComponent
    { initialState
    , render
    , eval: H.mkEval $ H.defaultEval
                                     { handleAction = handleAction
                                     , initialize = Just FetchReminds
                                     }
    }

initialState :: forall i. i -> State
initialState _ =
  { enabled: false
  , reminds: Nil
  }

render :: forall m. State -> H.ComponentHTML Action () m
render state =
  let
    label = if state.enabled then "On" else "Off"
  in
   HH.div_
   [ titleHeader "./logo.png"
   , mkTable $ state.reminds
   ]



mkTable :: forall m. List Remind -> H.ComponentHTML Action () m
mkTable = table header body
  where
    header = HH.tr_ [ HH.th_ [HH.text "Title"]
                    , HH.th_ [HH.text "Limit"]
                    , HH.th_ [HH.text "Done"]
                    ]
    body :: forall w. Remind -> HH.HTML w Action
    body r = HH.tr_ [ HH.td_ [HH.text r.title]
                    , HH.td_ [HH.text r.limit]
                    , HH.td_ [infoButton "done" (\_ -> Just $ Resolve r.id)]
                    ]

handleAction ∷ forall o. Action → H.HalogenM State Action () o Aff Unit
handleAction = case _ of
  FetchReminds -> do
    rs <- H.liftAff fetchReminds
    H.modify_ \st -> st { reminds = rs }
  Toggle ->
    H.modify_ \st -> st { enabled = not st.enabled }
  Resolve i ->
    do
      H.liftAff $ resolveRemind i
      H.modify_ \st -> st { reminds = RO.resolve i st.reminds }
      pure unit


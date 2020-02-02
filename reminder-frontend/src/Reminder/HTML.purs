module Reminder.HTML
       (table
       , infoButton
       , titleHeader
       ) where

import Kerfume.Prelude

import Data.Array as A
import Halogen as H
import Halogen.HTML as HH
import Halogen.HTML.Properties as HP
import Halogen.HTML.Events as HE
import Web.UIEvent.MouseEvent (MouseEvent)

table :: forall x w i . HH.HTML w i -> (x -> HH.HTML w i) -> List x -> HH.HTML w i
table header body xs = HH.table [ className "table is-fullwidth" ]
                (A.fromFoldable $ header : (body <$> xs))

infoButton :: forall w i. String -> (MouseEvent -> Maybe i) -> HH.HTML w i
infoButton text event = HH.button
              [ HE.onClick event
              , className "button is-info"
              ]
              [ HH.text text ]

titleHeader :: forall w i. String -> HH.HTML w i
titleHeader logoPath = HH.nav
                    [ className "navbar is-primary", HP.attr (HH.AttrName "role") "navigation", HP.attr (HH.AttrName "aria-label") "main navigation"]
                    [ HH.div [ className "navbar-brand" ] [ HH.img [HP.src logoPath]  ] ]

className cn = HP.class_ (H.ClassName cn )

role :: forall r i. String -> HH.IProp r i
role = HH.prop (HH.PropName "role")

ariaLabel :: forall r i. String -> HH.IProp r i
ariaLabel = HH.prop (HH.PropName "aria-label")

{-
Welcome to a Spago project!
You can edit this file as you like.
-}
{ name =
    "reminder-frontend"
, dependencies =
    [ "console", "effect", "halogen", "psci-support", "kerfume-pure-std", "aff", "random", "exceptions", "affjax", "foreign-generic" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs", "test/**/*.purs" ]
}

# Bookiecraft

Bookiecraft is a [Bukkit](https://bukkit.org/) plugin that lets players bet on the outcomes of matches. A "match" can be anything - it could be a built-in part of a mini-game, it could be a one-off event judged by the server admins, or it could be an ad hoc bet between players. You can start and end matches using a command, or you can do it programmatically from within another plugin - it's entirely up to you to decide how to use it.

## Bookiecraft as a Standalone Plugin

It's easy to run Bookiecraft as a standalone plugin - pull the project down from GitHub, build it (using maven), then drop the Jar file into the ./plugins directory of your server.

You will then be able to manually manage your matches using the following commands:

#### Commands

- **Start a match:** */bookiecraft start {match-name} {competitor-1} {competitor-2} ...*
- **Get info on current matches:** */bookiecraft*
- **Bet on a match:** */bet {match-name} {competitor}*/
- **Close betting on a match:** */bookiecraft close {match-name}*
- **End a match and announce a winner:** */bookiecraft end {match-name} {winner}*

**Note:** the */bookiecraft* command is also aliased as */bc* 

#### Permissions

- **bookiecraft.bet** - Needed to access the */bookiecraft* and */bet* commands. Defaulted to all users.
- **bookiecraft.admin** - Needed to access the */bookiecraft start*, */bookiecraft close*, and */bookiecraft end* commands. Defaulted to ops.

## Bookiecraft as a Library

You can also use Bookiecraft as a library in your own plugins, enabling you to programmatically control your matches. For example, you might want to automatically start a match when a mini-game starts, and close the match when it ends. To do this, clone the project, add it as a dependency in your own project, deploy it to your server, and away you go.

The easiest way to work Bookiecraft into your plugin is through the BookiecraftEventHelper (uk.co.robmarch.bookiecraft.BookiecraftEventHelper) (TODO link). This class has several static helper methods which fire the events that Bookiecraft listens for, allowing you to call functions in Bookiecraft in a nice decoupled way.

## Game Mechanics

TODO SCREENSHOTS

The average player will primarily interact with the Bookiecraft plugin through the */bet* and */bc* commands. Running */bc* will show the player all the information about currently ongoing matches, including who they can bet on, and whether or not betting is currently open.

They can then bet on who they think is going to win using the */bet* command. This will open the following window:

Once they have added the items that they're willing to put on the match into the bet window, and confirmed their bet, the items will be removed from their inventory. If they win the bet, currently they will receive double of what they bet; if they lose, the items will be gone forever.

## Roadmap

- [x] Let players bet items on a match
- [ ] Change the Bukkit dependency version to the latest major release
- [ ] Give the players the odds of each bet based on how many other bets have been placed
- [ ] Weight the winnings of each bet according to the odds at the time when the bet was made
- [ ] Allow server admins to manually change the odds of a bet
- [ ] Persist the matches and bets somewhere other than in-memory

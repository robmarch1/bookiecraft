# Bookiecraft Manual Test Script

### Basic Happy Path (Win)

- /bc start match teamA teamB
- /bet match teamA
- /bc close match
- /bc end match teamA

### Basic Happy Path (Lose)

- /bc start match teamA teamB
- /bet match teamA
- /bc close match
- /bc end match teamB

### Advanced Happy Path

- /bc with one match ongoing
- /bc with multiple matches ongoing
- /bc with a match closed
- /bc with a match open
- /bc with no matches open
- Multiple bets on the same match
- Multiple bets on the same team in the same match
- Bets from the same player on different teams in the same match
- End a match that is still open for bets
- Place bet without putting down any items
- Close the betting inventory using the inventory key
- Close the betting inventory using the cancel bet button

### Bad Starts

- /bc start
- /bc start match
- /bc start match teamA
- Start multiple matches with the same name

### Bad Bets

- /bet
- /bet arg1
- /bet arg1 arg2 arg3
- Bet before a match has started
- Bet while betting is closed
- Bet on a match that has ended
- Bet on a team that is not in the match

### Bad Closes

- /bc close
- /bc close arg1 arg2
- Close bets on a match that hasn't started
- Close bets on a match that has ended
- Close bets on a match that is already closed

### Bad Ends

- /bc end
- /bc end arg1
- /bc end arg1 arg2 arg3
- Close a match with a winner who is not a competitor
- Close a match that has not started yet
- Close a match that has ended

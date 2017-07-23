package uk.co.robmarch.bookiecraft.registry;

import uk.co.robmarch.bookiecraft.model.Bet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds a registry of bets on a single match
 */
public class BetRegistry {

    private Map<String, Set<Bet>> betRegistry;

    public BetRegistry() {
        betRegistry = new HashMap<>();
    }

    /**
     * Places a given bet within the registry
     *
     * @param bet the bet to place
     * @return this bet registry
     */
    public BetRegistry placeBet(Bet bet) {
        if (!betRegistry.containsKey(bet.getCompetitor())) {
            betRegistry.put(bet.getCompetitor(), new HashSet<>());
        }
        betRegistry.get(bet.getCompetitor()).add(bet);
        return this;
    }

    /**
     * Gets the bets on a given competitor. If no bets are placed on the competitor,
     * this returns an empty set.
     *
     * @param competitor the competitor to get the bets for
     * @return the set of all bets on the competitor
     */
    public Set<Bet> getBetsFor(String competitor) {
        if (!betRegistry.containsKey(competitor)) {
            return new HashSet<>();
        }
        return betRegistry.get(competitor);
    }

    /**
     * Gets the bets against a given competitor. For example, if there is one bet on
     * Team A, one bet on Team B, and one bet on Team C, getting the bets against
     * Team B will return a set of the bets for Teams A and C.
     *
     * @param competitor the competitor to get the bets against
     * @return the set of all bets against the competitor
     */
    public Set<Bet> getBetsAgainst(String competitor) {
        return getAllBets().stream().filter(bet -> !bet.getCompetitor().equals(competitor)).collect(Collectors.toSet());
    }

    /**
     * Gets all the bets, across all competitors.
     *
     * @return all the bets currently registered
     */
    public Set<Bet> getAllBets() {
        Set<Bet> bets = new HashSet<>();
        for (String competitor : betRegistry.keySet()) {
            bets.addAll(betRegistry.get(competitor));
        }
        return bets;
    }
}

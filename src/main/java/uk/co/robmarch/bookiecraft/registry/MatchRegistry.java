package uk.co.robmarch.bookiecraft.registry;

import com.google.common.annotations.VisibleForTesting;
import uk.co.robmarch.bookiecraft.model.Match;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a registry of matches that are open (i.e. matches that have not yet been won or cancelled)
 */
public class MatchRegistry {

    private static MatchRegistry instance;

    private Map<String, Match> matchRegistry;

    private MatchRegistry() {
        this.matchRegistry = new HashMap<>();
    }

    public static MatchRegistry getInstance() {
        if (instance == null) {
            instance = new MatchRegistry();
        }
        return instance;
    }

    /**
     * Registers a match as being open to receive bets
     *
     * @param match the match to open
     * @return this match registry
     */
    public MatchRegistry register(Match match) {
        matchRegistry.put(match.getName(), match);
        return this;
    }

    /**
     * De-registers a match from being open. This should only happen when a match ends with a winner, or is cancelled.
     *
     * @param matchName the name of the match to close
     * @return this match registry
     */
    public MatchRegistry close(String matchName) {
        if (matchRegistry.containsKey(matchName)) {
            matchRegistry.remove(matchName);
        }
        return this;
    }

    /**
     * Gets a match with a given name, or null if no match exists with that name.
     *
     * @param matchName the name of the match to get
     * @return the match if it exists; otherwise, null
     */
    public Match get(String matchName) {
        if (matchRegistry.containsKey(matchName)) {
            return matchRegistry.get(matchName);
        }
        return null;
    }

    public Map<String, Match> getMatchRegistry() {
        return matchRegistry;
    }

    @VisibleForTesting
    public MatchRegistry setMatchRegistry(Map<String, Match> matchRegistry) {
        this.matchRegistry = matchRegistry;
        return this;
    }
}

package uk.co.robmarch.bookiecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import uk.co.robmarch.bookiecraft.event.BetPlacedEvent;
import uk.co.robmarch.bookiecraft.event.BettingClosedEvent;
import uk.co.robmarch.bookiecraft.event.MatchEndedEvent;
import uk.co.robmarch.bookiecraft.event.MatchStartedEvent;
import uk.co.robmarch.bookiecraft.model.Bet;

import java.util.Set;
import java.util.UUID;

/**
 * Convenience helper class to be used to trigger the Bookiecraft events programmatically
 * with a much cleaner API than having to manually fire an event
 */
public class BookiecraftEventHelper {

    /**
     * Starts a match between a given set of competitors
     *
     * @param matchName the name of the match to be started
     * @param competitors the names of the competitors to be bet on
     */
    public static void startMatch(String matchName, Set<String> competitors) {
        Bukkit.getPluginManager().callEvent(new MatchStartedEvent(matchName, competitors));
    }

    /**
     * Starts a match between a given set of competitors
     *
     * @param matchName the name of the match to be started
     * @param competitors the names of the competitors to be bet on
     */
    public static void startMatch(String matchName, String... competitors) {
        Bukkit.getPluginManager().callEvent(new MatchStartedEvent(matchName, competitors));
    }

    /**
     * Places a bet on a match
     *
     * @param matchName the name of the match to bet on
     * @param bet the bet to be made
     */
    public static void placeBet(String matchName, Bet bet) {
        Bukkit.getPluginManager().callEvent(new BetPlacedEvent(matchName, bet));
    }

    /**
     * Places a bet on a match
     *
     * @param matchName the name of the match to bet on
     * @param competitor the name of the competitor in the match to bet on
     * @param betterUUID the UUID of the player making the bet
     * @param bet what the player is betting
     */
    public static void placeBet(String matchName, String competitor, UUID betterUUID, ItemStack[] bet) {
        Bukkit.getPluginManager().callEvent(new BetPlacedEvent(matchName, new Bet(competitor, betterUUID, bet)));
    }

    /**
     * Closes betting on a given match. Note that this does not end the match - it just stops
     * the players from placing any more bets. This step is entirely optional.
     *
     * @param matchName the name of the match to close the betting on
     */
    public static void closeBetting(String matchName) {
        Bukkit.getPluginManager().callEvent(new BettingClosedEvent(matchName));
    }

    /**
     * Ends a match, announces the winner, and handles giving players their winnings
     *
     * @param matchName the name of the match to end
     * @param winner the winner of the match
     */
    public static void endMatch(String matchName, String winner) {
        Bukkit.getPluginManager().callEvent(new MatchEndedEvent(matchName, winner));
    }
}

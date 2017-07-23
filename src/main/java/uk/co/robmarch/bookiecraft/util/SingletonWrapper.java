package uk.co.robmarch.bookiecraft.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;

import java.util.Set;
import java.util.UUID;

/**
 * Wrapper class to hold calls to static methods - in particular to get the instance
 * of a singleton. This is needed in order to be able to mock out calls to things that
 * don't really exist in the context of a test.
 */
public class SingletonWrapper {

    public Server getBukkitServer() {
        return Bukkit.getServer();
    }

    public MatchRegistry getMatchRegistry() {
        return MatchRegistry.getInstance();
    }

    public void startMatch(String matchName, Set<String> competitors) {
        BookiecraftEventHelper.startMatch(matchName, competitors);
    }

    public void placeBet(String matchName, String competitor, UUID betterUUID, ItemStack[] bet) {
        BookiecraftEventHelper.placeBet(matchName, competitor, betterUUID, bet);
    }

    public void closeBetting(String matchName) {
        BookiecraftEventHelper.closeBetting(matchName);
    }

    public void endMatch(String matchName, String winner) {
        BookiecraftEventHelper.endMatch(matchName, winner);
    }
}

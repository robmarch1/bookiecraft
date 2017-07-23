package uk.co.robmarch.bookiecraft.listener;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.co.robmarch.bookiecraft.event.BettingClosedEvent;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Listens for a {@link BettingClosedEvent} and carries out all the logic needed to prevent any more players from
 * placing any more bets on a match
 */
public class BettingClosedEventListener implements Listener {

    private SingletonWrapper singletonWrapper;

    public BettingClosedEventListener() {
        singletonWrapper = new SingletonWrapper();
    }

    @EventHandler
    public void onBettingClosed(BettingClosedEvent bettingClosedEvent) {
        if (bettingClosedEvent == null
                || bettingClosedEvent.getMatchName() == null) {
            return;
        }
        Match match = singletonWrapper.getMatchRegistry().get(bettingClosedEvent.getMatchName());
        if (match == null) {
            return;
        }
        match.setBettingOpen(false);
        singletonWrapper.getBukkitServer().broadcastMessage(PlayerMessageProvider.getBettingHasClosedMessage(match.getName()));
    }

    @VisibleForTesting
    BettingClosedEventListener setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

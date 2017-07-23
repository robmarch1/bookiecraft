package uk.co.robmarch.bookiecraft.listener;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.co.robmarch.bookiecraft.event.BetPlacedEvent;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Listens for a {@link BetPlacedEvent} and carries out all the logic needed to register the bet
 */
public class BetPlacedEventListener implements Listener {

    private SingletonWrapper singletonWrapper;

    public BetPlacedEventListener() {
        singletonWrapper = new SingletonWrapper();
    }

    // TODO maybe this doesn't need to be quite so defensive...
    @EventHandler
    public void onBetPlaced(BetPlacedEvent betPlacedEvent) {
        if (betPlacedEvent == null
                || betPlacedEvent.getBet() == null
                || betPlacedEvent.getBet().getBetterUUID() == null) {
            return;
        }
        Player better = singletonWrapper.getBukkitServer().getPlayer(betPlacedEvent.getBet().getBetterUUID());
        if (better == null) {
            return;
        }
        if (betPlacedEvent.getMatchName() == null) {
            better.sendMessage(PlayerMessageProvider.getGenericError());
            return;
        }
        Match match = singletonWrapper.getMatchRegistry().get(betPlacedEvent.getMatchName());
        if (match == null
                || betPlacedEvent.getBet().getCompetitor() == null
                || !match.getCompetitors().contains(betPlacedEvent.getBet().getCompetitor())
                || betPlacedEvent.getBet().getBet() == null) {
            better.sendMessage(PlayerMessageProvider.getGenericError());
            return;
        }
        if (!match.isBettingOpen()) {
            better.sendMessage(PlayerMessageProvider.getBettingNoLongerOpen(match.getName()));
            return;
        }
        match.getBetRegistry().placeBet(betPlacedEvent.getBet());
    }

    @VisibleForTesting
    BetPlacedEventListener setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

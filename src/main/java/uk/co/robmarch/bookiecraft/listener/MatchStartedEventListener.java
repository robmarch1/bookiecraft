package uk.co.robmarch.bookiecraft.listener;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.co.robmarch.bookiecraft.event.MatchStartedEvent;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Listens for a {@link MatchStartedEvent}, and carries out all the logic needed to start up a match
 */
public class MatchStartedEventListener implements Listener {

    private SingletonWrapper singletonWrapper;

    public MatchStartedEventListener() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @EventHandler
    public void onMatchStarted(MatchStartedEvent matchStartedEvent) {
        if (matchStartedEvent == null
                || matchStartedEvent.getMatchName() == null
                || matchStartedEvent.getCompetitors() == null
                || matchStartedEvent.getCompetitors().isEmpty()
                || singletonWrapper.getMatchRegistry().get(matchStartedEvent.getMatchName()) != null) {
            return;
        }
        singletonWrapper.getMatchRegistry().register(new Match(matchStartedEvent.getMatchName(), matchStartedEvent.getCompetitors()));
        singletonWrapper.getBukkitServer().broadcastMessage(PlayerMessageProvider.getBetsNowOpen(matchStartedEvent.getMatchName()));
    }

    @VisibleForTesting
    MatchStartedEventListener setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

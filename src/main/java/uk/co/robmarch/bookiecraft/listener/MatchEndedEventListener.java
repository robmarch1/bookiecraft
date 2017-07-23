package uk.co.robmarch.bookiecraft.listener;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.co.robmarch.bookiecraft.event.MatchEndedEvent;
import uk.co.robmarch.bookiecraft.model.Bet;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Set;

/**
 * Listens for a {@link MatchEndedEvent}, and carries out all the logic needed to end a match and give out the players'
 * winnings
 */
public class MatchEndedEventListener implements Listener {


    private SingletonWrapper singletonWrapper;

    public MatchEndedEventListener() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @EventHandler
    public void onMatchEnded(MatchEndedEvent matchEndedEvent) {
        if (matchEndedEvent == null
                || matchEndedEvent.getMatchName() == null
                || matchEndedEvent.getWinner() == null) {
            return;
        }
        Match match = singletonWrapper.getMatchRegistry().get(matchEndedEvent.getMatchName());
        if (match == null
                || !match.getCompetitors().contains(matchEndedEvent.getWinner())) {
            return;
        }

        notifyAllPlayers(matchEndedEvent);

        Set<Bet> winningBets = match.getBetRegistry().getBetsFor(matchEndedEvent.getWinner());
        Set<Bet> losingBets = match.getBetRegistry().getBetsAgainst(matchEndedEvent.getWinner());

        giveWinnings(winningBets, matchEndedEvent);
        notifyLosers(losingBets);

        singletonWrapper.getMatchRegistry().close(matchEndedEvent.getMatchName());
    }

    private void notifyAllPlayers(MatchEndedEvent matchEndedEvent) {
        singletonWrapper.getBukkitServer().broadcastMessage(
                PlayerMessageProvider.getMatchOverMessage(matchEndedEvent.getWinner(), matchEndedEvent.getMatchName()));
    }

    private void giveWinnings(Set<Bet> winningBets, MatchEndedEvent matchEndedEvent) {
        for (Bet b : winningBets) {
            b.convertBetToWinings();
            Player better = singletonWrapper.getBukkitServer().getPlayer(b.getBetterUUID());
            better.getInventory().addItem(b.getBet());
            better.sendMessage(PlayerMessageProvider.getWonBetMessage(matchEndedEvent.getWinner(), b.getBetAsString()));
        }
    }

    private void notifyLosers(Set<Bet> losingBets) {
        for (Bet b : losingBets) {
            Player better = singletonWrapper.getBukkitServer().getPlayer(b.getBetterUUID());
            better.sendMessage(PlayerMessageProvider.getLostBetMessage(b.getCompetitor()));
        }
    }

    @VisibleForTesting
    MatchEndedEventListener setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

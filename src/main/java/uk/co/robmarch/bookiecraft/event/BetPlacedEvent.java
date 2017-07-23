package uk.co.robmarch.bookiecraft.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.robmarch.bookiecraft.model.Bet;

/**
 * Event to indicate that a bet should be placed on a match
 */
public class BetPlacedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String matchName;

    private final Bet bet;

    public BetPlacedEvent(String matchName, Bet bet) {
        this.matchName = matchName;
        this.bet = bet;
    }

    public String getMatchName() {
        return matchName;
    }

    public Bet getBet() {
        return bet;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

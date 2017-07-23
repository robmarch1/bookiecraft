package uk.co.robmarch.bookiecraft.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event to indicate that no more bets may be placed on a match
 */
public class BettingClosedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String matchName;

    public BettingClosedEvent(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchName() {
        return matchName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

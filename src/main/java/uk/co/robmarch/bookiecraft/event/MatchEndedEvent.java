package uk.co.robmarch.bookiecraft.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event to indicate that we should try to end a match
 */
public class MatchEndedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String matchName;

    private final String winner;

    public MatchEndedEvent(String matchName, String winner) {
        this.matchName = matchName;
        this.winner = winner;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getWinner() {
        return winner;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

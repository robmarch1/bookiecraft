package uk.co.robmarch.bookiecraft.event;

import com.google.common.collect.Sets;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Event to indicate that we should try to start a match
 */
public class MatchStartedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String matchName;

    private final Set<String> competitors;

    public MatchStartedEvent(String matchName, Set<String> competitors) {
        this.matchName = matchName;
        this.competitors = competitors;
    }

    public MatchStartedEvent(String matchName, String... competitors) {
        this(matchName, Sets.newHashSet(competitors));
    }

    public String getMatchName() {
        return matchName;
    }

    public Set<String> getCompetitors() {
        return competitors;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

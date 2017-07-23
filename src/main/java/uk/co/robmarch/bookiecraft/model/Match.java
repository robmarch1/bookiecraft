package uk.co.robmarch.bookiecraft.model;

import uk.co.robmarch.bookiecraft.registry.BetRegistry;

import java.util.Set;

/**
 * Models some sort of match which can be betted on
 */
public class Match {

    private String name;

    private Set<String> competitors;

    private BetRegistry betRegistry;

    private boolean bettingOpen;

    public Match(String name, Set<String> competitors) {
        this.name = name;
        this.competitors = competitors;
        this.betRegistry = new BetRegistry();
        this.bettingOpen = true;
    }

    public String getName() {
        return name;
    }

    public Set<String> getCompetitors() {
        return competitors;
    }

    public BetRegistry getBetRegistry() {
        return betRegistry;
    }

    public boolean isBettingOpen() {
        return bettingOpen;
    }

    public Match setBettingOpen(boolean bettingOpen) {
        this.bettingOpen = bettingOpen;
        return this;
    }
}

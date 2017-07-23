package uk.co.robmarch.bookiecraft;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.robmarch.bookiecraft.command.BetCommand;
import uk.co.robmarch.bookiecraft.command.BookiecraftCommand;
import uk.co.robmarch.bookiecraft.listener.*;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Main plugin class for the Bookiecraft Plugin
 */
public class BookiecraftPlugin extends JavaPlugin implements Listener {

    private SingletonWrapper singletonWrapper;

    public BookiecraftPlugin() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        getCommand("bookiecraft").setExecutor(new BookiecraftCommand());
        getCommand("bet").setExecutor(new BetCommand());

        getServer().getPluginManager().registerEvents(new MatchStartedEventListener(), this);
        getServer().getPluginManager().registerEvents(new BetButtonClickedEventListener(), this);
        getServer().getPluginManager().registerEvents(new BetPlacedEventListener(), this);
        getServer().getPluginManager().registerEvents(new BettingClosedEventListener(), this);
        getServer().getPluginManager().registerEvents(new MatchEndedEventListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @VisibleForTesting
    BookiecraftPlugin setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

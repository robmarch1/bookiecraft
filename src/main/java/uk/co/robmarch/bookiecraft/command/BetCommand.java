package uk.co.robmarch.bookiecraft.command;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Arrays;

/**
 * The command used to place a bet
 */
public class BetCommand implements CommandExecutor {

    private SingletonWrapper singletonWrapper;

    public BetCommand() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PlayerMessageProvider.getPlayersOnly());
            return true;
        }
        if (args == null || args.length != 2) {
            return false;
        }
        String matchName = args[0];
        String competitor = args[1];
        Match match = singletonWrapper.getMatchRegistry().get(matchName);
        if (match == null) {
            sender.sendMessage(PlayerMessageProvider.getNoMatchFound(matchName));
            return true;
        }
        if (!match.isBettingOpen()) {
            sender.sendMessage(PlayerMessageProvider.getBettingNoLongerOpen(matchName));
            return true;
        }
        if (!match.getCompetitors().contains(competitor)) {
            sender.sendMessage(PlayerMessageProvider.getCompetitorNotFound(competitor, matchName));
            for (String c : match.getCompetitors()) {
                sender.sendMessage("    - " + c);
            }
            return true;
        }

        Inventory menu = createBettingMenu((Player) sender, competitor, matchName);
        ((Player) sender).openInventory(menu);

        return true;
    }

    private Inventory createBettingMenu(Player better, String competitor, String matchName) {
        Inventory menu = singletonWrapper.getBukkitServer().createInventory(better, 45, PlayerMessageProvider.getBettingMenuTitle(matchName, competitor));

        ItemStack continueButton = new ItemStack(Material.CONCRETE, 1, (byte) 5);
        ItemMeta continueButtonMetadata = continueButton.getItemMeta();
        continueButtonMetadata.setDisplayName("Place Bet");
        continueButtonMetadata.setLore(Arrays.asList(competitor, matchName));
        continueButton.setItemMeta(continueButtonMetadata);

        ItemStack cancelButton = new ItemStack(Material.CONCRETE, 1, (byte) 14);
        ItemMeta cancelButtonMetadata = cancelButton.getItemMeta();
        cancelButtonMetadata.setDisplayName("Cancel Bet");
        cancelButton.setItemMeta(cancelButtonMetadata);

        menu.setItem(43, cancelButton);
        menu.setItem(44, continueButton);

        return menu;
    }

    @VisibleForTesting
    BetCommand setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

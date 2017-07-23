package uk.co.robmarch.bookiecraft.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Map;

/**
 * Delegate class for the `/bookiecraft` subcommand used to list all the ongoing matches
 */
class OpenMatchesCommandDelegate implements BookiecraftCommandDelegate {

    private SingletonWrapper singletonWrapper;

    OpenMatchesCommandDelegate(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<String, Match> matchRegistry = singletonWrapper.getMatchRegistry().getMatchRegistry();
        if (matchRegistry.isEmpty()) {
            sender.sendMessage(PlayerMessageProvider.getNoOngoingMatches());
            return true;
        }
        sender.sendMessage(PlayerMessageProvider.getOngoingMatches());
        for (String matchName : matchRegistry.keySet()) {
            Match match = matchRegistry.get(matchName);
            String status = match.isBettingOpen() ? "Open" : "Closed";
            sender.sendMessage(ChatColor.DARK_GREEN + "    [" + matchName + "] " + ChatColor.GREEN + "- (" + status + ")");
            for (String competitor : match.getCompetitors()) {
                sender.sendMessage(ChatColor.DARK_GREEN + "        - " + competitor);
            }
        }
        return true;
    }
}

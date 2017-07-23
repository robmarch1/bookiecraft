package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Delegate class for the `/bookiecraft start {match-name} {competitors...}` subcommand, used to start a new match
 */
class StartMatchCommandDelegate implements BookiecraftCommandDelegate {

    private final SingletonWrapper singletonWrapper;

    StartMatchCommandDelegate(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length < 4) {
            sender.sendMessage(PlayerMessageProvider.getStartIncorrectUsageMessage());
            return true;
        }
        String matchName = args[1];
        Set<String> competitors = new HashSet<>();
        competitors.addAll(Arrays.asList(args).subList(2, args.length));
        if (singletonWrapper.getMatchRegistry().get(matchName) != null) {
            sender.sendMessage(PlayerMessageProvider.getMatchAlreadyOpen(matchName));
            return true;
        }
        singletonWrapper.startMatch(matchName, competitors);
        sender.sendMessage(PlayerMessageProvider.getMatchOpenedMessage());
        return true;
    }
}

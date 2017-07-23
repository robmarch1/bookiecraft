package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Delegate class for the `/bookiecraft close {match-name}` subcommand, used to stop any more players from making any
 * more bets on a given match
 */
class CloseBettingCommandDelegate implements BookiecraftCommandDelegate {

    private final SingletonWrapper singletonWrapper;

    CloseBettingCommandDelegate(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length != 2) {
            sender.sendMessage(PlayerMessageProvider.getCloseIncorrectUsageMessage());
            return true;
        }
        String matchName = args[1];
        Match match = singletonWrapper.getMatchRegistry().get(matchName);
        if (match == null) {
            sender.sendMessage(PlayerMessageProvider.getNoMatchFound(matchName));
            return true;
        }
        if (!match.isBettingOpen()) {
            sender.sendMessage(PlayerMessageProvider.getBettingNoLongerOpen(matchName));
            return true;
        }
        singletonWrapper.closeBetting(matchName);
        sender.sendMessage(PlayerMessageProvider.getBettingClosedConfirmationMessage(matchName));
        return true;
    }
}

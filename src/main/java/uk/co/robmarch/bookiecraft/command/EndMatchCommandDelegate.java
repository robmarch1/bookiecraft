package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * Delegate class for the `/bookiecraft end {match-name} {winner}` subcommand, used to end a given match, declare a
 * winner, and give out winnings
 */
class EndMatchCommandDelegate implements BookiecraftCommandDelegate {

    private final SingletonWrapper singletonWrapper;

    EndMatchCommandDelegate(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length != 3) {
            sender.sendMessage(PlayerMessageProvider.getEndIncorrectUsageMessage());
            return true;
        }
        String matchName = args[1];
        String winner = args[2];
        Match match = singletonWrapper.getMatchRegistry().get(matchName);
        if (match == null) {
            sender.sendMessage(PlayerMessageProvider.getNoMatchFound(matchName));
            return true;
        }
        if (!match.getCompetitors().contains(winner)) {
            sender.sendMessage(PlayerMessageProvider.getInvalidWinnerMessage(winner, matchName));
            return true;
        }
        singletonWrapper.endMatch(matchName, winner);
        sender.sendMessage(PlayerMessageProvider.getMatchEndedConfirmation(matchName));
        return true;
    }
}

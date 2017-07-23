package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Delegate class, used to indicate that the `/bookiecraft` command received an unknown subcommand
 */
class ErrorMessageCommandDelegate implements BookiecraftCommandDelegate {

    private final String errorMessage;

    ErrorMessageCommandDelegate(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(errorMessage);
        return false;
    }
}

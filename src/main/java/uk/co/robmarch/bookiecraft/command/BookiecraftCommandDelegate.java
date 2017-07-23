package uk.co.robmarch.bookiecraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Interface to provide logic for each subcommand of the /bookiecraft command
 */
interface BookiecraftCommandDelegate {

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}

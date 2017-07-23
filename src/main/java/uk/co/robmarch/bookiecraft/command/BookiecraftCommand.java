package uk.co.robmarch.bookiecraft.command;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

/**
 * The command used to manage matches
 */
public class BookiecraftCommand implements CommandExecutor {

    private static final String START_MATCH_SUB_COMMAND = "start";

    private static final String CLOSE_BETTING_SUB_COMMAND = "close";

    private static final String END_MATCH_SUB_COMMAND = "end";

    private SingletonWrapper singletonWrapper;

    public BookiecraftCommand() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BookiecraftCommandDelegate commandDelegate = getCommandDelegate(sender, args);
        return commandDelegate.onCommand(sender, command, label, args);
    }

    @VisibleForTesting
    BookiecraftCommandDelegate getCommandDelegate(CommandSender sender, String[] args) {
        if (!hasAdminPrivileges(sender) || args.length == 0) {
            return new OpenMatchesCommandDelegate(singletonWrapper);
        }
        if (START_MATCH_SUB_COMMAND.equals(args[0])) {
            return new StartMatchCommandDelegate(singletonWrapper);
        }
        if (CLOSE_BETTING_SUB_COMMAND.equals(args[0])) {
            return new CloseBettingCommandDelegate(singletonWrapper);
        }
        if (END_MATCH_SUB_COMMAND.equals(args[0])) {
            return new EndMatchCommandDelegate(singletonWrapper);
        }
        return new ErrorMessageCommandDelegate(PlayerMessageProvider.getGenericError());
    }

    private boolean hasAdminPrivileges(CommandSender sender) {
        return sender.hasPermission("bookiecraft.admin");
    }

    @VisibleForTesting
    BookiecraftCommand setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

package uk.co.robmarch.bookiecraft.util;

import org.bukkit.ChatColor;

/**
 * Class to hold all of the messages that could be sent to players
 */
public class PlayerMessageProvider {

    private static final String PREFIX = "[" + ChatColor.DARK_GREEN + "Bookie" + ChatColor.GREEN + "craft" + ChatColor.WHITE + "] - ";

    private static final String GENERIC_ERROR = "Oops, something went wrong.";

    private static final String PLAYERS_ONLY = ChatColor.RED + "This command is for players only!";

    private static final String NO_MATCH_FOUND = "No match found with name {MATCH-NAME}.";

    private static final String BETTING_NO_LONGER_OPEN = "Sorry! Betting is no longer open on {MATCH-NAME}.";

    private static final String COMPETITOR_NOT_FOUND = "No competitor found called {COMPETITOR-NAME}. The competitors for {MATCH-NAME} are:";

    private static final String BET_PLACED = "Bet placed!";

    private static final String BET_CANCELLED = "Bet cancelled";

    private static final String CLOSE_INCORRECT_USAGE_MESSAGE = "To close betting on a match, use " + ChatColor.YELLOW + "/bookiecraft close {match-name}" + ChatColor.WHITE + ".";

    private static final String BETTING_CLOSED_CONFIRMATION_MESSAGE = "Successfully closed betting on {MATCH-NAME}";

    private static final String END_INCORRECT_USAGE_MESSAGE = "To end a match, use /bookiecraft end {match-name} {winner}";

    private static final String INVALID_WINNER_MESSAGE = "{WINNER-NAME} is not a competitor in {MATCH-NAME}";

    private static final String MATCH_ENDED_CONFIRMATION = "Successfully ended match {MATCH-NAME}";

    private static final String ONGOING_MATCHES = "The following matches are currently ongoing:";

    private static final String START_INCORRECT_USAGE_MESSAGE = "To open a match use /bookiecraft open {match-name} {competitor-1} {competitor-2}...";

    private static final String MATCH_ALREADY_OPEN = "A match with name {MATCH-NAME} is already open.";

    private static final String MATCH_OPENED_MESSAGE = "Match successfully opened!";

    private static final String BETTING_HAS_CLOSED_MESSAGE = "Betting has now closed on {MATCH-NAME}.";

    private static final String MATCH_OVER_MESSAGE = "{WINNER-NAME} has won {MATCH-NAME}!";

    private static final String WON_BET_MESSAGE = "Congratulations! Your bet on {WINNER-NAME} has won you {WINNINGS}!";

    private static final String LOST_BET_MESSAGE = "Your bet on {COMPETITOR-NAME} lost. Better luck next time!";

    private static final String BETS_NOW_OPEN = "Bets are now open on {MATCH-NAME}.";

    private static final String NO_ONGOING_MATCHES = "There are no matches currently ongoing.";

    public static final String BETTING_MENU_PREFIX = ChatColor.DARK_GREEN + "Bookiecraft" + ChatColor.BLACK;

    private static final String BETTING_MENU_TITLE = BETTING_MENU_PREFIX + " ({MATCH-NAME}, {COMPETITOR-NAME})";

    public static String getGenericError() {
        return PREFIX + GENERIC_ERROR;
    }

    public static String getPlayersOnly() {
        return PREFIX + PLAYERS_ONLY;
    }

    public static String getNoMatchFound(String matchName) {
        return PREFIX + NO_MATCH_FOUND.replace("{MATCH-NAME}", matchName);
    }

    public static String getBettingNoLongerOpen(String matchName) {
        return PREFIX + BETTING_NO_LONGER_OPEN.replace("{MATCH-NAME}", matchName);
    }

    public static String getCompetitorNotFound(String competitor, String matchName) {
        return PREFIX + COMPETITOR_NOT_FOUND.replace("{COMPETITOR-NAME}", competitor).replace("{MATCH-NAME}", matchName);
    }

    public static String getBetPlaced() {
        return PREFIX + BET_PLACED;
    }

    public static String getBetCancelled() {
        return PREFIX + BET_CANCELLED;
    }

    public static String getCloseIncorrectUsageMessage() {
        return PREFIX + CLOSE_INCORRECT_USAGE_MESSAGE;
    }

    public static String getBettingClosedConfirmationMessage(String matchName) {
        return PREFIX + BETTING_CLOSED_CONFIRMATION_MESSAGE.replace("{MATCH-NAME}", matchName);
    }

    public static String getEndIncorrectUsageMessage() {
        return PREFIX + END_INCORRECT_USAGE_MESSAGE;
    }

    public static String getInvalidWinnerMessage(String winner, String matchName) {
        return PREFIX + INVALID_WINNER_MESSAGE.replace("{WINNER-NAME}", winner).replace("{MATCH-NAME}", matchName);
    }

    public static String getMatchEndedConfirmation(String matchName) {
        return PREFIX + MATCH_ENDED_CONFIRMATION.replace("{MATCH-NAME}", matchName);
    }

    public static String getOngoingMatches() {
        return PREFIX + ONGOING_MATCHES;
    }

    public static String getStartIncorrectUsageMessage() {
        return PREFIX + START_INCORRECT_USAGE_MESSAGE;
    }

    public static String getMatchAlreadyOpen(String matchName) {
        return PREFIX + MATCH_ALREADY_OPEN.replace("{MATCH-NAME}", matchName);
    }

    public static String getMatchOpenedMessage() {
        return PREFIX + MATCH_OPENED_MESSAGE;
    }

    public static String getBettingHasClosedMessage(String matchName) {
        return PREFIX + BETTING_HAS_CLOSED_MESSAGE.replace("{MATCH-NAME}", matchName);
    }

    public static String getMatchOverMessage(String winner, String matchName) {
        return PREFIX + MATCH_OVER_MESSAGE.replace("{WINNER-NAME}", winner).replace("{MATCH-NAME}", matchName);
    }

    public static String getWonBetMessage(String winner, String winnings) {
        return PREFIX + WON_BET_MESSAGE.replace("{WINNER-NAME}", winner).replace("{WINNINGS}", winnings);
    }

    public static String getLostBetMessage(String competitor) {
        return PREFIX + LOST_BET_MESSAGE.replace("{COMPETITOR-NAME}", competitor);
    }

    public static String getBetsNowOpen(String matchName) {
        return PREFIX + BETS_NOW_OPEN.replace("{MATCH-NAME}", matchName);
    }

    public static String getNoOngoingMatches() {
        return PREFIX + NO_ONGOING_MATCHES;
    }

    public static String getBettingMenuTitle(String matchName, String competitorName) {
        return BETTING_MENU_TITLE.replace("{COMPETITOR-NAME}", competitorName).replace("{MATCH-NAME}", matchName);
    }
}

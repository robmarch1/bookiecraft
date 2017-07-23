package uk.co.robmarch.bookiecraft.command;

import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.HashMap;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OpenMatchesCommandDelegateTest {

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private CommandSender commandSender;

    private OpenMatchesCommandDelegate underTest;

    @Before
    public void setup() {
        given(singletonWrapper.getMatchRegistry()).willReturn(MatchRegistry.getInstance());

        underTest = new OpenMatchesCommandDelegate(singletonWrapper);
    }

    @Test
    public void shouldWarnPlayerIfThereAreNoOngoingMatches() {
        // Given
        givenThereAreNoOngoingMatches();

        // When
        underTest.onCommand(commandSender, null, null, null);

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getNoOngoingMatches());
    }

    @Test
    public void shouldListAllOngoingMatchesToPlayer() {
        // Given
        givenOpenMatchWithCompetitors("Open1", "a", "b", "c");
        givenClosedMatchWithCompetitors("Closed1", "e", "f", "g");
        givenOpenMatchWithCompetitors("Open2", "h", "i", "j");
        givenClosedMatchWithCompetitors("Closed2", "k", "l", "m");

        // When
        underTest.onCommand(commandSender, null, null, null);

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getOngoingMatches());
        thenOpenMatchIsPrintedToPlayer("Open1", "a", "b", "c");
        thenClosedMatchIsPrintedToPlayer("Closed1", "e", "f", "g");
        thenOpenMatchIsPrintedToPlayer("Open2", "h", "i", "j");
        thenClosedMatchIsPrintedToPlayer("Closed2", "k", "l", "m");
    }

    private void givenThereAreNoOngoingMatches() {
        MatchRegistry.getInstance().setMatchRegistry(new HashMap<>());
    }

    private void givenOpenMatchWithCompetitors(String matchName, String... competitors) {
        Match match = new Match(matchName, Sets.newHashSet(competitors)).setBettingOpen(true);
        MatchRegistry.getInstance().register(match);
    }

    private void givenClosedMatchWithCompetitors(String matchName, String... competitors) {
        Match match = new Match(matchName, Sets.newHashSet(competitors)).setBettingOpen(false);
        MatchRegistry.getInstance().register(match);
    }

    private void thenMessageIsSentToPlayer(String message) {
        verify(commandSender).sendMessage(message);
    }

    private void thenOpenMatchIsPrintedToPlayer(String matchName, String... competitors) {
        thenMessageIsSentToPlayer(ChatColor.DARK_GREEN + "    [" + matchName + "] " + ChatColor.GREEN + "- (Open)");
        for (String competitor : competitors) {
            thenMessageIsSentToPlayer(ChatColor.DARK_GREEN + "        - " + competitor);
        }
    }

    private void thenClosedMatchIsPrintedToPlayer(String matchName, String... competitors) {
        thenMessageIsSentToPlayer(ChatColor.DARK_GREEN + "    [" + matchName + "] " + ChatColor.GREEN + "- (Closed)");
        for (String competitor : competitors) {
            thenMessageIsSentToPlayer(ChatColor.DARK_GREEN + "        - " + competitor);
        }
    }
}
package uk.co.robmarch.bookiecraft.command;

import com.google.common.collect.Sets;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CloseBettingCommandDelegateTest {

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private CommandSender commandSender;

    private Match match;

    private CloseBettingCommandDelegate underTest;

    @Before
    public void setup() {
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);

        underTest = new CloseBettingCommandDelegate(singletonWrapper);
    }

    @Test
    public void shouldInformPlayerIfNoArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, null);

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getCloseIncorrectUsageMessage());
        thenNoMatchIsClosed();
    }

    @Test
    public void shouldInformPlayerIfTooFewArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "Hi" });

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getCloseIncorrectUsageMessage());
        thenNoMatchIsClosed();
    }

    @Test
    public void shouldInformPlayerIfTooManyArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "Too", "Many", "Args" });

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getCloseIncorrectUsageMessage());
        thenNoMatchIsClosed();
    }

    @Test
    public void shouldInformPlayerIfNoMatchCanBeFound() {
        // Given
        givenThereIsNoMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "close", "match" });

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getNoMatchFound("match"));
        thenNoMatchIsClosed();
    }

    @Test
    public void shouldInformPlayerIfBettingIsAlreadyClosed() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenBettingIsClosedOnMatch();
        givenThereIsAMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "close", "match" });

        // Then
        thenMessageIsSentToPlayer(PlayerMessageProvider.getBettingNoLongerOpen("match"));
        thenNoMatchIsClosed();
    }

    @Test
    public void shouldCLoseMatchIfPossible() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenBettingIsOpenOnMatch();
        givenThereIsAMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "close", "match" });

        // Then
        thenMatchIsClosed("match");
        thenMessageIsSentToPlayer(PlayerMessageProvider.getBettingClosedConfirmationMessage("match"));
    }

    private void givenThereIsAMatchCalled(String matchName) {
        given(mockMatchRegistry.get(matchName)).willReturn(match);
    }

    private void givenThereIsNoMatchCalled(String matchName) {
        given(mockMatchRegistry.get(matchName)).willReturn(null);
    }

    private void givenMatchWithNameAndCompetitors(String matchName, String... competitors) {
        match = new Match(matchName, Sets.newHashSet(competitors));
    }

    private void givenBettingIsOpenOnMatch() {
        match.setBettingOpen(true);
    }

    private void givenBettingIsClosedOnMatch() {
        match.setBettingOpen(false);
    }

    private void thenMessageIsSentToPlayer(String message) {
        verify(commandSender).sendMessage(message);
    }

    private void thenNoMatchIsClosed() {
        verify(singletonWrapper, never()).closeBetting(anyString());
    }

    private void thenMatchIsClosed(String matchName) {
        verify(singletonWrapper).closeBetting(matchName);
    }
}
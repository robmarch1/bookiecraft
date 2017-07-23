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
public class EndMatchCommandDelegateTest {

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private CommandSender commandSender;

    private Match match;

    private EndMatchCommandDelegate underTest;

    @Before
    public void setup() {
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);

        underTest = new EndMatchCommandDelegate(singletonWrapper);
    }

    @Test
    public void shouldWarnPlayerIfNoArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, null);

        // Then
        thenNoMatchIsEnded();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getEndIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfTooFewArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "Too", "Few" });

        // Then
        thenNoMatchIsEnded();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getEndIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfTooManyArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "One", "Too", "Many", "Args" });

        // Then
        thenNoMatchIsEnded();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getEndIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfNoMatchIsFound() {
        // Given
        givenThereIsNoMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "end", "match", "a" });

        // Then
        thenNoMatchIsEnded();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getNoMatchFound("match"));
    }

    @Test
    public void shouldWarnPlayerIfTheWinnerIsNotACompetitor() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenThereIsAMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "end", "match", "c" });

        // Then
        thenNoMatchIsEnded();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getInvalidWinnerMessage("c", "match"));
    }

    @Test
    public void shouldEndMatchIfPossible() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenThereIsAMatchCalled("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "end", "match", "a" });

        // Then
        thenMatchIsEnded("match", "a");
        thenMessageIsSentToPlayer(PlayerMessageProvider.getMatchEndedConfirmation("match"));
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

    private void thenMessageIsSentToPlayer(String message) {
        verify(commandSender).sendMessage(message);
    }

    private void thenNoMatchIsEnded() {
        verify(singletonWrapper, never()).endMatch(anyString(), anyString());
    }

    private void thenMatchIsEnded(String matchName, String winner) {
        verify(singletonWrapper).endMatch(matchName, winner);
    }
}
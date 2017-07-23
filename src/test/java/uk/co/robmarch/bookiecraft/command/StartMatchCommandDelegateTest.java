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
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StartMatchCommandDelegateTest {

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private CommandSender commandSender;

    private StartMatchCommandDelegate underTest;

    @Before
    public void setup() {
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);

        underTest = new StartMatchCommandDelegate(singletonWrapper);
    }

    @Test
    public void shouldWarnPlayerIfNoArgsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, null);

        // Then
        thenNoMatchIsStarted();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getStartIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfNoMatchNameIsProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "start" });

        // Then
        thenNoMatchIsStarted();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getStartIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfNoCompetitorsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "start", "match" });

        // Then
        thenNoMatchIsStarted();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getStartIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfNotEnoughCompetitorsAreProvided() {
        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "start", "match", "a" });

        // Then
        thenNoMatchIsStarted();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getStartIncorrectUsageMessage());
    }

    @Test
    public void shouldWarnPlayerIfMatchIsAlreadyOpen() {
        // Given
        givenMatchIsOpen("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "start", "match", "a", "b" });

        // Then
        thenNoMatchIsStarted();
        thenMessageIsSentToPlayer(PlayerMessageProvider.getMatchAlreadyOpen("match"));
    }

    @Test
    public void shouldStartMatchIfPossible() {
        // Given
        givenNoMatchIsOpen("match");

        // When
        underTest.onCommand(commandSender, null, null, new String[]{ "start", "match", "a", "b" });

        // Then
        thenMatchIsStarted("match", "a", "b");
        thenMessageIsSentToPlayer(PlayerMessageProvider.getMatchOpenedMessage());
    }

    private void givenMatchIsOpen(String matchName) {
        Match match = new Match(matchName, Sets.newHashSet("a", "b"));
        given(mockMatchRegistry.get(matchName)).willReturn(match);
    }

    private void givenNoMatchIsOpen(String matchName) {
        given(mockMatchRegistry.get(matchName)).willReturn(null);
    }

    private void thenMessageIsSentToPlayer(String message) {
        verify(commandSender).sendMessage(message);
    }

    private void thenNoMatchIsStarted() {
        verify(singletonWrapper, never()).startMatch(anyString(), anySetOf(String.class));
    }

    private void thenMatchIsStarted(String matchName, String... competitors) {
        verify(singletonWrapper).startMatch(matchName, Sets.newHashSet(competitors));
    }

}
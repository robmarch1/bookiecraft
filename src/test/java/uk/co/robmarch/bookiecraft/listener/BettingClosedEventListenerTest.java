package uk.co.robmarch.bookiecraft.listener;

import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.event.BettingClosedEvent;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BettingClosedEventListenerTest {

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private Server mockBukkitServer;

    @Mock
    private MatchRegistry mockedMatchRegistry;

    private BettingClosedEventListener underTest;

    @Before
    public void setUp() {
        given(singletonWrapper.getBukkitServer()).willReturn(mockBukkitServer);
        given(singletonWrapper.getMatchRegistry()).willReturn(mockedMatchRegistry);
        underTest = new BettingClosedEventListener().setSingletonWrapper(singletonWrapper);
    }

    @Test
    public void shouldNotCloseTheMatchIfTheEventIsNull() {
        // When
        underTest.onBettingClosed(null);

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotCloseTheMatchIfTheMatchNameOnTheEventIsNull() {
        // When
        underTest.onBettingClosed(new BettingClosedEvent(null));

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotCloseTheMatchIfTheMatchNameIsNotRegistered() {
        // Given
        Map<String, Match> matchRegistry = new HashMap<>();
        matchRegistry.put("Another match", new Match("Another match", Sets.newHashSet("a", "b")));
        given(mockedMatchRegistry.getMatchRegistry()).willReturn(matchRegistry);
        given(mockedMatchRegistry.get("A match")).willReturn(null);

        // When
        underTest.onBettingClosed(new BettingClosedEvent("A match"));

        // Then
        verify(mockedMatchRegistry).get("A match");
        assertThat(mockedMatchRegistry.getMatchRegistry().get("Another match").isBettingOpen(), is(true));
    }

    @Test
    public void shouldCloseBettingOnAnOpenMatch() {
        // Given
        Match match = new Match("A match", Sets.newHashSet("a", "b"));
        match.setBettingOpen(true);
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        // When
        underTest.onBettingClosed(new BettingClosedEvent("A match"));

        // Then
        assertThat(match.isBettingOpen(), is(false));
        verify(mockBukkitServer).broadcastMessage(PlayerMessageProvider.getBettingHasClosedMessage("A match"));
    }
}
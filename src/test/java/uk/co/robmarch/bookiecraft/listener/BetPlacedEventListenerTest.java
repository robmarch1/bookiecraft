package uk.co.robmarch.bookiecraft.listener;

import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.event.BetPlacedEvent;
import uk.co.robmarch.bookiecraft.model.Bet;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BetPlacedEventListenerTest {

    private static final ItemStack[] BET = new ItemStack[1];

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private Server mockedServer;

    @Mock
    private Player mockedPlayer;

    @Mock
    private MatchRegistry mockedMatchRegistry;

    private BetPlacedEventListener underTest;

    @Before
    public void setUp() {
        given(singletonWrapper.getBukkitServer()).willReturn(mockedServer);
        given(singletonWrapper.getMatchRegistry()).willReturn(mockedMatchRegistry);
        underTest = new BetPlacedEventListener().setSingletonWrapper(singletonWrapper);
    }

    @Test
    public void shouldNotPlaceABetIfTheEventIsNull() {
        // When
        underTest.onBetPlaced(null);

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotPlaceABetIfTheEventHasNoBet() {
        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", null));

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotPlaceABetIfTheBetHasNoBetterUUID() {
        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 1", null, BET)));

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotPlaceABetIfTheBetterUUIDDoesNotCorrespondToAPlayer() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(null);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 1", betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
    }

    @Test
    public void shouldNotPlaceBetIfTheMatchNameIsNull() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);

        // When
        underTest.onBetPlaced(new BetPlacedEvent(null, new Bet("Team 1", betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, never()).get(anyString());
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getGenericError());
    }

    @Test
    public void shouldNotPlaceBetIfTheMatchNameIsNotRegistered() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        given(mockedMatchRegistry.get("A match")).willReturn(null);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 1", betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getGenericError());
    }

    @Test
    public void shouldNotPlaceBetIfBettingIsNotOpen() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        Match match = new Match("A match", Sets.newHashSet("Team 1", "Team 2"));
        match.setBettingOpen(false);
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 1", betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        assertThat(match.getBetRegistry().getAllBets(), empty());
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getBettingNoLongerOpen(match.getName()));
    }

    @Test
    public void shouldNotPlaceBetIfThereIsNoCompetitor() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        Match match = new Match("A match", Sets.newHashSet("Team 1", "Team 2"));
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet(null, betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        assertThat(match.getBetRegistry().getAllBets(), empty());
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getGenericError());
    }

    @Test
    public void shouldNotPlaceBetIfNothingHasBeenBet() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        Match match = new Match("A match", Sets.newHashSet("Team 1", "Team 2"));
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 1", betterUUID, null)));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        assertThat(match.getBetRegistry().getAllBets(), empty());
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getGenericError());
    }

    @Test
    public void shouldNotPlaceBetIfCompetitorDoesNotExist() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        Match match = new Match("A match", Sets.newHashSet("Team 1", "Team 2"));
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", new Bet("Team 3", betterUUID, BET)));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        assertThat(match.getBetRegistry().getAllBets(), empty());
        verify(mockedPlayer).sendMessage(PlayerMessageProvider.getGenericError());
    }

    @Test
    public void shouldPlaceBetIfWeHaveEnoughDataToActuallyPlaceABet() {
        // Given
        UUID betterUUID = new UUID(10, 10);
        given(mockedServer.getPlayer(betterUUID)).willReturn(mockedPlayer);
        Match match = new Match("A match", Sets.newHashSet("Team 1", "Team 2"));
        given(mockedMatchRegistry.get("A match")).willReturn(match);

        Bet bet = new Bet("Team 1", betterUUID, BET);

        // When
        underTest.onBetPlaced(new BetPlacedEvent("A match", bet));

        // Then
        verify(mockedMatchRegistry, times(1)).get("A match");
        assertThat(match.getBetRegistry().getAllBets(), contains(bet));
        verify(mockedPlayer, never()).sendMessage(anyString());
    }
}
package uk.co.robmarch.bookiecraft.listener;

import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.event.MatchEndedEvent;
import uk.co.robmarch.bookiecraft.model.Bet;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MatchEndedEventListenerTest {

    private static final UUID PLAYER_A_UUID = new UUID(10L, 10L);

    private static final UUID PLAYER_B_UUID = new UUID(20L, 20L);

    private static final UUID PLAYER_C_UUID = new UUID(30L, 30L);

    private static final ItemStack[] BET = new ItemStack[] {};

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private Server mockBukkitServer;

    @Mock
    private Player mockedPlayerA;

    @Mock
    private PlayerInventory mockedInventoryA;

    @Mock
    private Player mockedPlayerB;

    @Mock
    private PlayerInventory mockedInventoryB;

    @Mock
    private Player mockedPlayerC;

    @Mock
    private PlayerInventory mockedInventoryC;

    private MatchEndedEventListener underTest;

    @Before
    public void setUp() {
        given(singletonWrapper.getBukkitServer()).willReturn(mockBukkitServer);
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);
        given(mockBukkitServer.getPlayer(PLAYER_A_UUID)).willReturn(mockedPlayerA);
        given(mockBukkitServer.getPlayer(PLAYER_B_UUID)).willReturn(mockedPlayerB);
        given(mockBukkitServer.getPlayer(PLAYER_C_UUID)).willReturn(mockedPlayerC);
        given(mockedPlayerA.getInventory()).willReturn(mockedInventoryA);
        given(mockedPlayerB.getInventory()).willReturn(mockedInventoryB);
        given(mockedPlayerC.getInventory()).willReturn(mockedInventoryC);
        underTest = new MatchEndedEventListener().setSingletonWrapper(singletonWrapper);
    }

    @Test
    public void shouldNotEndMatchIfTheEventIsNotSent() {
        // When
        underTest.onMatchEnded(null);

        // Then
        verify(mockMatchRegistry, never()).close(anyString());
    }

    @Test
    public void shouldNotEndMatchIfTheNameOfTheMatchToEndIsNotProvided() {
        // When
        underTest.onMatchEnded(new MatchEndedEvent(null, "a"));

        // Then
        verify(mockMatchRegistry, never()).close(anyString());
    }

    @Test
    public void shouldNotEndMatchIfTheNameOfTheWinnerIsNotProvided() {
        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", null));

        // Then
        verify(mockMatchRegistry, never()).close(anyString());
    }

    @Test
    public void shouldNotEndMatchIfTheMatchToEndIsNotOpen() {
        // Given
        given(mockMatchRegistry.get("A Match")).willReturn(null);

        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", "a"));

        // Then
        verify(mockMatchRegistry, never()).close(anyString());
    }

    @Test
    public void shouldNotEndMatchIfTheWinnerIsNotOneOfTheCompetitors() {
        // Given
        Match match = new Match("A Match", Sets.newHashSet("a", "b", "c"));
        given(mockMatchRegistry.get("A Match")).willReturn(match);

        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", "d"));

        // Then
        verify(mockMatchRegistry, never()).close(anyString());
    }

    @Test
    public void shouldEndTheMatchIfWeHaveEnoughInformationToEndAMatch() {
        // Given
        Match match = new Match("A Match", Sets.newHashSet("a", "b", "c"));
        given(mockMatchRegistry.get("A Match")).willReturn(match);

        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", "a"));

        // Then
        verify(mockBukkitServer).broadcastMessage(PlayerMessageProvider.getMatchOverMessage("a", "A Match"));
        verify(mockMatchRegistry).close("A Match");
    }

    @Test
    public void shouldInformWinnersOnSuccessfullyClosingAMatch() {
        // Given
        Match match = new Match("A Match", Sets.newHashSet("a", "b", "c"));
        match.getBetRegistry().placeBet(new Bet("a", PLAYER_A_UUID, BET));
        match.getBetRegistry().placeBet(new Bet("a", PLAYER_B_UUID, BET));
        match.getBetRegistry().placeBet(new Bet("b", PLAYER_C_UUID, BET));
        given(mockMatchRegistry.get("A Match")).willReturn(match);

        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", "a"));

        // Then
        verify(mockedPlayerA).sendMessage(expectedWinMessage("a"));
        verify(mockedPlayerB).sendMessage(expectedWinMessage("a"));
        verify(mockedPlayerC, never()).sendMessage(expectedWinMessage("b"));
    }

    @Test
    public void shouldInformLosersOnSuccessfullyClosingAMatch() {
        // Given
        Match match = new Match("A Match", Sets.newHashSet("a", "b", "c"));
        match.getBetRegistry().placeBet(new Bet("a", PLAYER_A_UUID, BET));
        match.getBetRegistry().placeBet(new Bet("b", PLAYER_B_UUID, BET));
        match.getBetRegistry().placeBet(new Bet("c", PLAYER_C_UUID, BET));
        given(mockMatchRegistry.get("A Match")).willReturn(match);

        // When
        underTest.onMatchEnded(new MatchEndedEvent("A Match", "a"));

        // Then
        verify(mockedPlayerA, never()).sendMessage(expectedLoseMessage("a"));
        verify(mockedPlayerB).sendMessage(expectedLoseMessage("b"));
        verify(mockedPlayerC).sendMessage(expectedLoseMessage("c"));
    }

    private String expectedWinMessage(String winner) {
        return PlayerMessageProvider.getWonBetMessage(winner, "");
    }

    private String expectedLoseMessage(String competitor) {
        return PlayerMessageProvider.getLostBetMessage(competitor);
    }

}
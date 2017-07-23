package uk.co.robmarch.bookiecraft.command;

import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.model.Match;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BetCommandTest {

    @Mock
    private Server mockServer;

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private SingletonWrapper singletonWrapper;

    @Mock
    private Player commandSender;

    @Mock
    private Inventory bettingInventory;

    private Match match;

    private BetCommand underTest;

    @Before
    public void setup() {
        given(singletonWrapper.getBukkitServer()).willReturn(mockServer);
        given(singletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);
        given(mockServer.createInventory(eq(commandSender), eq(45), anyString())).willReturn(bettingInventory);

        underTest = new BetCommand().setSingletonWrapper(singletonWrapper);
    }

    @Test
    public void shouldNotLetANonPlayerMakeBets() {
        // Given
        CommandSender horse = mock(Horse.class);

        // When
        boolean isValidCommand = underTest.onCommand(horse, null, null, null);

        // Then
        assertThat(isValidCommand, is(true));
        verify(horse).sendMessage(PlayerMessageProvider.getPlayersOnly());
    }

    @Test
    public void shouldShowHelpTextWhenThereAreNoCommandArgs() {
        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, null);

        // Then
        assertThat(isValidCommand, is(false));
        thenNoInventoryIsOpened();
    }

    @Test
    public void shouldShowHelpTextWhenThereAreTooFewCommandArgs() {
        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "Hi" });

        // Then
        assertThat(isValidCommand, is(false));
        thenNoInventoryIsOpened();
    }

    @Test
    public void shouldShowHelpTextWhenThereAreTooManyCommandArgs() {
        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "Too", "Many", "Args" });

        // Then
        assertThat(isValidCommand, is(false));
        thenNoInventoryIsOpened();
    }

    @Test
    public void shouldInformUserIfNoMatchIsFound() {
        // Given
        givenMatchWithNameAndCompetitors("match1", "a", "b");
        givenThereIsNoMatchCalled("match2");

        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "match2", "a"});

        // Then
        assertThat(isValidCommand, is(true));
        thenPlayerShouldBeSentMessages(PlayerMessageProvider.getNoMatchFound("match2"));
    }

    @Test
    public void shouldInformUserIfBettingIsClosedOnTheMatch() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenBettingIsClosedOnMatch();
        givenThereIsAMatchCalled("match");

        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "match", "a"});

        // Then
        assertThat(isValidCommand, is(true));
        thenPlayerShouldBeSentMessages(PlayerMessageProvider.getBettingNoLongerOpen("match"));
    }

    @Test
    public void shouldListTheCompetitorsToThePlayerIfTheirPlayerIsNotInTheMatch() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenBettingIsOpenOnMatch();
        givenThereIsAMatchCalled("match");

        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "match", "c"});

        // Then
        assertThat(isValidCommand, is(true));
        thenPlayerShouldBeSentMessages(
                PlayerMessageProvider.getCompetitorNotFound("c", "match"),
                "    - a",
                "    - b");
    }

    @Ignore("This can't be tested - production code calls ItemStack#getItemMeta() which calls a static method, causing an NPE")
    @Test
    public void shouldOpenBettingInventoryMenuWhenTheBetCanBePlaced() {
        // Given
        givenMatchWithNameAndCompetitors("match", "a", "b");
        givenBettingIsOpenOnMatch();
        givenThereIsAMatchCalled("match");

        // When
        boolean isValidCommand = underTest.onCommand(commandSender, null, null, new String[]{ "match", "a"});

        // Then
        assertThat(isValidCommand, is(true));
        thenBettingInventoryHasCancelButton();
        thenBettingInventoryHasPlaceBetButton("a", "match");
        thenBettingInventoryIsOpened();
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

    private void thenNoInventoryIsOpened() {
        verify(commandSender, never()).openInventory(any(Inventory.class));
    }

    private void thenPlayerShouldBeSentMessages(String... messages) {
        for (String message : messages) {
            verify(commandSender).sendMessage(message);
        }
    }

    private void thenBettingInventoryIsOpened() {
        verify(commandSender).openInventory(bettingInventory);
    }

    private void thenBettingInventoryHasCancelButton() {
        ArgumentCaptor<ItemStack> cancelButtonCaptor = ArgumentCaptor.forClass(ItemStack.class);
        verify(bettingInventory).setItem(eq(43), cancelButtonCaptor.capture());
        assertThat(cancelButtonCaptor.getValue().getItemMeta().getDisplayName(), is("Cancel Bet"));
    }

    private void thenBettingInventoryHasPlaceBetButton(String competitor, String matchName) {
        ArgumentCaptor<ItemStack> placeBetButtonCaptor = ArgumentCaptor.forClass(ItemStack.class);
        verify(bettingInventory).setItem(eq(44), placeBetButtonCaptor.capture());
        assertThat(placeBetButtonCaptor.getValue().getItemMeta().getDisplayName(), is("Place Bet"));
        assertThat(placeBetButtonCaptor.getValue().getItemMeta().getLore(), contains(competitor, matchName));
    }

}
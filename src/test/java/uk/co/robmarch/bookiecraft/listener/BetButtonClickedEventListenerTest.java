package uk.co.robmarch.bookiecraft.listener;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.robmarch.bookiecraft.registry.MatchRegistry;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BetButtonClickedEventListenerTest {

    private static final UUID BETTER_UUID = new UUID(10L, 20L);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SingletonWrapper mockSingletonWrapper;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private MatchRegistry mockMatchRegistry;

    @Mock
    private InventoryClickEvent inventoryClickEvent;

    @Mock
    private Inventory inventory;

    @Mock
    private ItemStack currentItem;

    @Mock
    private ItemMeta currentItemMeta;

    @Mock
    private HumanEntity better;

    @Mock
    private PlayerInventory betterInventory;

    private BetButtonClickedEventListener underTest;

    @Before
    public void setup() {
        given(mockSingletonWrapper.getBukkitServer()).willReturn(mockServer);
        given(mockServer.getPluginManager()).willReturn(mockPluginManager);
        given(mockServer.getItemFactory()).willReturn(mock(ItemFactory.class));
        given(mockSingletonWrapper.getMatchRegistry()).willReturn(mockMatchRegistry);
        given(inventoryClickEvent.getInventory()).willReturn(inventory);
        given(inventoryClickEvent.getWhoClicked()).willReturn(better);
        given(better.getUniqueId()).willReturn(BETTER_UUID);
        given(better.getInventory()).willReturn(betterInventory);
        underTest = new BetButtonClickedEventListener().setSingletonWrapper(mockSingletonWrapper);
    }

    @Test
    public void shouldNotDoAnythingIfTheInventoryIsNotTheBookiecraftBettingUI() {
        // Given
        givenInventoryIsCalled("inventory");

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();
    }

    @Test
    public void shouldNotDoAnythingIfNoItemWasClicked() {
        // Given
        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(null);

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();
    }

    @Test
    public void shouldNotDoAnythingIfTheClickedItemHasNoMeta() {
        // Given
        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(null);

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();
    }

    @Test
    public void shouldNotDoAnythingIfANonUIItemIsClicked() {
        // Given
        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(currentItemMeta);
        givenClickedItemHasName("Not A Bookiecraft UI Item");

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfPlaceBetButtonDoesNotHaveLore() {
        // Given
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Incorrect lore from place bet button!");

        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(currentItemMeta);
        givenClickedItemHasName("Place Bet");
        givenClickedItemHasNoLore();

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();

        // Then IllegalStateException is thrown
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfPlaceBetButtonHasTooMuchLore() {
        // Given
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Incorrect lore from place bet button!");

        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(currentItemMeta);
        givenClickedItemHasName("Place Bet");
        givenClickedItemHasLore("Lore A", "Lore B", "Lore C");

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenNoBetIsCancelled();

        // Then IllegalStateException is thrown
    }

    @Test
    public void shouldAttemptToPlaceBetIfPossible() {
        // Given
        ItemStack[] bet = new ItemStack[]{ aStackOf(Material.CONCRETE, 2), aStackOf(Material.ACACIA_FENCE, 3) };
        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(currentItemMeta);
        givenClickedItemHasName("Place Bet");
        givenClickedItemHasLore("Competitor Name", "Match Name");
        givenBettingInventoryContains(bet);

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenBetIsPlaced("Match Name", "Competitor Name", BETTER_UUID, bet);
        thenBettingInventoryWasClosed();
    }

    @Test
    public void shouldAttemptToReturnItemsToUserOnTheNextTickWhenCancellingTheBet() {
        // Given
        ItemStack[] bet = new ItemStack[]{ aStackOf(Material.CONCRETE, 2), aStackOf(Material.ACACIA_FENCE, 3) };
        givenInventoryIsCalled(PlayerMessageProvider.getBettingMenuTitle("Match", "Competitor"));
        givenClickedItemIs(currentItem);
        givenClickedItemHasMeta(currentItemMeta);
        givenClickedItemHasName("Cancel Bet");
        givenClickedItemHasLore("Competitor Name", "Match Name");
        givenBettingInventoryContains(bet);

        // When
        underTest.onBetButtonClicked(inventoryClickEvent);

        // Then
        thenNoBetIsPlaced();
        thenBetWasCancelled(bet);
    }

    private void givenInventoryIsCalled(String inventoryName) {
        given(inventory.getName()).willReturn(inventoryName);
    }

    private void givenClickedItemIs(ItemStack clickedItem) {
        given(inventoryClickEvent.getCurrentItem()).willReturn(clickedItem);
    }

    private void givenClickedItemHasMeta(ItemMeta itemMeta) {
        given(currentItem.getItemMeta()).willReturn(itemMeta);
    }

    private void givenClickedItemHasName(String name) {
        given(currentItemMeta.getDisplayName()).willReturn(name);
    }

    private void givenClickedItemHasNoLore() {
        given(currentItemMeta.getLore()).willReturn(null);
    }

    private void givenClickedItemHasLore(String... lore) {
        given(currentItemMeta.getLore()).willReturn(Arrays.asList(lore));
    }

    private void givenBettingInventoryContains(ItemStack... bet) {
        given(inventory.getContents()).willReturn(bet);
    }

    private ItemStack aStackOf(Material material, int amount) {
        ItemStack itemStack = mock(ItemStack.class);
        given(itemStack.getType()).willReturn(material);
        given(itemStack.getAmount()).willReturn(amount);
        return itemStack;
    }

    private void thenNoBetIsPlaced() {
        verify(mockSingletonWrapper, never()).placeBet(anyString(), anyString(), any(UUID.class), any(ItemStack[].class));
    }

    private void thenNoBetIsCancelled() {
        verify(better, never()).sendMessage(PlayerMessageProvider.getBetCancelled());
    }

    private void thenBetIsPlaced(String matchName, String competitor, UUID betterUUID, ItemStack... bet) {
        // TODO fix this
        verify(mockSingletonWrapper).placeBet(matchName, competitor, betterUUID, bet);
    }

    private void thenBettingInventoryWasClosed() {
        verify(better).closeInventory();
    }

    private void thenBetWasCancelled(ItemStack... bet) {
        verify(betterInventory).addItem(bet);
        verify(better).closeInventory();
    }
}
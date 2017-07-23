package uk.co.robmarch.bookiecraft.listener;

import com.google.common.annotations.VisibleForTesting;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import uk.co.robmarch.bookiecraft.util.PlayerMessageProvider;
import uk.co.robmarch.bookiecraft.util.SingletonWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Listens for a click on any of the buttons in the betting inventory, and responds appropriately by either cancelling
 * the bet, or placing the bet
 */
public class BetButtonClickedEventListener implements Listener {

    private static final String PLACE_BET_BUTTON = "Place Bet";

    private static final String CANCEL_BET_BUTTON = "Cancel Bet";

    private SingletonWrapper singletonWrapper;

    public BetButtonClickedEventListener() {
        this.singletonWrapper = new SingletonWrapper();
    }

    @EventHandler
    public void onBetButtonClicked(InventoryClickEvent inventoryClickEvent) {
        if (hasClicked(PLACE_BET_BUTTON, inventoryClickEvent)) {
            placeBet(inventoryClickEvent);
            inventoryClickEvent.setCancelled(true);
        } else if (hasClicked(CANCEL_BET_BUTTON, inventoryClickEvent)) {
            cancelBet(inventoryClickEvent);
            inventoryClickEvent.setCancelled(true);
        }
    }

    private boolean hasClicked(String buttonName, InventoryClickEvent inventoryClickEvent) {
        ItemStack clicked = inventoryClickEvent.getCurrentItem();
        return isBettingInventory(inventoryClickEvent)
                && clicked != null
                && clicked.getItemMeta() != null
                && buttonName.equals(clicked.getItemMeta().getDisplayName());
    }

    private boolean isBettingInventory(InventoryClickEvent inventoryClickEvent) {
        return inventoryClickEvent.getInventory().getName().contains(PlayerMessageProvider.BETTING_MENU_PREFIX);
    }

    private void placeBet(InventoryClickEvent inventoryClickEvent) {
        List<String> itemLore = inventoryClickEvent.getCurrentItem().getItemMeta().getLore();
        if (itemLore == null || itemLore.size() != 2) {
            throw new IllegalStateException("Incorrect lore from place bet button!");
        }
        String competitor = itemLore.get(0);
        String matchName = itemLore.get(1);
        singletonWrapper.placeBet(matchName, competitor, inventoryClickEvent.getWhoClicked().getUniqueId(), getBettedItems(inventoryClickEvent));
        closeInventory(inventoryClickEvent);
        inventoryClickEvent.getWhoClicked().sendMessage(PlayerMessageProvider.getBetPlaced());
    }

    private ItemStack[] getBettedItems(InventoryClickEvent inventoryClickEvent) {
        ItemStack[] items = inventoryClickEvent.getInventory().getContents();
        List<ItemStack> itemList = Arrays.stream(items)
                .filter(itemStack -> itemStack != null && !Material.AIR.equals(itemStack.getType()))
                .filter(itemStack -> itemStack.getItemMeta() == null || !PLACE_BET_BUTTON.equals(itemStack.getItemMeta().getDisplayName()))
                .filter(itemStack -> itemStack.getItemMeta() == null || !CANCEL_BET_BUTTON.equals(itemStack.getItemMeta().getDisplayName()))
                .collect(Collectors.toList());
        ItemStack[] bettedItems = new ItemStack[itemList.size()];
        return itemList.toArray(bettedItems);
    }

    private void closeInventory(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.getWhoClicked().closeInventory();
    }

    private void cancelBet(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.getWhoClicked().getInventory().addItem(getBettedItems(inventoryClickEvent));
        closeInventory(inventoryClickEvent);
        inventoryClickEvent.getWhoClicked().sendMessage(PlayerMessageProvider.getBetCancelled());
    }

    @VisibleForTesting
    BetButtonClickedEventListener setSingletonWrapper(SingletonWrapper singletonWrapper) {
        this.singletonWrapper = singletonWrapper;
        return this;
    }
}

package uk.co.robmarch.bookiecraft.model;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

/**
 * Models a bet made by a player
 */
public class Bet {

    private String competitor;

    private UUID betterUUID;

    private ItemStack[] bet;

    public Bet(String competitor, UUID betterUUID, ItemStack[] bet) {
        this.competitor = competitor;
        this.betterUUID = betterUUID;
        this.bet = bet;
    }

    public String getCompetitor() {
        return competitor;
    }

    public UUID getBetterUUID() {
        return betterUUID;
    }

    public ItemStack[] getBet() {
        return bet;
    }

    // TODO find a way to get rid of this state mutability
    public void convertBetToWinings() {
        Arrays.stream(bet)
                .filter(itemStack -> itemStack != null)
                .forEach(itemStack -> itemStack.setAmount(getWinningsAmount(itemStack)));
    }

    private int getWinningsAmount(ItemStack itemStack) {
        if (itemStack.getAmount() * 2 > itemStack.getMaxStackSize()) {
            // TODO excess should be given in a new item stack somehow
            return itemStack.getMaxStackSize();
        }
        return itemStack.getAmount() * 2;
    }

    public String getBetAsString() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(bet).forEach(itemStack -> sb.append(getItemStackString(itemStack)));
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    private String getItemStackString(ItemStack itemStack) {
        String itemName = itemStack.getType().name();
        if (itemStack.getItemMeta() != null && StringUtils.isNotBlank(itemStack.getItemMeta().getDisplayName())) {
            itemName = itemStack.getItemMeta().getDisplayName().toUpperCase();
        }
        return itemStack.getAmount() + "x" + itemName + ", ";
    }
}

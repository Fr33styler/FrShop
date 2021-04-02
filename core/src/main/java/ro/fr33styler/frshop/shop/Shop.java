package ro.fr33styler.frshop.shop;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.fr33styler.frshop.provider.EconomyProvider;
import ro.fr33styler.frshop.shop.types.ClickType;
import ro.fr33styler.frshop.shop.types.ShopType;
import ro.fr33styler.frshop.shop.types.TradeType;

import java.util.UUID;

public interface Shop {

    Block getBlock();

    UUID getUUID();

    void setUUID(UUID uuid);

    String getName();

    void setName(String name);

    int getAmount();

    void setAmount(int amount);

    double getPrice();

    void setPrice(double price);

    ItemStack getItemStack();

    void setItemStack(ItemStack itemStack);

    TradeType getTradeType();

    void setTradeType(TradeType tradeType);

    Block[] getProtectedBlocks();

    void setProtectedBlocks(Block... block);

    EconomyProvider getEconomyProvider();

    void setEconomyProvider(EconomyProvider economyProvider);

    ShopType getType();

    void refreshSign();

    boolean canInteract(Player p);

    void onClick(ClickType clickType, Player p);

}
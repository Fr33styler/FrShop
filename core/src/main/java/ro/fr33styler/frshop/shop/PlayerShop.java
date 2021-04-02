package ro.fr33styler.frshop.shop;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.fr33styler.frshop.FrShop;
import ro.fr33styler.frshop.provider.EconomyProvider;
import ro.fr33styler.frshop.shop.types.ClickType;
import ro.fr33styler.frshop.shop.types.ShopType;
import ro.fr33styler.frshop.shop.types.TradeType;
import ro.fr33styler.frshop.util.Utils;

import java.util.UUID;

public class PlayerShop implements Shop {

    private UUID uuid;
    private int amount;
    private String name;
    private double price;
    private String priceFormat;
    private ItemStack itemStack;
    private TradeType tradeType;
    private Block[] protectedBlocks;
    private EconomyProvider economyProvider = FrShop.getInstance().getDefaultEco();

    private final Block block;

    public PlayerShop(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        Validate.isTrue(amount > 0, FrShop.getInstance().getMessages().getAmountMustBeHigher());
        this.amount = amount;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        Validate.isTrue(price > 0, FrShop.getInstance().getMessages().getPriceMustBeHigher());
        this.price = price;
        priceFormat = Utils.formatNumber(price);
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public Block[] getProtectedBlocks() {
        return protectedBlocks;
    }

    @Override
    public void setProtectedBlocks(Block... protectedBlocks) {
        this.protectedBlocks = protectedBlocks;
    }

    @Override
    public TradeType getTradeType() {
        return tradeType;
    }

    @Override
    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    @Override
    public EconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    @Override
    public void setEconomyProvider(EconomyProvider economyProvider) {
        this.economyProvider = economyProvider;
    }

    @Override
    public ShopType getType() {
        return ShopType.PLAYER_SHOP;
    }

    private String replaceText(String text) {
        text = text.replace("%material%", Utils.formatMaterial(itemStack.getType()));
        text = text.replace("%amount%", String.valueOf(amount));
        text = text.replace("%price%", priceFormat);
        text = text.replace("%price_per_item%", String.valueOf(price / amount));
        text = text.replace("%owner%", name);
        text = text.replace("%symbol%", economyProvider.getSymbol(price));
        return text;
    }

    @Override
    public void refreshSign() {
        BlockState blockState = protectedBlocks[0].getState();
        if (blockState instanceof Container) {
            refreshSign((Container) blockState);
        } else {
            FrShop.getInstance().getDatabase().deleteShop(this);
        }
    }

    private void refreshSign(Container container) {

        boolean available = false;
        String[] text = Utils.EMPTY;

        if (tradeType == TradeType.BUY) {
            text = FrShop.getInstance().getMessages().getSignPlayerBuy();
            if (Utils.giveAmount(Utils.getContainer(container.getInventory()), itemStack, amount)) {
                available = true;
            }
        } else if (tradeType == TradeType.SELL) {
            text = FrShop.getInstance().getMessages().getSignPlayerSell();
            if (Utils.takeAmount(Utils.getContainer(container.getInventory()), itemStack, amount)) {
                available = true;
            }
        }

        Sign sign = (Sign) block.getState();
        for (int i = 0; i < 4 && i < text.length; i++) {
            sign.setLine(i, replaceText(text[i]).replace("%available%", available ? "§a" : "§4"));
        }
        sign.update();
    }

    @Override
    public boolean canInteract(Player p) {
        return p.hasPermission("frshop.admin") || p.getUniqueId().equals(uuid);
    }

    @Override
    public void onClick(ClickType clickType, Player p) {
        if (clickType == ClickType.LEFT) {
            if (!p.isSneaking()) {
                if (tradeType == TradeType.BUY) {
                    for (String msg : FrShop.getInstance().getMessages().getInfoPlayerBuy()) {
                        p.sendMessage(replaceText(msg));
                    }
                } else if (tradeType == TradeType.SELL) {
                    for (String msg : FrShop.getInstance().getMessages().getInfoPlayerSell()) {
                        p.sendMessage(replaceText(msg));
                    }
                }
            }
        } else if (clickType == ClickType.RIGHT) {
            BlockState blockState = protectedBlocks[0].getState();
            if (blockState instanceof Container) {

                Container container = (Container) blockState;
                if (tradeType == TradeType.BUY) {

                    ItemStack[] chestContents = Utils.getContainer(container.getInventory());
                    ItemStack[] playerContents = Utils.getContainer(p.getInventory());
                    if (!Utils.giveAmount(chestContents, itemStack, amount)) {
                        p.sendMessage(FrShop.getInstance().getMessages().getShopNotEnoughSpace());
                    } else if (!Utils.takeAmount(playerContents, itemStack, amount)) {
                        p.sendMessage(FrShop.getInstance().getMessages().getYouNotEnoughItems());
                    } else {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                        if (economyProvider.has(op, price)) {
                            economyProvider.deposit(p, price);
                            economyProvider.withdraw(op, price);
                            container.getInventory().setContents(chestContents);
                            p.getInventory().setContents(playerContents);
                            p.sendMessage(replaceText(FrShop.getInstance().getMessages().getSellMessage()));
                        } else {
                            p.sendMessage(FrShop.getInstance().getMessages().getShopNotEnoughMoney());
                        }
                    }

                } else if (tradeType == TradeType.SELL) {

                    ItemStack[] chestContents = Utils.getContainer(container.getInventory());
                    ItemStack[] playerContents = Utils.getContainer(p.getInventory());
                    if (!Utils.takeAmount(chestContents, itemStack, amount)) {
                        p.sendMessage(FrShop.getInstance().getMessages().getShopNotEnoughItems());
                    } else if (!Utils.giveAmount(playerContents, itemStack, amount)) {
                        p.sendMessage(FrShop.getInstance().getMessages().getYouNotEnoughSpace());
                    } else if (!economyProvider.has(p, price)) {
                        p.sendMessage(FrShop.getInstance().getMessages().getNotEnoughMoney());
                    } else {
                        economyProvider.withdraw(p, price);
                        economyProvider.deposit(Bukkit.getOfflinePlayer(uuid), price);
                        container.getInventory().setContents(chestContents);
                        p.getInventory().setContents(playerContents);
                        p.sendMessage(replaceText(FrShop.getInstance().getMessages().getBuyMessage()));
                    }

                }
                refreshSign(container);
            } else {
                FrShop.getInstance().getDatabase().deleteShop(this);
            }
        }
    }

}
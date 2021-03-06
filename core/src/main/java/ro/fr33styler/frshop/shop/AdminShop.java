package ro.fr33styler.frshop.shop;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
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

public class AdminShop implements Shop {

    private UUID uuid;
    private int amount;
    private String name;
    private double price;
    private String priceFormat;
    private ItemStack itemStack;
    private TradeType tradeType;
    private Block[] relativeBlock;
    private EconomyProvider economyProvider = FrShop.getInstance().getDefaultEco();

    private final Block block;

    public AdminShop(Block block) {
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
        return relativeBlock;
    }

    @Override
    public void setProtectedBlocks(Block... protectedBlocks) {
        this.relativeBlock = protectedBlocks;
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
        return ShopType.ADMIN_SHOP;
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
        String[] text = Utils.EMPTY;

        if (tradeType == TradeType.BUY) {
            text = FrShop.getInstance().getMessages().getSignAdminBuy();
        } else if (tradeType == TradeType.SELL) {
            text = FrShop.getInstance().getMessages().getSignAdminSell();
        }

        Sign sign = (Sign) block.getState();
        for (int i = 0; i < 4 && i < text.length; i++) {
            sign.setLine(i, replaceText(text[i]));
        }
        sign.update();
    }

    @Override
    public boolean canInteract(Player p) {
        return p.hasPermission("frshop.admin");
    }

    @Override
    public void onClick(ClickType clickType, Player p) {
        if (clickType == ClickType.LEFT) {
            if (!p.isSneaking()) {
                if (tradeType == TradeType.BUY) {
                    for (String msg : FrShop.getInstance().getMessages().getInfoAdminBuy()) {
                        p.sendMessage(replaceText(msg));
                    }
                } else if (tradeType == TradeType.SELL) {
                    for (String msg : FrShop.getInstance().getMessages().getInfoAdminSell()) {
                        p.sendMessage(replaceText(msg));
                    }
                }
            }
        } else if (clickType == ClickType.RIGHT) {

            if (tradeType == TradeType.BUY) {

                ItemStack[] playerContents = Utils.getContainer(p.getInventory());
                if (!Utils.takeAmount(playerContents, itemStack, amount)) {
                    p.sendMessage(FrShop.getInstance().getMessages().getYouNotEnoughItems());
                } else {
                    economyProvider.deposit(p, price);
                    p.getInventory().setContents(playerContents);
                    p.sendMessage(replaceText(FrShop.getInstance().getMessages().getSellMessage()));
                }
            } else if (tradeType == TradeType.SELL) {
                ItemStack[] playerContents = Utils.getContainer(p.getInventory());
                if (!Utils.giveAmount(playerContents, itemStack, amount)) {
                    p.sendMessage(FrShop.getInstance().getMessages().getYouNotEnoughSpace());
                } else if (!economyProvider.has(p, price)) {
                    p.sendMessage(FrShop.getInstance().getMessages().getNotEnoughMoney());
                } else {
                    economyProvider.withdraw(p, price);
                    p.getInventory().setContents(playerContents);
                    p.sendMessage(replaceText(FrShop.getInstance().getMessages().getBuyMessage()));
                }
            }
            refreshSign();
        }
    }


}

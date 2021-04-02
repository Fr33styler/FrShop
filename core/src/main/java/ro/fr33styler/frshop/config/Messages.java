package ro.fr33styler.frshop.config;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Messages {

    private final String notEnoughMoney;
    private final String[] infoAdminSell;
    private final String[] infoAdminBuy;
    private final String[] infoPlayerSell;
    private final String[] infoPlayerBuy;
    private final String[] signAdminSell;
    private final String[] signAdminBuy;
    private final String[] signPlayerSell;
    private final String[] signPlayerBuy;
    private final String sellMessage;
    private final String buyMessage;
    private final String shopRemoved;
    private final String amountMustBeHigher;
    private final String priceMustBeHigher;
    private final String youNotEnoughSpace;
    private final String shopNotEnoughSpace;
    private final String youNotEnoughItems;
    private final String shopNotEnoughItems;
    private final String breakNotAllowed;
    private final String attachNotAllowed;
    private final String playerShopContainer;
    private final String shopCreated;
    private final String interactNotAllowed;
    private final String mustBeNumber;
    private final String shopNotEnoughMoney;

    public Messages(ConfigurationSection section) {
        Validate.notNull(section, "Error while loading the messages!");
        notEnoughMoney = getMsg(section.getString("NotEnoughMoney"));
        infoAdminSell = getMsg(section.getStringList("InfoAdminSell"));
        infoAdminBuy = getMsg(section.getStringList("InfoAdminBuy"));
        infoPlayerSell = getMsg(section.getStringList("InfoPlayerSell"));
        infoPlayerBuy = getMsg(section.getStringList("InfoPlayerBuy"));
        signAdminSell = getMsg(section.getStringList("SignAdminSell"));
        signAdminBuy = getMsg(section.getStringList("SignAdminBuy"));
        signPlayerSell = getMsg(section.getStringList("SignPlayerSell"));
        signPlayerBuy = getMsg(section.getStringList("SignPlayerBuy"));
        sellMessage = getMsg(section.getString("SellMessage"));
        buyMessage = getMsg(section.getString("BuyMessage"));
        shopCreated = getMsg(section.getString("ShopCreated"));
        shopRemoved = getMsg(section.getString("ShopRemoved"));
        mustBeNumber = getMsg(section.getString("MustBeNumber"));
        amountMustBeHigher = getMsg(section.getString("AmountMustBeHigher"));
        priceMustBeHigher = getMsg(section.getString("PriceMustBeHigher"));
        youNotEnoughSpace = getMsg(section.getString("YouNotEnoughSpace"));
        shopNotEnoughSpace = getMsg(section.getString("ShopNotEnoughSpace"));
        youNotEnoughItems = getMsg(section.getString("YouNotEnoughItems"));
        shopNotEnoughItems = getMsg(section.getString("ShopNotEnoughItems"));
        shopNotEnoughMoney = getMsg(section.getString("ShopNotEnoughMoney"));
        breakNotAllowed = getMsg(section.getString("BreakNotAllowed"));
        attachNotAllowed = getMsg(section.getString("AttachNotAllowed"));
        interactNotAllowed = getMsg(section.getString("InteractNotAllowed"));
        playerShopContainer = getMsg(section.getString("PlayerShopContainer"));
    }

    private String getMsg(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }

    private String[] getMsg(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = getMsg(list.get(i));
        }
        return array;
    }

    public String getNotEnoughMoney() {
        return notEnoughMoney;
    }

    public String[] getInfoAdminSell() {
        return infoAdminSell;
    }

    public String[] getInfoAdminBuy() {
        return infoAdminBuy;
    }

    public String[] getInfoPlayerSell() {
        return infoPlayerSell;
    }

    public String[] getInfoPlayerBuy() {
        return infoPlayerBuy;
    }

    public String[] getSignAdminSell() {
        return signAdminSell;
    }

    public String[] getSignAdminBuy() {
        return signAdminBuy;
    }

    public String[] getSignPlayerSell() {
        return signPlayerSell;
    }

    public String[] getSignPlayerBuy() {
        return signPlayerBuy;
    }

    public String getSellMessage() {
        return sellMessage;
    }

    public String getBuyMessage() {
        return buyMessage;
    }

    public String getShopCreated() {
        return shopCreated;
    }

    public String getShopRemoved() {
        return shopRemoved;
    }

    public String getMustBeNumber() {
        return mustBeNumber;
    }

    public String getYouNotEnoughSpace() {
        return youNotEnoughSpace;
    }

    public String getShopNotEnoughSpace() {
        return shopNotEnoughSpace;
    }

    public String getYouNotEnoughItems() {
        return youNotEnoughItems;
    }

    public String getShopNotEnoughItems() {
        return shopNotEnoughItems;
    }

    public String getAmountMustBeHigher() {
        return amountMustBeHigher;
    }

    public String getPriceMustBeHigher() {
        return priceMustBeHigher;
    }

    public String getBreakNotAllowed() {
        return breakNotAllowed;
    }

    public String getAttachNotAllowed() {
        return attachNotAllowed;
    }

    public String getInteractNotAllowed() {
        return interactNotAllowed;
    }

    public String getPlayerShopContainer() {
        return playerShopContainer;
    }

    public String getShopNotEnoughMoney() {
        return shopNotEnoughMoney;
    }
}
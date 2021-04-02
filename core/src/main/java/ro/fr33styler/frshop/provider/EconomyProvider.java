package ro.fr33styler.frshop.provider;

import org.bukkit.OfflinePlayer;

public interface EconomyProvider {

    String getName();

    String getSymbol(double amount);

    boolean has(OfflinePlayer player, double amount);

    void deposit(OfflinePlayer player, double amount);

    void withdraw(OfflinePlayer player, double amount);

}
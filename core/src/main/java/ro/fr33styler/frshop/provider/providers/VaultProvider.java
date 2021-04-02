package ro.fr33styler.frshop.provider.providers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import ro.fr33styler.frshop.provider.EconomyProvider;

public class VaultProvider implements EconomyProvider {

    private final Economy economy;

    public VaultProvider(Economy economy) {
        this.economy = economy;
    }

    @Override
    public String getName() {
        return economy.getName();
    }

    @Override
    public String getSymbol(double amount) {
        return amount == 1 ? economy.currencyNameSingular() : economy.currencyNamePlural();
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

}

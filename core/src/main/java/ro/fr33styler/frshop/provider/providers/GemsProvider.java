package ro.fr33styler.frshop.provider.providers;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.account.Account;
import me.xanium.gemseconomy.currency.Currency;
import org.bukkit.OfflinePlayer;
import ro.fr33styler.frshop.provider.EconomyProvider;

public class GemsProvider implements EconomyProvider {

    private final Currency currency;

    public GemsProvider(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String getSymbol(double amount) {
        if (currency.getSymbol() == null) {
            return amount == 1 ? currency.getSingular() : currency.getPlural();
        } else {
            return currency.getSymbol();
        }
    }

    @Override
    public String getName() {
        return getSymbol(1);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        Account user = GemsEconomy.getInstance().getAccountManager().getAccount(player.getUniqueId());
        if (user != null) {
            return user.hasEnough(currency, amount);
        }
        return false;
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) {
        Account user = GemsEconomy.getInstance().getAccountManager().getAccount(player.getUniqueId());
        if (user != null) {
            user.deposit(currency, amount);
        }
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) {
        Account user = GemsEconomy.getInstance().getAccountManager().getAccount(player.getUniqueId());
        if (user != null) {
            user.withdraw(currency, amount);
        }
    }
}

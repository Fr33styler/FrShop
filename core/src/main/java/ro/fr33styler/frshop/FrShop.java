package ro.fr33styler.frshop;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.currency.Currency;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ro.fr33styler.frshop.config.Messages;
import ro.fr33styler.frshop.config.Settings;
import ro.fr33styler.frshop.database.Database;
import ro.fr33styler.frshop.events.Events;
import ro.fr33styler.frshop.provider.EconomyProvider;
import ro.fr33styler.frshop.provider.providers.GemsProvider;
import ro.fr33styler.frshop.provider.providers.VaultProvider;
import ro.fr33styler.frshop.version.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FrShop extends JavaPlugin {

    private Version version;
    private Messages messages;
    private Database database;
    private EconomyProvider defaultEco;
    private final List<EconomyProvider> economies = new ArrayList<>();

    private static FrShop INSTANCE;

    public FrShop() {
        INSTANCE = this;
    }
//https://cdn.discordapp.com/attachments/806223443786727475/820381088072335401/unknown.png
    public static FrShop getInstance() {
        return INSTANCE;
    }

    private boolean hasPlugin(Plugin plugin) {
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public void onEnable() {
        ConsoleCommandSender console = getServer().getConsoleSender();
        switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
            case "v1_13_R2":
                version = new v1_13_R2();
                break;
            case "v1_14_R1":
                version = new v1_14_R1();
                break;
            case "v1_15_R1":
                version = new v1_15_R1();
                break;
            case "v1_16_R1":
                version = new v1_16_R1();
                break;
            case "v1_16_R2":
                version = new v1_16_R2();
                break;
            case "v1_16_R3":
                version = new v1_16_R3();
                break;
            default:
                console.sendMessage("§c=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                console.sendMessage("§c" + getName() + " only works for: ");
                console.sendMessage("§c - 1.16.5");
                console.sendMessage("§c - 1.16.3");
                console.sendMessage("§c - 1.16.1");
                console.sendMessage("§c - 1.15.2");
                console.sendMessage("§c - 1.14.4");
                console.sendMessage("§c - 1.13.2");
                console.sendMessage("§c=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                setEnabled(false);
                return;
        }
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        console.sendMessage("§a" + getName() + " plugin is loading... ");
        saveDefaultConfig();
        FileConfiguration configuration = getConfig();
        Settings settings = new Settings(configuration.getConfigurationSection("Settings"));
        messages = new Messages(configuration.getConfigurationSection("Messages"));
        if (hasPlugin(getServer().getPluginManager().getPlugin("Vault"))) {
            console.sendMessage("§a - Loading Vault economies...");
            for (RegisteredServiceProvider<Economy> provider : getServer().getServicesManager().getRegistrations(Economy.class)) {
                economies.add(new VaultProvider(provider.getProvider()));
            }
        }
        if (hasPlugin(getServer().getPluginManager().getPlugin("GemsEconomy"))) {
            console.sendMessage("§a - Loading GemsEconomy currencies...");
            for (Currency currency : GemsEconomy.getInstance().getCurrencyManager().getCurrencies()) {
                economies.add(new GemsProvider(currency));
            }
        }
        console.sendMessage("§eEconomies found: ");
        for (EconomyProvider provider : economies) {
            if (provider.getName().equals(settings.getDefaultCurrency())) {
                defaultEco = provider;
            } else if (defaultEco == null) {
                defaultEco = provider;
            }
            if (provider instanceof GemsProvider) {
                console.sendMessage("- §e|" + provider.getName() + "| (GemsEconomy)");
            } else {
                console.sendMessage("- §e|" + provider.getName() + "| (Vault)");
            }
        }
        if (defaultEco == null) {
            console.sendMessage("§cThe default economy could not be found!");
            setEnabled(false);
            console.sendMessage("§a" + getName() + " has been disabled!");
            console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            return;
        } else {
            try {
                database = new Database(this, new File(getDataFolder(), "Database"));
                getServer().getPluginManager().registerEvents(new Events(this), this);
            } catch (Exception e) {
                e.printStackTrace();
                setEnabled(false);
                console.sendMessage("§a" + getName() + " has been disabled!");
                console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                return;
            }
        }
        console.sendMessage("§a" + getName() + " has been loaded!");
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    @Override
    public void onDisable() {
        economies.clear();
        defaultEco = null;
    }

    public Database getDatabase() {
        return database;
    }

    public Version getVersion() {
        return version;
    }

    public EconomyProvider getEconomy(String eco) {
        if (eco.isEmpty()) {
            return defaultEco;
        }
        for (EconomyProvider economy : economies) {
            if (economy.getName().equals(eco)) {
                return economy;
            }
        }
        throw new IllegalArgumentException("The economy " + eco + " is missing!");
    }

    public Messages getMessages() {
        return messages;
    }

    public EconomyProvider getDefaultEco() {
        return defaultEco;
    }

}
package ro.fr33styler.frshop.config;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class Settings {

    private String defaultCurrency;

    public Settings(ConfigurationSection section) {
        Validate.notNull(section, "Error while loading the config!");
        defaultCurrency = section.getString("DefaultCurrency");
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

}

package ro.fr33styler.frshop.version;

import org.bukkit.inventory.ItemStack;

public interface Version {

    byte[] serialize(ItemStack itemStack);

    ItemStack deserialize(byte[] data);

}
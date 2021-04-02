package ro.fr33styler.frshop.version;

import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class v1_16_R3 implements Version {

    @Override
    public byte[] serialize(ItemStack itemStack) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTCompressedStreamTools.a(CraftItemStack.asNMSCopy(itemStack).save(tag), out);
            return out.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public ItemStack deserialize(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            NBTTagCompound tag = NBTCompressedStreamTools.a(in);
            return CraftItemStack.asBukkitCopy(net.minecraft.server.v1_16_R3.ItemStack.a(tag));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}

package ro.fr33styler.frshop.version;

import net.minecraft.server.v1_16_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class v1_16_R1 implements Version {

    @Override
    public byte[] serialize(ItemStack itemStack) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag = CraftItemStack.asNMSCopy(itemStack).save(tag);
            NBTCompressedStreamTools.a(tag, out);
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
            return CraftItemStack.asBukkitCopy(net.minecraft.server.v1_16_R1.ItemStack.a(tag));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}

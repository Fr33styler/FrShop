package ro.fr33styler.frshop.util;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Utils {

    private Utils() {}

    public static final String[] EMPTY = new String[0];
    private static final char[] NUMBER_FORMAT = {'k', 'M', 'G', 'T', 'P', 'E'};
    public static final Map<BlockFace, BlockFace> LEFT_RELATION = ImmutableMap.<BlockFace, BlockFace>builder()
            .put(BlockFace.NORTH, BlockFace.EAST)
            .put(BlockFace.EAST, BlockFace.SOUTH)
            .put(BlockFace.SOUTH, BlockFace.WEST)
            .put(BlockFace.WEST, BlockFace.NORTH)
            .build();

    public static final Map<BlockFace, BlockFace> RIGHT_RELATION = ImmutableMap.<BlockFace, BlockFace>builder()
            .put(BlockFace.NORTH, BlockFace.WEST)
            .put(BlockFace.WEST, BlockFace.SOUTH)
            .put(BlockFace.SOUTH, BlockFace.EAST)
            .put(BlockFace.EAST, BlockFace.NORTH)
            .build();

    public static ItemStack[] getContainer(Inventory inventory) {
        ItemStack[] old = inventory.getContents();
        ItemStack[] contents = new ItemStack[old.length];
        for (int i = 0; i < old.length; i++) {
            ItemStack item = old[i];
            if (item != null) {
                contents[i] = new ItemStack(item);
            }
        }
        return contents;
    }

    public static String serializeBlock(Block block) {
        return block.getWorld().getName() + ',' + block.getX() + ',' + block.getY() + ',' + block.getZ();
    }

    public static String formatNumber(double count) {
        if (count < 1000) {
            return String.valueOf(count);
        }
        int exp = (int) (Math.log(count) / 6.9077);
        return String.format("%.1f%c", count / Math.pow(1000, exp), NUMBER_FORMAT[exp - 1]);
    }

    public static boolean takeAmount(ItemStack[] contents, ItemStack itemStack, int amount) {
        for (int x = 0; x < contents.length; x++) {
            ItemStack item = contents[x];
            if (item != null && item.isSimilar(itemStack)) {
                if (item.getAmount() <= amount) {
                    amount -= item.getAmount();
                    contents[x] = null;
                } else {
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }
                if (amount <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean giveAmount(ItemStack[] contents, ItemStack itemStack, int amount) {
        Material type = itemStack.getType();
        for (int x = 0; x < contents.length; x++) {
            ItemStack item = contents[x];
            if (item == null) {
                int val = Math.min(amount, type.getMaxStackSize());
                item = new ItemStack(itemStack);
                item.setAmount(val);
                contents[x] = item;
                amount -= val;
            } else if (item.getAmount() < type.getMaxStackSize() && item.isSimilar(itemStack)) {
                int val = Math.min(type.getMaxStackSize() - item.getAmount(), amount);
                item.setAmount(item.getAmount() + val);
                amount -= val;
            }
            if (amount <= 0) {
                return true;
            }
        }
        return false;
    }

    public static String formatMaterial(Material material) {
        boolean uppercase = true;
        byte[] chars = material.name().getBytes();
        for (int i = 0; i < chars.length; i++) {
            byte character = chars[i];
            if (character == '_') {
                uppercase = true;
                chars[i] = ' ';
            } else if (uppercase) {
                if (character >= 97 && character <= 122) {
                    chars[i] -= 32;
                }
                uppercase = false;
            } else if (character >= 65 && character <= 90) {
                chars[i] += 32;
            }
        }
        if (chars.length > 13) {
          return new String(chars, 0, 13);
        }
        return new String(chars);
    }

}

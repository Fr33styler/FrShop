package ro.fr33styler.frshop.database;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import ro.fr33styler.frshop.FrShop;
import ro.fr33styler.frshop.provider.EconomyProvider;
import ro.fr33styler.frshop.shop.Shop;
import ro.fr33styler.frshop.shop.types.ShopType;
import ro.fr33styler.frshop.shop.types.TradeType;
import ro.fr33styler.frshop.util.Utils;

import java.io.*;
import java.util.UUID;

public class Database {

    private final File folder;
    private final FrShop frShop;

    public Database(FrShop frShop, File folder) {
        this.folder = folder;
        this.frShop = frShop;
        folder.mkdir();
    }

    public boolean hasShop(Block block) {
        String prot = Utils.serializeBlock(block);
        //Check if exists as sign
        if (new File(folder, prot + ".dat").exists()) {
            return true;
        }
        //Check if exists as prot block
        if (new File(folder, prot + "-prot.dat").exists()) {
            return true;
        }
        return false;
    }

    public void deleteShop(Shop shop) {
        new File(folder, Utils.serializeBlock(shop.getBlock()) + ".dat").delete();
        for (Block block : shop.getProtectedBlocks()) {
            new File(folder, Utils.serializeBlock(block) + "-prot.dat").delete();
        }
    }

    public void createShop(Block sign, Shop shop) throws IOException {
        //Load file
        File file = new File(folder, Utils.serializeBlock(sign) + ".dat");
        
        //Create if necessary
        file.createNewFile();
        
        //Let try-with-resource close up loose connections
        try (FileOutputStream fileOut = new FileOutputStream(file); DataOutputStream dataOut = new DataOutputStream(fileOut)) {
            
            //Write data to file
            dataOut.writeInt(0);
            dataOut.writeByte(shop.getType().getId());
            dataOut.writeByte(shop.getTradeType().getId());
            dataOut.writeLong(shop.getUUID().getMostSignificantBits());
            dataOut.writeLong(shop.getUUID().getLeastSignificantBits());
            dataOut.writeUTF(shop.getName());
            byte[] bytes = frShop.getVersion().serialize(shop.getItemStack());
            dataOut.writeInt(bytes.length);
            dataOut.write(bytes);
            dataOut.writeInt(shop.getAmount());
            dataOut.writeDouble(shop.getPrice());

            if (shop.getEconomyProvider() == frShop.getDefaultEco()) {
                dataOut.writeUTF("");
            } else {
                dataOut.writeUTF(shop.getName());
            }

            Block[] protBlocks = shop.getProtectedBlocks();
            dataOut.writeInt(protBlocks.length);
            for (Block block : protBlocks) {
                dataOut.writeInt(block.getX());
                dataOut.writeInt(block.getY());
                dataOut.writeInt(block.getZ());

                //Load protection file
                File protFile = new File(folder, Utils.serializeBlock(block) + "-prot.dat");

                //Create file
                protFile.createNewFile();

                try (FileOutputStream protFileOut = new FileOutputStream(protFile); DataOutputStream protDataOut = new DataOutputStream(protFileOut)) {
                    protDataOut.writeInt(sign.getX());
                    protDataOut.writeInt(sign.getY());
                    protDataOut.writeInt(sign.getZ());
                }

            }
            
        }

    }

    public Shop getShop(Block block, boolean isBlock) {
        String fileName = Utils.serializeBlock(block);

        //Get shop from the protected block
        if (isBlock) {
            File file = new File(folder, fileName + "-prot.dat");
            if (file.exists()) {
                try (FileInputStream fileIn = new FileInputStream(file); DataInputStream dataIn = new DataInputStream(fileIn)) {
                    block = block.getWorld().getBlockAt(dataIn.readInt(), dataIn.readInt(), dataIn.readInt());
                    fileName = Utils.serializeBlock(block);
                } catch (Exception exception) {
                    return null;
                }
            } else {
                return null;
            }
        }

        //Load file
        File file = new File(folder, fileName + ".dat");
        if (file.exists()) {
            try {
                //Let try-with-resource close up loose connections
                try (FileInputStream fileIn = new FileInputStream(file); DataInputStream dataIn = new DataInputStream(fileIn)) {

                    //Parse file
                    dataIn.readInt();//Version
                    ShopType shopType = ShopType.getByID(dataIn.readByte());
                    TradeType tradeType = TradeType.getByID(dataIn.readByte());
                    UUID uuid = new UUID(dataIn.readLong(), dataIn.readLong());
                    String name = dataIn.readUTF();
                    byte[] bytes = new byte[ dataIn.readInt() ];
                    dataIn.read(bytes);
                    ItemStack itemStack = frShop.getVersion().deserialize(bytes);
                    int amount = dataIn.readInt();
                    double price = dataIn.readDouble();
                    EconomyProvider economyProvider = frShop.getEconomy(dataIn.readUTF());
                    Block[] protBlocks = new Block[ dataIn.readInt() ];
                    for (int i = 0; i < protBlocks.length; i++) {
                        protBlocks[i] = block.getWorld().getBlockAt(dataIn.readInt(), dataIn.readInt(), dataIn.readInt());
                    }

                    //Create shop
                    Shop shop = shopType.newInstance(block);
                    shop.setTradeType(tradeType);
                    shop.setUUID(uuid);
                    shop.setName(name);
                    shop.setItemStack(itemStack);
                    shop.setAmount(amount);
                    shop.setPrice(price);
                    shop.setEconomyProvider(economyProvider);
                    shop.setProtectedBlocks(protBlocks);

                    return shop;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
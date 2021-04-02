package ro.fr33styler.frshop.events;

import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ro.fr33styler.frshop.FrShop;
import ro.fr33styler.frshop.shop.AdminShop;
import ro.fr33styler.frshop.shop.PlayerShop;
import ro.fr33styler.frshop.shop.Shop;
import ro.fr33styler.frshop.shop.types.ClickType;
import ro.fr33styler.frshop.shop.types.TradeType;
import ro.fr33styler.frshop.util.Utils;

import java.io.IOException;

public class Events implements Listener {

    private final FrShop frShop;

    public Events(FrShop frShop) {
        this.frShop = frShop;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        boolean isWallSign = block.getBlockData() instanceof WallSign;

        Shop shop = frShop.getDatabase().getShop(block, !isWallSign);
        if (shop != null) {
            if (isWallSign && shop.canInteract(p)) {
                frShop.getDatabase().deleteShop(shop);
                p.sendMessage(frShop.getMessages().getShopRemoved());
            } else {
                e.setCancelled(true);
                p.sendMessage(FrShop.getInstance().getMessages().getBreakNotAllowed());
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        BlockData data = block.getBlockData();
        if (data instanceof Chest) {
            Chest chest = (Chest) data;
            BlockFace relative = null;
            if (chest.getType() == Chest.Type.LEFT) {
                relative = Utils.LEFT_RELATION.get(chest.getFacing());
            } else if (chest.getType() == Chest.Type.RIGHT) {
                relative = Utils.RIGHT_RELATION.get(chest.getFacing());
            }
            if (relative != null) {
                Block attached = block.getRelative(relative);
                Shop shop = frShop.getDatabase().getShop(attached, true);
                if (shop != null) {
                    if (shop.canInteract(p)) {
                        try {
                            shop.setProtectedBlocks(block, attached);
                            frShop.getDatabase().createShop(shop.getBlock(), shop);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(FrShop.getInstance().getMessages().getAttachNotAllowed());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Block block = null;
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Container) {
            block = ((Container) holder).getBlock();
        } else if (holder instanceof DoubleChest) {
            block = ((DoubleChest) holder).getLocation().getBlock();
        }
        if (block != null) {
            Shop shop = frShop.getDatabase().getShop(block, true);
            if (shop != null) {
                shop.refreshSign();
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.HAND) {
            Block block = e.getClickedBlock();
            if (block != null && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                BlockData blockData = block.getBlockData();
                if (blockData instanceof WallSign) {

                    WallSign wallSign = (WallSign) blockData;
                    Shop shop = frShop.getDatabase().getShop(block, false);
                    if (shop == null) {
                        //Try creating a new shop
                        ItemStack item = e.getItem();
                        if (item != null && e.getAction() == Action.LEFT_CLICK_BLOCK) {

                            Sign sign = (Sign) block.getState();
                            String[] lines = sign.getLines();
                            Block rel = block.getRelative(wallSign.getFacing().getOppositeFace());

                            if (frShop.getDatabase().hasShop(rel)) {
                                //Empty
                            } else if (lines[0].equalsIgnoreCase("[Shop]")) {
                                BlockState blockState = rel.getState();
                                if (blockState instanceof Container) {
                                    shop = new PlayerShop(block);
                                    Container container = (Container) blockState;
                                    if (container.getInventory() instanceof DoubleChestInventory) {
                                        DoubleChestInventory chest = (DoubleChestInventory) container.getInventory();
                                        Block left = chest.getLeftSide().getLocation().getBlock();
                                        Block right = chest.getRightSide().getLocation().getBlock();
                                        shop.setProtectedBlocks(left, right);
                                    } else {
                                        shop.setProtectedBlocks(rel);
                                    }
                                } else {
                                    p.sendMessage(FrShop.getInstance().getMessages().getPlayerShopContainer());
                                }
                            } else if (lines[0].equalsIgnoreCase("[iShop]")) {
                                shop = new AdminShop(block);
                                shop.setProtectedBlocks(rel);
                            }

                            if (shop != null) {
                                e.setCancelled(true);
                                try {
                                    shop.setUUID(p.getUniqueId());
                                    shop.setName(p.getName());
                                    shop.setItemStack(item);
                                    shop.setAmount(Integer.parseInt(lines[1]));
                                    shop.setPrice(Double.parseDouble(lines[2]));
                                    shop.setTradeType(TradeType.getByName(lines[3]));
                                    frShop.getDatabase().createShop(block, shop);
                                    p.sendMessage(FrShop.getInstance().getMessages().getShopCreated());
                                    shop.refreshSign();
                                } catch (NumberFormatException exception) {
                                    p.sendMessage(FrShop.getInstance().getMessages().getMustBeNumber());
                                } catch (IllegalArgumentException exception) {
                                    p.sendMessage("§c" + exception.getMessage());
                                } catch (IOException exception) {
                                    p.sendMessage("§cError writing the shop file: " + exception.getMessage());
                                }
                            }

                        }
                    } else {
                        //Provide the shop with info
                        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            shop.onClick(ClickType.LEFT, p);
                        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            shop.onClick(ClickType.RIGHT, p);
                        }

                    }
                } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Shop shop = frShop.getDatabase().getShop(block, true);
                    if (shop != null && !shop.canInteract(p)) {
                        e.setCancelled(true);
                        p.sendMessage(FrShop.getInstance().getMessages().getInteractNotAllowed());
                    }
                }

            }
        }
    }

}
package ro.fr33styler.frshop.shop.types;

import org.bukkit.block.Block;
import ro.fr33styler.frshop.shop.AdminShop;
import ro.fr33styler.frshop.shop.PlayerShop;
import ro.fr33styler.frshop.shop.Shop;

import java.util.function.Function;

public enum ShopType {

    PLAYER_SHOP(0, PlayerShop::new),
    ADMIN_SHOP(1, AdminShop::new);

    private final int id;
    private final Function<Block, Shop> constructor;
    static final ShopType[] VALUES = ShopType.values();

    ShopType(int id, Function<Block, Shop> function) {
        this.id = id;
        this.constructor = function;
    }

    public Shop newInstance(Block block) {
        return constructor.apply(block);
    }

    public int getId() {
        return id;
    }

    public static ShopType getByID(int id) {
        return VALUES[id];
    }

}

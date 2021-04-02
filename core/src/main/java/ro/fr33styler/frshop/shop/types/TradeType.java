package ro.fr33styler.frshop.shop.types;

public enum TradeType {

    BUY(0, "Buy"),
    SELL(1, "Sell");

    private final int id;
    private final String name;
    static final TradeType[] VALUES = TradeType.values();

    TradeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static TradeType getByID(int id) {
        return VALUES[id];
    }

    public String getName() {
        return name;
    }

    public static TradeType getByName(String name) {
        for (TradeType tradeType : TradeType.values()) {
            if (tradeType.getName().equalsIgnoreCase(name)) {
                return tradeType;
            }
        }
        throw new IllegalArgumentException("The trade type does not exists!");
    }

}

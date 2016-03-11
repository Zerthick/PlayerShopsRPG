package io.github.zerthick.playershopsrpg.shop;

public class ShopTransactionResult {

    public static final ShopTransactionResult SUCCESS = new ShopTransactionResult("SUCCESS");
    public static final ShopTransactionResult EMPTY = new ShopTransactionResult("");

    private final String message;

    public ShopTransactionResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

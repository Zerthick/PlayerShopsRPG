package io.github.zerthick.playershopsrpg.utils.inventory;

public class InventoryUtilTransactionResult {

    public static final InventoryUtilTransactionResult SUCCESS = new InventoryUtilTransactionResult("SUCCESS");
    public static final InventoryUtilTransactionResult EMPTY = new InventoryUtilTransactionResult("");

    private final String message;

    public InventoryUtilTransactionResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

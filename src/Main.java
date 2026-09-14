import java.util.List;

/**
 * Console demonstration of an educational blockchain ledger.
 * Suitable for a short video: create, display, verify, tamper, re-verify.
 */
public class Main {

    private static final int TAMPER_BLOCK_INDEX = 2;
    private static final String ORIGINAL_BLOCK_TWO_DATA = "Bob -> Charlie: $50";
    private static final String TAMPERED_BLOCK_TWO_DATA = "Bob -> Charlie: $5000";
    private static final List<String> DEMO_TRANSACTIONS = List.of(
            "Alice -> Bob: $100",
            ORIGINAL_BLOCK_TWO_DATA,
            "Charlie -> David: $25",
            "David -> Alice: $75",
            "Alice -> Charlie: $40"
    );

    public static void main(String[] args) {
        Blockchain ledger = createBlockchain();
        addTransactions(ledger);
        displayChain(ledger);
        verifyChain(ledger);
        demonstrateTampering(ledger);
        verifyChain(ledger);
    }

    private static Blockchain createBlockchain() {
        System.out.println("=== STEP 1: CREATE BLOCKCHAIN ===");
        System.out.println("Genesis block created automatically.");
        System.out.println();
        return new Blockchain();
    }

    private static void addTransactions(Blockchain ledger) {
        System.out.println("=== STEP 2: ADD TRANSACTIONS ===");
        for (String transaction : DEMO_TRANSACTIONS) {
            ledger.addBlock(transaction);
            System.out.println("Added: " + transaction);
        }
        System.out.println();
    }

    private static void displayChain(Blockchain ledger) {
        System.out.println("=== STEP 3: DISPLAY BLOCKCHAIN ===");
        System.out.println();
        ledger.printChain();
    }

    private static void verifyChain(Blockchain ledger) {
        System.out.println("=== VERIFYING BLOCKCHAIN ===");
        System.out.println();

        if (ledger.isValid()) {
            System.out.println("Blockchain is VALID.");
            System.out.println();
            return;
        }

        System.out.println("Blockchain is INVALID.");
        System.out.println();
        System.out.println("Reason:");
        for (String error : ledger.getValidationErrors()) {
            System.out.println(error);
        }
        System.out.println();
    }

    private static void demonstrateTampering(Blockchain ledger) {
        System.out.println("=== STEP 5: DEMONSTRATE TAMPERING ===");
        Block targetBlock = ledger.getBlock(TAMPER_BLOCK_INDEX);
        System.out.println("Tampering with Block #" + TAMPER_BLOCK_INDEX + " data.");
        System.out.println("Original: " + targetBlock.getData());
        targetBlock.setData(TAMPERED_BLOCK_TWO_DATA);
        System.out.println("Tampered: " + targetBlock.getData());
        System.out.println("Stored hash was NOT recalculated.");
        System.out.println();
    }
}

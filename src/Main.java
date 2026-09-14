import java.util.List;
import java.util.Scanner;

/**
 * Console demonstration of an educational blockchain ledger.
 * Suitable for a short video: create, display, verify, tamper, re-verify.
 */
public class Main {

    private static final boolean USE_COLOR = true;
    private static final long BLOCK_CREATION_DELAY_MILLIS = 50L;
    private static final String DONE_COMMAND = "done";
    private static final String TRANSACTION_PROMPT =
            "Enter transaction (or 'done' to stop, Enter for samples): ";
    private static final String TAMPER_INDEX_PROMPT = "Enter block index to tamper with: ";
    private static final String TAMPER_DATA_PROMPT = "Enter new data for that block: ";
    private static final List<String> DEMO_TRANSACTIONS = List.of(
            "Alice -> Bob: $100",
            "Bob -> Charlie: $50",
            "Charlie -> David: $25",
            "David -> Alice: $75",
            "Alice -> Charlie: $40"
    );

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Blockchain ledger = createBlockchain();
            addTransactions(ledger, scanner);
            displayChain(ledger);
            verifyChain(ledger);
            if (demonstrateTampering(ledger, scanner)) {
                verifyChain(ledger);
            }
        }
    }

    private static Blockchain createBlockchain() {
        System.out.println("=== STEP 1: CREATE BLOCKCHAIN ===");
        System.out.println("Genesis block created automatically.");
        System.out.println();
        return new Blockchain();
    }

    private static void addTransactions(Blockchain ledger, Scanner scanner) {
        System.out.println("=== STEP 2: ADD TRANSACTIONS ===");
        String firstTransaction = readLine(scanner, TRANSACTION_PROMPT);
        if (firstTransaction == null || firstTransaction.isBlank()) {
            addSampleTransactions(ledger);
            return;
        }

        addCustomTransactions(ledger, scanner, firstTransaction);
        System.out.println();
    }

    private static void addSampleTransactions(Blockchain ledger) {
        System.out.println("Using five sample transactions.");
        for (String transaction : DEMO_TRANSACTIONS) {
            addTransaction(ledger, transaction);
        }
        System.out.println();
    }

    private static void addCustomTransactions(
            Blockchain ledger,
            Scanner scanner,
            String firstTransaction) {
        String transaction = firstTransaction;
        while (transaction != null && !DONE_COMMAND.equalsIgnoreCase(transaction)) {
            if (!transaction.isBlank()) {
                addTransaction(ledger, transaction);
            }
            transaction = readLine(scanner, TRANSACTION_PROMPT);
        }
    }

    private static void addTransaction(Blockchain ledger, String transaction) {
        pauseBeforeBlockCreation();
        ledger.addBlock(transaction);
        System.out.println("Added: " + transaction);
    }

    private static void pauseBeforeBlockCreation() {
        try {
            Thread.sleep(BLOCK_CREATION_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Block creation was interrupted.", exception);
        }
    }

    private static void displayChain(Blockchain ledger) {
        System.out.println("=== STEP 3: DISPLAY BLOCKCHAIN ===");
        System.out.println();
        ledger.printChain();
    }

    private static void verifyChain(Blockchain ledger) {
        System.out.println("=== VERIFYING BLOCKCHAIN ===");
        System.out.println();
        printHashComparisons(ledger);

        if (ledger.isValid()) {
            printColored("Blockchain is VALID.", AnsiColors.GREEN);
            System.out.println();
            return;
        }

        printColored("Blockchain is INVALID.", AnsiColors.RED);
        System.out.println();
        System.out.println("Reason:");
        for (String error : ledger.getValidationErrors()) {
            printColored(error, AnsiColors.RED);
        }
        System.out.println();
    }

    private static void printHashComparisons(Blockchain ledger) {
        for (Block block : ledger.getChain()) {
            String recalculatedHash = block.calculateHash();
            boolean isMatch = block.getHash().equals(recalculatedHash);
            String status = isMatch ? "MATCH" : "MISMATCH";
            String color = isMatch ? AnsiColors.GREEN : AnsiColors.RED;
            String comparison = "Block #" + block.getIndex()
                    + ": stored=" + block.getHash()
                    + "  recalculated=" + recalculatedHash
                    + "  -> " + status;
            printColored(comparison, color);
        }
        System.out.println();
    }

    private static boolean demonstrateTampering(Blockchain ledger, Scanner scanner) {
        printColored("=== STEP 5: DEMONSTRATE TAMPERING ===", AnsiColors.YELLOW);
        Integer blockIndex = readValidBlockIndex(ledger, scanner);
        if (blockIndex == null) {
            System.out.println("No input available. Tampering skipped.");
            return false;
        }

        String replacementData = readRequiredLine(scanner, TAMPER_DATA_PROMPT);
        if (replacementData == null) {
            System.out.println("No input available. Tampering skipped.");
            return false;
        }

        tamperWithBlock(ledger.getBlock(blockIndex), replacementData);
        return true;
    }

    private static Integer readValidBlockIndex(Blockchain ledger, Scanner scanner) {
        while (true) {
            String input = readLine(scanner, TAMPER_INDEX_PROMPT);
            if (input == null) {
                return null;
            }
            try {
                int blockIndex = Integer.parseInt(input.trim());
                if (blockIndex >= 0 && blockIndex < ledger.size()) {
                    return blockIndex;
                }
            } catch (NumberFormatException exception) {
                // The message below also covers non-numeric input.
            }
            System.out.println("Enter an index from 0 to " + (ledger.size() - 1) + ".");
        }
    }

    private static String readRequiredLine(Scanner scanner, String prompt) {
        while (true) {
            String input = readLine(scanner, prompt);
            if (input == null || !input.isBlank()) {
                return input;
            }
            System.out.println("New block data cannot be empty.");
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private static void tamperWithBlock(Block targetBlock, String replacementData) {
        printColored(
                "Tampering with Block #" + targetBlock.getIndex() + "...",
                AnsiColors.YELLOW
        );
        System.out.println("Original: " + targetBlock.getData());
        targetBlock.setData(replacementData);
        System.out.println("Tampered: " + targetBlock.getData());
        printColored("Warning: stored hash was NOT recalculated.", AnsiColors.RED);
        System.out.println();
    }

    private static void printColored(String message, String color) {
        System.out.println(AnsiColors.colorize(message, color, USE_COLOR));
    }
}

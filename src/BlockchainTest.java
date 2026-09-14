import java.util.List;

/**
 * Lightweight demonstration tests without external frameworks.
 * Exit code is non-zero if any check fails.
 */
public class BlockchainTest {

    private static final int MINIMUM_TRANSACTION_BLOCKS = 5;
    private static final int TAMPER_BLOCK_INDEX = 2;
    private static final String TAMPERED_DATA = "Bob -> Charlie: $5000";
    private static final String TAMPERED_PREVIOUS_HASH = "deadbeef";
    private static final List<String> SAMPLE_TRANSACTIONS = List.of(
            "Alice -> Bob: $100",
            "Bob -> Charlie: $50",
            "Charlie -> David: $25",
            "David -> Alice: $75",
            "Alice -> Charlie: $40"
    );

    private static int failureCount = 0;

    public static void main(String[] args) {
        testNormalChainIsValid();
        testModifiedDataIsInvalid();
        testModifiedPreviousHashIsInvalid();
        testChainHasAtLeastFiveHashedBlocks();
        testExposedHashInputProducesStoredHash();

        if (failureCount == 0) {
            System.out.println("All tests PASSED.");
            return;
        }

        System.out.println(failureCount + " test(s) FAILED.");
        System.exit(1);
    }

    private static void testNormalChainIsValid() {
        Blockchain ledger = buildSampleChain();
        assertTrue("Test 1 — Normal chain is VALID", ledger.isValid());
    }

    private static void testModifiedDataIsInvalid() {
        Blockchain ledger = buildSampleChain();
        ledger.getBlock(TAMPER_BLOCK_INDEX).setData(TAMPERED_DATA);

        boolean isInvalid = !ledger.isValid();
        boolean hasHashMismatch = containsSubstring(
                ledger.getValidationErrors(),
                "hash does not match"
        );

        assertTrue("Test 2 — Modified block data is INVALID", isInvalid && hasHashMismatch);
    }

    private static void testModifiedPreviousHashIsInvalid() {
        Blockchain ledger = buildSampleChain();
        ledger.getBlock(TAMPER_BLOCK_INDEX).setPreviousHash(TAMPERED_PREVIOUS_HASH);

        boolean isInvalid = !ledger.isValid();
        boolean hasLinkMismatch = containsSubstring(
                ledger.getValidationErrors(),
                "previousHash does not match"
        );

        assertTrue("Test 3 — Modified previous hash is INVALID", isInvalid && hasLinkMismatch);
    }

    private static void testChainHasAtLeastFiveHashedBlocks() {
        Blockchain ledger = buildSampleChain();
        int transactionBlockCount = ledger.size() - 1;
        assertTrue(
                "Test 4 — Chain has at least five SHA-256 blocks",
                transactionBlockCount >= MINIMUM_TRANSACTION_BLOCKS
        );
    }

    private static void testExposedHashInputProducesStoredHash() {
        Blockchain ledger = buildSampleChain();
        Block block = ledger.getBlock(TAMPER_BLOCK_INDEX);
        String hashFromExposedInput = HashUtil.sha256(block.getHashInput());
        assertTrue(
                "Test 5 — Exposed input produces the stored hash",
                block.getHash().equals(hashFromExposedInput)
        );
    }

    private static Blockchain buildSampleChain() {
        Blockchain ledger = new Blockchain();
        for (String transaction : SAMPLE_TRANSACTIONS) {
            ledger.addBlock(transaction);
        }
        return ledger;
    }

    private static boolean containsSubstring(List<String> messages, String fragment) {
        for (String message : messages) {
            if (message.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + testName);
            return;
        }
        failureCount++;
        System.out.println("FAIL: " + testName);
    }
}

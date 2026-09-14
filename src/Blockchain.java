import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordered chain of blocks linked by previousHash values.
 * Validation recalculates hashes so tampering is easy to detect.
 */
public class Blockchain {

    private static final int GENESIS_INDEX = 0;
    private static final String GENESIS_PREVIOUS_HASH = "0";
    private static final String GENESIS_DATA = "Genesis Block";
    private static final String INVALID_PREFIX = "[INVALID] ";

    private final List<Block> chain = new ArrayList<>();

    public Blockchain() {
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        long timestamp = System.currentTimeMillis();
        return new Block(GENESIS_INDEX, timestamp, GENESIS_DATA, GENESIS_PREVIOUS_HASH);
    }

    public void addBlock(String data) {
        Block previousBlock = getLatestBlock();
        int nextIndex = previousBlock.getIndex() + 1;
        long timestamp = System.currentTimeMillis();
        Block newBlock = new Block(nextIndex, timestamp, data, previousBlock.getHash());
        chain.add(newBlock);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException("Block index out of range: " + index);
        }
        return chain.get(index);
    }

    public int size() {
        return chain.size();
    }

    public List<Block> getChain() {
        return Collections.unmodifiableList(chain);
    }

    public void printChain() {
        for (Block block : chain) {
            System.out.println(block.toDisplayString());
            System.out.println();
        }
    }

    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Collects every validation failure so the demo can show both
     * hash mismatch and broken previousHash links after tampering.
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        validateGenesis(errors);

        for (int index = 1; index < chain.size(); index++) {
            Block currentBlock = chain.get(index);
            Block previousBlock = chain.get(index - 1);
            validateStoredHash(currentBlock, errors);
            validatePreviousHashLink(currentBlock, previousBlock, errors);
        }

        return errors;
    }

    private void validateGenesis(List<String> errors) {
        Block genesis = chain.get(GENESIS_INDEX);
        validateStoredHash(genesis, errors);

        if (!GENESIS_PREVIOUS_HASH.equals(genesis.getPreviousHash())) {
            errors.add(INVALID_PREFIX + "Genesis previousHash must be \""
                    + GENESIS_PREVIOUS_HASH + "\".");
        }
    }

    private void validateStoredHash(Block block, List<String> errors) {
        String calculatedHash = block.calculateHash();
        if (calculatedHash.equals(block.getHash())) {
            return;
        }
        errors.add(INVALID_PREFIX + "Block #" + block.getIndex()
                + " hash does not match its calculated hash.");
    }

    private void validatePreviousHashLink(
            Block currentBlock,
            Block previousBlock,
            List<String> errors) {
        String expectedPreviousHash = previousBlock.calculateHash();
        if (expectedPreviousHash.equals(currentBlock.getPreviousHash())) {
            return;
        }
        errors.add(INVALID_PREFIX + "Block #" + currentBlock.getIndex()
                + " previousHash does not match Block #" + previousBlock.getIndex() + ".");
    }
}

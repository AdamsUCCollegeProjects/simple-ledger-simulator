import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * One block in the ledger chain.
 * The stored hash is set at creation and is NOT updated when data is later changed,
 * so tampering can be demonstrated clearly.
 */
public class Block {

    private static final String DISPLAY_SEPARATOR = "========================================";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final int index;
    private final long timestamp;
    private String data;
    private String previousHash;
    private final String hash;

    public Block(int index, long timestamp, String data, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String content = index + Long.toString(timestamp) + data + previousHash;
        return HashUtil.sha256(content);
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    /**
     * Changes transaction data without recalculating the stored hash.
     * Used only for the educational tampering demonstration.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Changes the previous-hash link without recalculating the stored hash.
     * Used only for the educational tampering demonstration.
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String toDisplayString() {
        String formattedTimestamp = TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(timestamp));
        return DISPLAY_SEPARATOR + "\n"
                + "BLOCK #" + index + "\n"
                + DISPLAY_SEPARATOR + "\n"
                + "Timestamp    : " + formattedTimestamp + "\n"
                + "Data         : " + data + "\n"
                + "Previous Hash: " + previousHash + "\n"
                + "Hash         : " + hash + "\n"
                + DISPLAY_SEPARATOR;
    }
}

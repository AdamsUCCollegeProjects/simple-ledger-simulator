# Blockchain Ledger Simulator — Report

## 1. Introduction

This project is a small Java program made for learning. It shows the basic ideas behind a blockchain — how data is "locked" using a hash, and how blocks are connected to each other — without the complicated parts of a real cryptocurrency system.

The project focuses on three simple ideas:

1. Each block stores some transaction data (example: "Alice pays Bob $50") plus some extra info about itself.
2. Each block creates its own unique code, called a hash, using something called SHA-256.
3. Each new block stores the hash of the block before it. This connects all the blocks into a chain. If someone changes an old block, the chain becomes "broken" (invalid).

## 2. Project Objective

Our goal was to build a simple console program (text-only, no graphics) that can:

- Create the first block, called the genesis block
- Add several transaction blocks after it
- Show the whole chain on the screen
- Check if the chain is still valid (unchanged)
- Show what happens when someone secretly changes a block — the chain should catch it

On purpose, we did NOT include some advanced features that real blockchains have, such as:
- Networking (multiple computers talking to each other)
- Wallets
- Digital signatures
- Mining or proof of work
- Consensus (computers agreeing with each other)
- Databases

These features are important in real systems, but adding them would make it harder to focus on the main lesson: hashing and linking blocks.

## 3. Blockchain Structure

Our code has four Java files (classes):

- `HashUtil` — creates the SHA-256 hash codes
- `Block` — represents one entry (one block) in the ledger
- `Blockchain` — manages the full list of blocks and checks if the chain is valid
- `Main` — runs the demonstration, used for our presentation/video

Each `Block` stores this information:

| Field | What it means |
|---|---|
| `index` | The block's position in the chain (0, 1, 2, 3...) |
| `timestamp` | The exact time the block was created |
| `data` | The transaction text (example: a fake payment) |
| `previousHash` | The hash of the block right before this one |
| `hash` | This block's own SHA-256 hash |

The very first block is called the genesis block. It always has `index = 0`, `previousHash = "0"` (since there's nothing before it), and `data = "Genesis Block"`. After that, we add more blocks using a method called `addBlock(String data)`.

## 4. SHA-256 Hashing

To create a hash, we use a tool that is already built into Java, called `MessageDigest`, with a method named `"SHA-256"`. This tool turns any text into a fixed-length code made of letters and numbers.

To create a block's hash, we combine these pieces of information together:

- index
- timestamp
- data
- previousHash

An important property of SHA-256 is: if you give it the exact same information, you always get the exact same hash back. But if you change even one small thing (a single letter or number), the hash comes out completely different. This is exactly how we detect if someone has tampered with (secretly changed) a block.

## 5. Block Linking

Here is what happens, step by step, when we add a new block:

1. We look at the most recent block already in the chain
2. We give the new block the next index number
3. We record the current time as its timestamp
4. We copy the previous block's hash into our new block's `previousHash` field
5. We calculate the new block's own SHA-256 hash and save it
6. We add the finished block to the chain

This creates a one-directional link between blocks, like this:

```text
Block #0's hash → stored inside Block #1 as "previousHash"
Block #1's hash → stored inside Block #2 as "previousHash"
Block #2's hash → stored inside Block #3 as "previousHash"
```

Because of this, if someone changes an old block, the newer blocks will no longer correctly "point back" to it — this is how the chain shows something is wrong.

## 6. Chain Verification (Checking If the Chain Is Valid)

We wrote a method called `getValidationErrors()` that checks every single block and makes a list of anything wrong that it finds. Another method, `isValid()`, simply returns "true" if that list is empty (no problems found).

Here's what the checker looks for:

1. For every block (including the genesis block), it recalculates the hash using the block's current data, then compares it to the hash that was originally stored. If they don't match, something changed.
2. It checks that the genesis block's `previousHash` is still `"0"`.
3. For every block after genesis, it checks that its stored `previousHash` matches the hash we get by *recalculating* the previous block's hash right now (not just trusting the old stored value).

This last point matters a lot for teaching purposes. Because we recalculate instead of just comparing old stored values, when someone tampers with Block #2, our program can catch and clearly report BOTH of these problems:

- Block #2's stored hash no longer matches what we calculate from its (changed) data
- Block #3 is still pointing to Block #2's *original* hash, which no longer exists

## 7. Tampering Demonstration (Simulating an Attack)

Here is the step-by-step flow used in our `Main` demonstration:

1. Create the blockchain (this automatically includes the genesis block)
2. Add five example transactions
3. Print out the entire chain so we can see it
4. Check that the chain is valid — it should say **VALID**
5. Secretly change the data in Block #2, from `Bob -> Charlie: $50` to `Bob -> Charlie: $5000`, without updating its saved hash (just like a real attacker might try to do)
6. Check the chain again — this time it should say **INVALID**, and explain exactly why

One important detail: in our code, the methods `Block.setData(...)` and `Block.setPreviousHash(...)` do NOT automatically recalculate the hash when you use them. This is on purpose! In a normal, honest situation, a new hash would always be calculated whenever data changes. By skipping that step here, we are simulating what an unauthorized/sneaky change would look like — someone changing the data but "forgetting" (or being unable) to fix the hash to match.

## 8. Implementation Details (Tools We Used)

What we used to build this:

- Java 25
- Only Java's built-in standard library — no outside tools or downloads needed
- A simple console (text) interface
- No Maven or Gradle (tools that manage external code libraries) — we didn't need them
- No JUnit (a common testing tool) — instead we wrote our own simple `BlockchainTest` class that checks things step by step

What we focused on for code quality:

- Each class has one clear job
- Methods (functions) are kept short and easy to follow
- We used named constants (fixed values with clear names) for things like the genesis block's info and demo text
- We only added comments where they help explain the blockchain concept, not just to explain basic code

We designed the project so that a student could open `Block.java` and `Blockchain.java` and explain the whole hashing and linking idea in just a few minutes.

## 9. Testing and Results

Our `BlockchainTest` file checks four situations:

1. **Normal chain** — we add five transactions after the genesis block; we expect the result to be VALID.
2. **Modified block data** — we change the data inside Block #2; we expect the result to be INVALID, because the hash won't match anymore.
3. **Modified previous hash** — we directly change a block's `previousHash` value; we expect the result to be INVALID, because the link between blocks is now broken.
4. **Five or more blocks with hashes** — we confirm the chain has the genesis block plus at least five more blocks, and that all of them have valid SHA-256 hashes.

When we run `Main` by hand, we see the same result: the untouched chain shows as valid, and once we tamper with it, the program shows clear "[INVALID]" messages explaining exactly what went wrong.

## 10. Conclusion

This project helps show why blockchain-style systems are good at catching secret changes to data. Each block "locks in" its own content using a cryptographic hash, and every later block also stores a copy of the hash before it. So if someone tries to change old data without updating every single hash after it (which is very hard to do), the chain will notice and show that something is wrong.

To be clear, this project is not trying to copy or fully represent real systems like Bitcoin or Ethereum. Its purpose is to teach the core idea in a simple, understandable way. By keeping the project small and showing everything clearly on the screen, it becomes much easier to see, explain, and demonstrate how SHA-256 hashing, block linking (`previousHash`), and tamper detection work together.
# Blockchain Ledger Simulator

## Project Info

- **Group:** 1
- **Topic:** Blockchain Ledger Simulator

### Team Members

1. Yen Phary
2. Chroeng Simleng
3. Kech Kheang
4. Phort Sopheakdei
5. Samouen Rachana
6. Sroem Meng
7. Phorn Sros
8. Ros Sopheak
9. Seng Tharom

## What This Project Is

This is a simple Java program that runs in the console (no graphics, just text).

It shows how a **blockchain** works in a simple way. A blockchain is like a chain of blocks, and each block holds some data (in our case, fake transactions like "Alice pays Bob $50"). Each block is connected to the one before it using something called a **hash**.

A **hash** is just a short code (a string of letters and numbers) that is created from the data in a block. If you change even one letter in the data, the hash changes completely. This is how we can tell if someone tried to change the data later — it's like a tamper-proof seal.

**Important:** This project is only for learning. It is NOT a real blockchain like Bitcoin. It does not have:
- Networking (computers talking to each other)
- Wallets
- Mining
- Proof of work
- Consensus (multiple computers agreeing on the data)

It's a simplified model to help us understand and explain the *idea* behind blockchains.

## What the Program Does

- Creates a hash (a unique code) for each block using a method called SHA-256 (Java has this built in, we don't need extra libraries)
- Creates the very first block, called the **genesis block**
- Connects each block to the previous one by storing the previous block's hash
- Shows the whole chain of blocks on the screen
- Checks if the chain is still valid (not tampered with) and shows clear messages if something is wrong
- Demonstrates what happens if someone tries to secretly change data in a block

## How It Works (Simple Diagram)

**Normal flow — creating a block:**

```text
Block data (example: "Alice pays Bob $50")
    ↓
Turn it into a hash (SHA-256)
    ↓
This hash is now the block's "hash"
    ↓
The NEXT block stores this hash as its "previousHash"
```

This is how blocks are linked together, like a chain.

**What happens if someone tampers with (secretly changes) a block:**

```text
Someone changes the data in a block
    ↓
If we recalculate the hash now, it comes out different
    ↓
But the hash stored in the block is still the OLD one
    ↓
The next block still remembers the OLD hash too
    ↓
So when we check the chain, it fails validation
```

This is the main point of the project: any change to old data breaks the chain and can be detected.

## What You Need to Run This

- Java 25 (we recommend the "Temurin" version)
- No extra libraries needed — everything used is already built into Java

### Setting Up Java with SDKMAN (optional tool to manage Java versions)

If you use SDKMAN to install Java, run these commands in your terminal:

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25.0.4-tem
sdk use java 25.0.4-tem
java -version
javac -version
```

The last two commands just check that Java installed correctly.

## Project Files

```text
src/
  Main.java           <- runs the demo
  Block.java           <- describes what a "block" is
  Blockchain.java       <- manages the full chain of blocks
  HashUtil.java         <- creates the SHA-256 hashes
  BlockchainTest.java    <- simple tests to check everything works
README.md
REPORT.md
```

## How to Run the Program

**Step 1: Compile the code** (this turns the human-readable code into something the computer can run)

```bash
mkdir -p out
javac -d out src/*.java
```

**Step 2: Run the demo**

```bash
java -cp out Main
```

At the transaction prompt, enter your own transactions and type `done` when finished.
Press Enter on the first prompt to use the five sample transactions instead. The program
then asks which block to tamper with and what replacement data to use.

The demo uses ANSI colors for validation results. Set `USE_COLOR` to `false` near the top
of `Main.java` if your terminal does not support ANSI colors.

**Step 3 (optional): Run the tests** (these automatically check that the code behaves correctly)

```bash
java -cp out BlockchainTest
```

## Example Output

When you run `Main`, the program will:

1. Create a blockchain, starting with the genesis block
2. Ask for live transactions, with an Enter-to-use-samples fallback
3. Print every block's exact hashing input and the SHA-256 hash computed from it
4. Compare every stored hash with a fresh recalculation and show a colored **MATCH**
5. Ask you to choose a block and enter replacement data
6. Check the chain again and show colored **MISMATCH** and **INVALID** results

```text
Enter transaction (or 'done' to stop, Enter for samples):
Using five sample transactions.
...
Hashing input : "2#1726331760123#Bob -> Charlie: $50#466d8c27..."
Computed hash : 1945ec0d...
...
Block #2: stored=1945ec0d...  recalculated=7f3ac0e1...  -> MISMATCH
Blockchain is INVALID.
```

## Understanding the Tampering Demo

This is the most important part of the project to understand, so here it is step by step:

1. We secretly change the data inside Block #2, but leave its stored hash the same
2. When the program recalculates Block #2's hash from the new data, it gets a different result than what's stored — so this is the first sign something's wrong
3. Block #3 was created based on Block #2's *original* hash. Since that original hash no longer matches, this is the second sign something's wrong
4. Because of this, both checks fail — which proves the chain is very good at catching tampering

We designed it to fail in two places on purpose. It makes it much easier to explain and show in a short presentation or video, since you can clearly point to two separate reasons why the tampering was caught.

## License

This is an educational project made for a class assignment and demonstration purposes only.
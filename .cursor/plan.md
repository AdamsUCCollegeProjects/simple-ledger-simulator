# Blockchain Ledger Simulator

## 1. Project Goal

Build a small educational blockchain ledger simulator in Java.

The application must demonstrate:

* How blocks store transaction data.
* How SHA-256 is used to generate a block hash.
* How blocks are linked using `previousHash`.
* How modifying an existing block causes the blockchain to become invalid.
* How the entire chain can be displayed and verified.

This is an educational simulator, NOT a production blockchain.

Do not implement unnecessary blockchain features such as:

* Networking
* Peer-to-peer communication
* Wallets
* Digital signatures
* Cryptocurrency
* Mining difficulty
* Proof of Work
* Consensus mechanisms
* Databases
* External APIs

Keep the implementation simple, readable, and easy to explain in a college presentation.

---

# 2. Technology

Use:

* Java
* Java Standard Library only
* SHA-256 via Java's built-in `MessageDigest`
* Console-based interface

Do not introduce third-party dependencies unless absolutely necessary.

The project should be runnable locally with standard Java tooling.

---

# 3. Project Structure

Use a simple structure similar to:

```text
BlockchainLedger/
├── src/
│   ├── Main.java
│   ├── Block.java
│   ├── Blockchain.java
│   └── HashUtil.java
├── README.md
└── REPORT.md
```

Keep responsibilities clearly separated.

---

# 4. HashUtil

Create a `HashUtil` utility class.

Responsibilities:

* Generate SHA-256 hashes.
* Accept a `String`.
* Return the hash as a hexadecimal string.

Example API:

```java
public static String sha256(String input)
```

Use:

```java
MessageDigest.getInstance("SHA-256")
```

Do not implement SHA-256 manually.

The hash should be deterministic: identical input must always produce identical output.

---

# 5. Block

Create a `Block` class representing one block in the chain.

Each block must contain:

```text
index
timestamp
data
previousHash
hash
```

Suggested types:

```text
index        -> int
timestamp    -> long or appropriate Java time representation
data         -> String
previousHash -> String
hash         -> String
```

The block's hash must be calculated from its important contents, including at minimum:

```text
index
timestamp
data
previousHash
```

Use SHA-256.

A block should calculate its own hash when it is created.

Provide appropriate getters.

For the educational demonstration, allow the block's data to be modified so that the application can demonstrate tampering.

Important:

If the data is changed after creation, DO NOT automatically update the stored hash.

This is necessary for the tampering demonstration.

---

# 6. Blockchain

Create a `Blockchain` class containing an ordered collection of blocks.

Responsibilities:

* Create the genesis block.
* Add new blocks.
* Return/display the chain.
* Validate the chain.

Use a simple collection such as:

```java
List<Block>
```

## Genesis Block

The blockchain must begin with a genesis block.

The genesis block should:

```text
index = 0
previousHash = "0"
data = "Genesis Block"
```

Its hash should also be calculated using SHA-256.

---

# 7. Adding Blocks

Provide a method similar to:

```java
addBlock(String data)
```

When adding a block:

1. Get the previous block.
2. Set the new block's index.
3. Generate the current timestamp.
4. Copy the previous block's hash into `previousHash`.
5. Calculate the new block's SHA-256 hash.
6. Add the block to the chain.

The new block must reference the actual hash of the previous block.

Example:

```text
Block #0
hash = ABC123

Block #1
previousHash = ABC123
hash = DEF456

Block #2
previousHash = DEF456
hash = GHI789
```

---

# 8. Chain Validation

Implement a method similar to:

```java
isValid()
```

The method must verify every block after the genesis block.

For each block:

## Check 1 — Stored hash

Recalculate the block's hash from its current contents.

Compare:

```text
calculatedHash
vs
storedHash
```

If they differ, the block has been modified.

The chain must be invalid.

## Check 2 — Previous hash

Compare:

```text
currentBlock.previousHash
vs
previousBlock.hash
```

If they differ, the chain must be invalid.

This demonstrates why changing an older block also breaks the links to subsequent blocks.

Return:

```text
true
```

only if every block passes validation.

---

# 9. Displaying the Blockchain

Create a readable console representation.

For every block, display something similar to:

```text
========================================
BLOCK #2
========================================
Timestamp    : 2026-09-14 17:20:00
Data         : Alice -> Bob: $100
Previous Hash: 91a7f3...
Hash         : 4bd821...
========================================
```

Do not print unnecessarily enormous amounts of information.

Full hashes may be displayed because the project is specifically demonstrating hashing.

---

# 10. Main Demonstration

The `Main` class should provide a simple demonstration that can be used directly in the 3–5 minute video.

The demonstration should follow this exact flow:

## Step 1 — Create blockchain

Create the blockchain and automatically create the genesis block.

## Step 2 — Add transactions

Add at least five blocks containing example transactions.

For example:

```text
Alice -> Bob: $100
Bob -> Charlie: $50
Charlie -> David: $25
David -> Alice: $75
Alice -> Charlie: $40
```

This ensures the project clearly contains at least five SHA-256-generated blocks in addition to the genesis block.

## Step 3 — Display chain

Print all blocks and their:

* Index
* Timestamp
* Data
* Previous hash
* Hash

## Step 4 — Verify chain

Print:

```text
=== VERIFYING BLOCKCHAIN ===

Blockchain is VALID.
```

## Step 5 — Demonstrate tampering

Select one existing block, preferably Block #2.

Change its data without recalculating its stored hash.

Example:

```text
Original:
Bob -> Charlie: $50

Tampered:
Bob -> Charlie: $5000
```

## Step 6 — Verify again

Run validation again.

The output should clearly indicate:

```text
=== VERIFYING BLOCKCHAIN ===

Blockchain is INVALID.

Reason:
Block #2 hash does not match its calculated hash.
```

Because Block #3 still references the original hash of Block #2, validation should also demonstrate the broken link:

```text
Block #3 previousHash does not match Block #2 hash.
```

The exact error-reporting implementation can be designed sensibly, but it must clearly demonstrate both types of invalidation.

---

# 11. Validation Output

Do not make validation simply return `false` with no explanation.

For the demonstration, provide useful messages identifying why validation failed.

For example:

```text
[INVALID] Block #2 hash mismatch.
[INVALID] Block #3 previous hash does not match Block #2.
```

This makes the concept easy to demonstrate in the video and report.

---

# 12. README

Create a clear `README.md`.

Include:

## Project Overview

Explain what the simulator does.

## Features

* SHA-256 hashing
* Block creation
* Genesis block
* Linked blocks
* Blockchain validation
* Tampering detection
* Console demonstration

## How It Works

Explain:

```text
Block data
    ↓
SHA-256
    ↓
Block hash
    ↓
Stored in next block as previousHash
```

## Requirements

Specify the required Java version.

## How to Run

Provide straightforward commands for compiling and running the project.

## Example

Show a short example of the application output.

## Tampering Demonstration

Explain how modifying a block causes the chain to become invalid.

---

# 13. Report

Create `REPORT.md` as a draft for the required 2+ page report.

Include these sections:

1. Introduction
2. Project Objective
3. Blockchain Structure
4. SHA-256 Hashing
5. Block Linking
6. Chain Verification
7. Tampering Demonstration
8. Implementation Details
9. Testing and Results
10. Conclusion

The report should explain the implementation rather than making unsupported claims about real-world blockchain systems.

---

# 14. Testing

Include tests or a clearly structured demonstration covering:

### Test 1 — Normal chain

Expected:

```text
VALID
```

### Test 2 — Modify block data

Expected:

```text
INVALID
```

because the stored hash no longer matches the calculated hash.

### Test 3 — Modify a previous hash

Expected:

```text
INVALID
```

because the block no longer correctly references the previous block.

### Test 4 — Five or more blocks

Confirm that the chain contains at least five SHA-256-generated blocks.

---

# 15. Code Quality Requirements

Keep the code:

* Simple
* Readable
* Well-named
* Properly separated into classes
* Easy for students to explain

Avoid:

* Overengineering
* Excessive design patterns
* Unnecessary abstractions
* Frameworks
* External dependencies
* Complex generics
* Networking
* Database persistence

Add comments only where they help explain the blockchain concept.

Do not generate huge amounts of boilerplate.

---

# 16. Important Educational Constraint

The implementation should make the concept obvious.

The central demonstration is:

```text
Original Block
      ↓
SHA-256 Hash
      ↓
Next Block stores that hash
      ↓
Next Block
      ↓
Next Block
```

After tampering:

```text
Modified Block
      ↓
Its calculated hash changes
      ↓
Stored hash is now incorrect
      ↓
Next block still references the old hash
      ↓
Chain validation fails
```

The final project should make this behavior easy to see both in the console output and in the source code.

---

# 17. Final Deliverables

The finished project must provide:

* Working Java source code
* At least five SHA-256-generated blocks
* Genesis block
* Blockchain display
* Blockchain verification
* Tampering demonstration
* Chain invalidation after tampering
* Clear README
* 2+ page report draft
* Console flow suitable for a 3–5 minute video demonstration

Before considering the project complete, verify that all assignment requirements are explicitly satisfied.

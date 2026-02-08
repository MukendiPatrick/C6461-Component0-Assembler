# C6461 Assembler - User Guide
## Component 0 - CS6461 Computer Architecture

**Version:** 1.0  
**Team:** Team D: Chandle Mubashir,
Tshiswaka Mukendi, Miguel Martinez 
**Date:** 02/07/2026

---

## Table of Contents
1. [Quick Start](#quick-start)
2. [Installation](#installation)
3. [Running the Assembler](#running-the-assembler)
4. [Writing Assembly Programs](#writing-assembly-programs)
5. [Understanding Output](#understanding-output)
6. [Troubleshooting](#troubleshooting)
7. [Examples](#examples)

---

## Quick Start

**Fastest way to run the assembler:**
```bash
java -jar C6461_Assembler.jar test/test_program.asm
```

**Output:**
- `test_program.lst` - Listing file with addresses and machine code
- `test_program.load` - Load file for simulator

---

## Installation

### Prerequisites

- **Java:** JDK 11 or later
    - Check: `java -version`
    - Download: https://www.oracle.com/java/technologies/downloads/

- **IntelliJ IDEA** (for development)
    - Download: https://www.jetbrains.com/idea/download/

### Setup

1. **Clone the repository:**
```bash
   git clone https://github.com/miguelmartinezm-gwu/CS6461-Component0-Assembler.git
   cd CS6461-Component0-Assembler
```

2. **Open in IntelliJ:**
    - File → Open → Select project folder
    - IntelliJ will auto-configure

3. **Verify JAR file exists:**
    - Check: `out/artifacts/C6461_Assembler_jar/C6461_Assembler.jar`
    - If missing, see "Building from Source" below

---

## Running the Assembler

### Method 1: Using JAR File (Recommended)
```bash
java -jar C6461_Assembler.jar <input_file.asm>
```

**Example:**
```bash
java -jar C6461_Assembler.jar test/test_program.asm
```

**With custom output files:**
```bash
java -jar C6461_Assembler.jar input.asm output.lst output.load
```

### Method 2: Using IntelliJ

1. Open project in IntelliJ
2. Run → Run 'C6461Assembler'
3. Check `test/` folder for output files

### Method 3: From Compiled Classes
```bash
java -cp out/production/C6461_Assembler edu.gwu.cs6461.assembler.C6461Assembler test/test_program.asm
```

---

## Writing Assembly Programs

### File Format

Assembly source files use `.asm` extension (by convention).

**Line Format:**
```
[label:] [opcode] [operand1,operand2,...] [;comment]
```

### Components

#### Labels (Optional)
- Start at beginning of line
- End with colon `:`
- Used to mark locations
- Can be referenced before they're defined (forward reference)

**Example:**
```assembly
Start: LDR 0,0,20
Loop:  AIR 0,1
End:   HLT
```

#### Opcodes (Required)
- Instruction mnemonic OR directive
- Not case-sensitive (converted to uppercase)

**Instructions:** LDR, STR, LDA, LDX, STX, JZ, JNE, HLT, etc.

**Directives:** LOC, Data

#### Operands (Varies by instruction)
- Comma-separated, no spaces
- Can be numbers or labels

**Example:**
```assembly
LDR 0,0,20     ; Three operands: 0, 0, 20
JZ 0,0,End     ; Third operand is label
```

#### Comments (Optional)
- Start with semicolon `;`
- Rest of line is ignored

**Example:**
```assembly
LDR 0,0,20  ;This is a comment
;This entire line is a comment
```

### Directives

#### LOC - Set Location Counter

**Syntax:** `LOC <decimal_address>`

**Purpose:** Set where next instruction/data will be placed

**Example:**
```assembly
 LOC 10      ;Start at address 10
 LDR 0,0,20  ;This will be at address 10
 HLT         ;This will be at address 11

 LOC 100     ;Jump to address 100
 Data 42     ;This will be at address 100
```

#### Data - Define Data

**Syntax:** `Data <value_or_label>`

**Purpose:** Allocate one word (16 bits) with initial value

**Examples:**
```assembly
 Data 42      ;Store decimal 42
 Data 0       ;Store 0
 Data Start   ;Store address of label 'Start'
```

### Instructions

#### Load/Store Instructions

**Format:** `opcode r, ix, address[,I]`

**Parameters:**
- `r` = Register (0-3)
- `ix` = Index register (0-3, where 0 means no indexing)
- `address` = Memory address or label (0-31 direct)
- `I` = Indirect flag (optional)

**Examples:**
```assembly
LDR 0,0,20       ;Load R0 from address 20
STR 1,0,25       ;Store R1 to address 25
LDA 2,0,30       ;Load address 30 into R2
LDX 1,0,15       ;Load index register X1 from address 15
STX 2,0,16       ;Store index register X2 to address 16
```

**With indexing:**
```assembly
LDX 1,0,100      ;X1 = 100
LDR 0,1,5        ;Load from address (100 + 5) = 105
```

**With indirect addressing:**
```assembly
LDR 0,0,20,I     ;Load from address stored at location 20
```

#### Arithmetic Instructions

**Immediate format:** `opcode r, immed`
```assembly
AIR 0,10         ;Add 10 to R0
SIR 1,5          ;Subtract 5 from R1
```

**Memory format:** `opcode r, ix, address[,I]`
```assembly
AMR 0,0,20       ;Add memory[20] to R0
SMR 1,0,21       ;Subtract memory[21] from R1
```

**Register format:** `opcode rx, ry`
```assembly
MLT 0,2          ;Multiply R0 by R2 (result in R0:R1)
DVD 0,2          ;Divide R0 by R2 (quotient in R0, remainder in R1)
```

#### Logical Instructions
```assembly
AND 0,1          ;R0 = R0 AND R1
ORR 0,2          ;R0 = R0 OR R2
NOT 0            ;R0 = NOT R0
TRR 0,1          ;Test if R0 == R1, set condition code
```

#### Branch Instructions

**Format:** `opcode r, ix, address[,I]`
```assembly
JZ 0,0,Loop      ;Jump to Loop if R0 == 0
JNE 1,0,End      ;Jump to End if R1 != 0
JMA 0,Start      ;Unconditional jump to Start
JSR 0,Sub        ;Jump to subroutine at Sub
JGE 0,0,Pos      ;Jump to Pos if R0 >= 0
SOB 2,0,Loop     ;Subtract 1 from R2, branch if > 0
```

#### Shift/Rotate Instructions

**Format:** `opcode r, count, lr, al`

**Parameters:**
- `r` = Register (0-3)
- `count` = Number of positions (0-15)
- `lr` = Left(1) or Right(0)
- `al` = Arithmetic(0) or Logical(1)
```assembly
SRC 0,3,1,1      ;Shift R0 left 3 bits, logical
SRC 1,2,0,0      ;Shift R1 right 2 bits, arithmetic
RRC 2,4,1,1      ;Rotate R2 left 4 bits
```

#### I/O Instructions

**Format:** `opcode r, devid`
```assembly
IN 0,0           ;Input from keyboard to R0
OUT 1,1          ;Output R1 to printer
CHK 2,0          ;Check device 0 status
```

#### Miscellaneous
```assembly
HLT              ;Halt execution
TRAP 5           ;Trap to routine 5
```

---

## Understanding Output

### Listing File (.lst)

Shows assembled code with addresses and machine code in octal.

**Format:**
```
<address> <value> <source_line>
```

**Example:**
```
                     LOC 10
000012 003420 Start: LDR 0,0,20    ;Load R0
000013 014005  AIR 0,5         ;Add 5
000014 004520  STR 0,0,21      ;Store result
000015 000000  HLT             ;Stop
                     LOC 20
000024 000144  Data 100        ;Initial value
000025 000000  Data 0          ;Result
```

**Columns:**
1. **Address** (octal) - Where instruction is in memory
2. **Value** (octal) - Machine code for instruction
3. **Source** - Your original assembly line

**Note:** Lines without code (LOC, comments, labels-only) show blank in first two columns.

### Load File (.load)

Machine code ready for simulator. Only address-value pairs.

**Format:**
```
<address> <value>
```

**Example:**
```
000012 003420
000013 014005
000014 004520
000015 000000
000024 000144
000025 000000
```

**Usage:** This file is loaded into the C6461 simulator.

---

## Troubleshooting

### Common Errors

#### "I/O Error: file.asm (No such file or directory)"

**Problem:** File path is wrong

**Solution:**
- Check file path spelling
- Use relative path from project root
- Example: `test/test_program.asm`

#### "ERROR: Line 5: Duplicate label 'Start'"

**Problem:** Same label defined twice

**Solution:**
- Use unique label names
- Check for typos in labels

#### "ERROR: Line 12: Unknown instruction 'LOAD'"

**Problem:** Invalid instruction mnemonic

**Solution:**
- Use correct mnemonic (LDR, not LOAD)
- Check spelling
- See instruction reference in README.md

#### "ERROR: Line 8: Missing operand 1"

**Problem:** Not enough operands for instruction

**Solution:**
- Check instruction format
- LDR needs 3 operands: `LDR r,ix,address`

#### "ERROR: Line 10: Invalid DATA value 'End'"

**Problem:** Label referenced before it's defined

**Solution:**
- Make sure label exists somewhere in file
- Check label spelling

### No Output Files Created

**Check:**
1. Did assembly complete successfully?
2. Any error messages in console?
3. Do you have write permission in output directory?

### Wrong Machine Code Values

**Check:**
1. Operand order correct? (r, ix, address)
2. Decimal vs octal confusion? (input is decimal, output is octal)
3. Indirect flag needed? (,I at end)

---

## Examples

### Example 1: Simple Addition
```assembly
; Add two numbers
 LOC 10

Main: LDR 0,0,20     ;Load first number
 LDR 1,0,21     ;Load second number
 AMR 0,0,21     ;Add second to first
 STR 0,0,22     ;Store result
 HLT            ;Stop

 LOC 20
 Data 15        ;First number
 Data 25        ;Second number
 Data 0         ;Result location
```

**Output (test.lst):**
```
                     LOC 10
000012 003424 Main: LDR 0,0,20     ;Load first number
000013 002425  LDR 1,0,21     ;Load second number
000014 010425  AMR 0,0,21     ;Add second to first
000015 004426  STR 0,0,22     ;Store result
000016 000000  HLT            ;Stop
                     LOC 20
000024 000017  Data 15        ;First number
000025 000031  Data 25        ;Second number
000026 000000  Data 0         ;Result location
```

### Example 2: Loop
```assembly
; Count from 10 down to 0
 LOC 10

 LDR 0,0,20     ;Load counter

Loop: SIR 0,1        ;Subtract 1
 STR 0,0,20     ;Store counter
 JNE 0,0,Loop   ;Loop if not zero
 HLT            ;Stop

 LOC 20
 Data 10        ;Initial count
```

### Example 3: Forward Reference
```assembly
; Jump to label defined later
 LOC 10

 JMA 0,End      ;Jump to End (forward reference)
 HLT            ;Never executed

 LOC 100
End: LDR 0,0,200  ;This runs first!
 HLT
```

**Note:** The assembler handles forward references automatically!

---

## Advanced Usage

### Using with Make/Build Scripts

Create a `Makefile`:
```makefile
assemble:
	java -jar C6461_Assembler.jar test/test_program.asm

clean:
	rm -f test/*.lst test/*.load
```

### Batch Processing

Assemble multiple files:
```bash
for file in *.asm; do
    java -jar C6461_Assembler.jar "$file"
done
```

---

## Tips 

1. **Start Simple** - Test with small programs first
2. **Use Comments** - Document what your code does
3. **Check Output** - Always review the .lst file
4. **Test Incrementally** - Assemble after each change
5. **Use Labels** - Makes code more readable
6. **Organize Code** - Use LOC to separate sections

---

## Support

**For bugs or questions:**
- Check this user guide
- Review design notes in `docs/DesignNotes.md`
- Check GitHub issues
- Contact team  7 members

**For C6461 ISA questions:**
- See course documentation
- Review instruction reference in README.md

---



---

**Document Version:** 1.0  
**Last Updated:** 02/07/2026 
**Team:**  Team 7 Chandle Mubashir,
Tshiswaka Mukendi,
Miguel Martinez
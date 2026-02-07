package edu.gwu.cs6461.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the complete C6461 instruction set
 */
public class InstructionSet {

    private static final Map<String, Instruction> instructions = new HashMap<>();

    static {
        // Miscellaneous
        add("HLT", 0x00, Instruction.Format.MISC);
        add("TRAP", 0x30, Instruction.Format.MISC);

        // Load/Store
        add("LDR", 0x01, Instruction.Format.LOAD_STORE);
        add("STR", 0x02, Instruction.Format.LOAD_STORE);
        add("LDA", 0x03, Instruction.Format.LOAD_STORE);
        add("LDX", 0x41, Instruction.Format.LOAD_STORE);
        add("STX", 0x42, Instruction.Format.LOAD_STORE);

        // Arithmetic/Logical (Memory)
        add("AMR", 0x04, Instruction.Format.LOAD_STORE);
        add("SMR", 0x05, Instruction.Format.LOAD_STORE);
        add("AIR", 0x06, Instruction.Format.IMMEDIATE);
        add("SIR", 0x07, Instruction.Format.IMMEDIATE);

        // Transfer/Branch
        add("JZ", 0x10, Instruction.Format.LOAD_STORE);
        add("JNE", 0x11, Instruction.Format.LOAD_STORE);
        add("JCC", 0x12, Instruction.Format.LOAD_STORE);
        add("JMA", 0x13, Instruction.Format.LOAD_STORE);
        add("JSR", 0x14, Instruction.Format.LOAD_STORE);
        add("RFS", 0x15, Instruction.Format.IMMEDIATE);
        add("SOB", 0x16, Instruction.Format.LOAD_STORE);
        add("JGE", 0x17, Instruction.Format.LOAD_STORE);

        // Register Operations
        add("MLT", 0x70, Instruction.Format.REGISTER);
        add("DVD", 0x71, Instruction.Format.REGISTER);
        add("TRR", 0x72, Instruction.Format.REGISTER);
        add("AND", 0x73, Instruction.Format.REGISTER);
        add("ORR", 0x74, Instruction.Format.REGISTER);
        add("NOT", 0x75, Instruction.Format.REGISTER);

        // Shift/Rotate
        add("SRC", 0x31, Instruction.Format.SHIFT_ROTATE);
        add("RRC", 0x32, Instruction.Format.SHIFT_ROTATE);

        // I/O
        add("IN", 0x61, Instruction.Format.IO);
        add("OUT", 0x62, Instruction.Format.IO);
        add("CHK", 0x63, Instruction.Format.IO);

        // Floating Point
        add("FADD", 0x33, Instruction.Format.LOAD_STORE);
        add("FSUB", 0x34, Instruction.Format.LOAD_STORE);
        add("VADD", 0x35, Instruction.Format.LOAD_STORE);
        add("VSUB", 0x36, Instruction.Format.LOAD_STORE);
        add("CNVRT", 0x37, Instruction.Format.LOAD_STORE);
        add("LDFR", 0x50, Instruction.Format.LOAD_STORE);
        add("STFR", 0x51, Instruction.Format.LOAD_STORE);
    }

    private static void add(String mnemonic, int opcode, Instruction.Format format) {
        instructions.put(mnemonic, new Instruction(mnemonic, opcode, format));
    }

    public static Instruction getInstruction(String mnemonic) {
        return instructions.get(mnemonic.toUpperCase());
    }

    public static boolean isValidInstruction(String mnemonic) {
        return instructions.containsKey(mnemonic.toUpperCase());
    }
}
package edu.gwu.cs6461.assembler;

/**
 * Represents a C6461 instruction with encoding capability
 */
public class Instruction {

    public enum Format {
        LOAD_STORE,    // OpCode(6), R(2), IX(2), I(1), Address(5)
        REGISTER,      // OpCode(6), Rx(2), Ry(2), unused(6)
        IMMEDIATE,     // OpCode(6), R(2), unused(3), Immed(5)
        SHIFT_ROTATE,  // OpCode(6), R(2), AL(1), LR(1), unused(2), Count(4)
        IO,            // OpCode(6), R(2), DevID(5)
        MISC           // OpCode(6), code(10)
    }

    private String mnemonic;
    private int opcode;
    private Format format;

    public Instruction(String mnemonic, int opcode, Format format) {
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.format = format;
    }

    public String getMnemonic() { return mnemonic; }
    public int getOpcode() { return opcode; }
    public Format getFormat() { return format; }

    /**
     * Encode Load/Store format instruction
     */
    public int encodeLoadStore(int r, int ix, int address, boolean indirect) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (r & 0x03) << 8;
        instruction |= (ix & 0x03) << 6;
        instruction |= (indirect ? 1 : 0) << 5;
        instruction |= (address & 0x1F);
        return instruction & 0xFFFF;
    }

    /**
     * Encode Register format instruction
     */
    public int encodeRegister(int rx, int ry) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (rx & 0x03) << 8;
        instruction |= (ry & 0x03) << 6;
        return instruction & 0xFFFF;
    }

    /**
     * Encode Immediate format instruction
     */
    public int encodeImmediate(int r, int immed) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (r & 0x03) << 8;
        instruction |= (immed & 0x1F);
        return instruction & 0xFFFF;
    }

    /**
     * Encode Shift/Rotate format instruction
     */
    public int encodeShiftRotate(int r, int count, int lr, int al) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (r & 0x03) << 8;
        instruction |= (al & 0x01) << 7;
        instruction |= (lr & 0x01) << 6;
        instruction |= (count & 0x0F);
        return instruction & 0xFFFF;
    }

    /**
     * Encode I/O format instruction
     */
    public int encodeIO(int r, int devid) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (r & 0x03) << 8;
        instruction |= (devid & 0x1F);
        return instruction & 0xFFFF;
    }

    /**
     * Encode Miscellaneous format instruction
     */
    public int encodeMisc(int code) {
        int instruction = (opcode & 0x3F) << 10;
        instruction |= (code & 0x0F);
        return instruction & 0xFFFF;
    }
}
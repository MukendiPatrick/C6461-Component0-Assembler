package edu.gwu.cs6461.assembler;

import java.io.*;
import java.util.*;

/**
 * C6461 Two-Pass Assembler
 * Component 0 - CS6461 Computer Architecture Project
 *
 * This assembler translates C6461 assembly language into machine code.
 * It implements a classic two-pass assembly algorithm:
 *   Pass 1: Build symbol table, assign addresses
 *   Pass 2: Generate machine code, resolve labels
 *
 * @author CS6461 Project Team
 * @version 1.0
 */
public class C6461Assembler {

    private Map<String, Integer> symbolTable;
    private List<SourceLine> sourceLines;
    private int locationCounter;
    private List<String> errors;

    /**
     * Constructor
     */
    public C6461Assembler() {
        this.symbolTable = new HashMap<>();
        this.sourceLines = new ArrayList<>();
        this.locationCounter = 0;
        this.errors = new ArrayList<>();
    }

    /**
     * Main assembly method
     * @param inputFile Source assembly file
     * @param listingFile Output listing file
     * @param loadFile Output load file
     * @return true if assembly successful
     */
    public boolean assemble(String inputFile, String listingFile, String loadFile) {
        try {
            readSourceFile(inputFile);

            System.out.println("Pass 1: Building symbol table...");
            if (!pass1()) {
                printErrors();
                return false;
            }

            System.out.println("Pass 2: Generating machine code...");
            if (!pass2()) {
                printErrors();
                return false;
            }

            writeListingFile(listingFile);
            writeLoadFile(loadFile);

            System.out.println("Assembly completed successfully!");
            System.out.println("  Listing file: " + listingFile);
            System.out.println("  Load file: " + loadFile);

            return true;

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Read source file into memory
     */
    private void readSourceFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            sourceLines.add(new SourceLine(lineNumber, line));
        }
        reader.close();
    }

    /**
     * Pass 1: Build symbol table and assign addresses
     */
    private boolean pass1() {
        locationCounter = 0;
        boolean success = true;

        for (SourceLine line : sourceLines) {
            line.parse();

            // Add label to symbol table
            if (line.hasLabel()) {
                String label = line.getLabel();
                if (symbolTable.containsKey(label)) {
                    errors.add("Line " + line.getLineNumber() + ": Duplicate label '" + label + "'");
                    success = false;
                } else {
                    symbolTable.put(label, locationCounter);
                }
            }

            String opcode = line.getOpcode();
            if (opcode == null) continue;

            // Process directives and instructions
            if (opcode.equals("LOC")) {
                try {
                    locationCounter = Integer.parseInt(line.getOperand(0));
                } catch (Exception e) {
                    errors.add("Line " + line.getLineNumber() + ": Invalid LOC value");
                    success = false;
                }
            } else if (opcode.equals("DATA")) {
                line.setAddress(locationCounter);
                line.setHasCode(true);
                locationCounter++;
            } else if (InstructionSet.isValidInstruction(opcode)) {
                line.setAddress(locationCounter);
                line.setHasCode(true);
                locationCounter++;
            } else {
                errors.add("Line " + line.getLineNumber() + ": Unknown instruction '" + opcode + "'");
                success = false;
            }
        }

        return success;
    }

    /**
     * Pass 2: Generate machine code
     */
    private boolean pass2() {
        locationCounter = 0;
        boolean success = true;

        for (SourceLine line : sourceLines) {
            String opcode = line.getOpcode();
            if (opcode == null) continue;

            if (opcode.equals("LOC")) {
                locationCounter = Integer.parseInt(line.getOperand(0));
            } else if (opcode.equals("DATA")) {
                if (!encodeData(line)) success = false;
                locationCounter++;
            } else if (InstructionSet.isValidInstruction(opcode)) {
                if (!encodeInstruction(line)) success = false;
                locationCounter++;
            }
        }

        return success;
    }

    /**
     * Encode DATA directive
     */
    private boolean encodeData(SourceLine line) {
        String value = line.getOperand(0);

        // Check if label reference
        if (symbolTable.containsKey(value)) {
            line.setEncodedValue(symbolTable.get(value));
            return true;
        }

        // Parse as integer
        try {
            int intValue = Integer.parseInt(value);
            line.setEncodedValue(intValue & 0xFFFF);
            return true;
        } catch (NumberFormatException e) {
            errors.add("Line " + line.getLineNumber() + ": Invalid DATA value '" + value + "'");
            return false;
        }
    }

    /**
     * Encode instruction based on format
     */
    private boolean encodeInstruction(SourceLine line) {
        String mnemonic = line.getOpcode();
        Instruction instr = InstructionSet.getInstruction(mnemonic);

        try {
            int encoded = 0;

            switch (instr.getFormat()) {
                case LOAD_STORE:
                    encoded = encodeLoadStore(line, instr);
                    break;
                case REGISTER:
                    encoded = encodeRegister(line, instr);
                    break;
                case IMMEDIATE:
                    encoded = encodeImmediate(line, instr);
                    break;
                case SHIFT_ROTATE:
                    encoded = encodeShiftRotate(line, instr);
                    break;
                case IO:
                    encoded = encodeIO(line, instr);
                    break;
                case MISC:
                    encoded = encodeMisc(line, instr);
                    break;
            }

            line.setEncodedValue(encoded);
            return true;

        } catch (Exception e) {
            errors.add("Line " + line.getLineNumber() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Encode Load/Store format: opcode r, ix, address[,I]
     */
    private int encodeLoadStore(SourceLine line, Instruction instr) {
        int r = parseOperand(line, 0);
        int ix = parseOperand(line, 1);
        int address = parseAddress(line, 2);
        boolean indirect = line.getOperandCount() > 3 && line.getOperand(3).equals("I");

        return instr.encodeLoadStore(r, ix, address, indirect);
    }

    /**
     * Encode Register format: opcode rx, ry
     */
    private int encodeRegister(SourceLine line, Instruction instr) {
        int rx = parseOperand(line, 0);
        int ry = line.getOperandCount() > 1 ? parseOperand(line, 1) : 0;
        return instr.encodeRegister(rx, ry);
    }

    /**
     * Encode Immediate format: opcode r, immed
     */
    private int encodeImmediate(SourceLine line, Instruction instr) {
        int r = line.getOperandCount() > 0 ? parseOperand(line, 0) : 0;
        int immed = line.getOperandCount() > 1 ? parseOperand(line, 1) : 0;
        return instr.encodeImmediate(r, immed);
    }

    /**
     * Encode Shift/Rotate format: opcode r, count, lr, al
     */
    private int encodeShiftRotate(SourceLine line, Instruction instr) {
        int r = parseOperand(line, 0);
        int count = parseOperand(line, 1);
        int lr = parseOperand(line, 2);
        int al = parseOperand(line, 3);
        return instr.encodeShiftRotate(r, count, lr, al);
    }

    /**
     * Encode I/O format: opcode r, devid
     */
    private int encodeIO(SourceLine line, Instruction instr) {
        int r = parseOperand(line, 0);
        int devid = parseOperand(line, 1);
        return instr.encodeIO(r, devid);
    }

    /**
     * Encode Miscellaneous format
     */
    private int encodeMisc(SourceLine line, Instruction instr) {
        int code = line.getOperandCount() > 0 ? parseOperand(line, 0) : 0;
        return instr.encodeMisc(code);
    }

    /**
     * Parse operand as integer
     */
    private int parseOperand(SourceLine line, int index) {
        if (index >= line.getOperandCount()) {
            throw new RuntimeException("Missing operand " + (index + 1));
        }
        return Integer.parseInt(line.getOperand(index));
    }

    /**
     * Parse address (could be integer or label)
     */
    private int parseAddress(SourceLine line, int index) {
        String operand = line.getOperand(index);

        // Check if it's a label first
        if (symbolTable.containsKey(operand)) {
            return symbolTable.get(operand);
        }

        // Try to parse as integer
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            // If it's not a number and not in symbol table, it's an undefined label
            throw new RuntimeException("Undefined label: " + operand);
        }
    }

    /**
     * Write listing file
     */
    private void writeListingFile(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        for (SourceLine line : sourceLines) {
            if (line.hasCode()) {
                writer.printf("%06o %06o %s%n",
                        line.getAddress(),
                        line.getEncodedValue(),
                        line.getOriginalLine());
            } else {
                writer.printf("%19s%s%n", "", line.getOriginalLine());
            }
        }

        writer.close();
    }

    /**
     * Write load file (octal format)
     */
    private void writeLoadFile(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        for (SourceLine line : sourceLines) {
            if (line.hasCode()) {
                writer.printf("%06o %06o%n", line.getAddress(), line.getEncodedValue());
            }
        }

        writer.close();
    }

    /**
     * Print errors to console
     */
    private void printErrors() {
        for (String error : errors) {
            System.err.println("ERROR: " + error);
        }
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java C6461Assembler <source_file> [listing_file] [load_file]");
            System.exit(1);
        }

        String sourceFile = args[0];
        String baseName = sourceFile.replaceAll("\\.[^.]*$", "");
        String listingFile = args.length > 1 ? args[1] : baseName + ".lst";
        String loadFile = args.length > 2 ? args[2] : baseName + ".load";

        C6461Assembler assembler = new C6461Assembler();
        boolean success = assembler.assemble(sourceFile, listingFile, loadFile);

        System.exit(success ? 0 : 1);
    }
}

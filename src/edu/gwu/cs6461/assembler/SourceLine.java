package edu.gwu.cs6461.assembler;

/**
 * Represents a single line of source code after parsing
 */
public class SourceLine {
    private int lineNumber;
    private String originalLine;
    private String label;
    private String opcode;
    private String[] operands;
    private int address;
    private int encodedValue;
    private boolean hasCode;

    public SourceLine(int lineNumber, String line) {
        this.lineNumber = lineNumber;
        this.originalLine = line;
        this.label = null;
        this.opcode = null;
        this.operands = new String[0];
        this.address = 0;
        this.encodedValue = 0;
        this.hasCode = false;
    }

    /**
     * Parse the line into components
     */
    public void parse() {
        String line = originalLine;

        // Remove comment
        int commentIdx = line.indexOf(';');
        if (commentIdx >= 0) {
            line = line.substring(0, commentIdx);
        }

        line = line.trim();
        if (line.isEmpty()) return;

        // Extract label
        if (line.contains(":")) {
            int colonIdx = line.indexOf(':');
            label = line.substring(0, colonIdx).trim();
            line = line.substring(colonIdx + 1).trim();
        }

        if (line.isEmpty()) return;

        // Parse opcode and operands
        String[] parts = line.split("\\s+", 2);
        opcode = parts[0].toUpperCase();

        if (parts.length > 1) {
            operands = parts[1].split(",");
            for (int i = 0; i < operands.length; i++) {
                operands[i] = operands[i].trim();
            }
        }
    }

    // Getters and setters
    public int getLineNumber() { return lineNumber; }
    public String getOriginalLine() { return originalLine; }
    public boolean hasLabel() { return label != null; }
    public String getLabel() { return label; }
    public String getOpcode() { return opcode; }
    public int getOperandCount() { return operands.length; }
    public String getOperand(int index) { return operands[index]; }
    public int getAddress() { return address; }
    public void setAddress(int address) { this.address = address; }
    public int getEncodedValue() { return encodedValue; }
    public void setEncodedValue(int value) { this.encodedValue = value; }
    public boolean hasCode() { return hasCode; }
    public void setHasCode(boolean hasCode) { this.hasCode = hasCode; }
}
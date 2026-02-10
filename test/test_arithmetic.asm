; Test arithmetic and logical operations
 LOC 10

; Initialize values
 LDR 0,0,30      ;Load 15 into R0
 LDR 1,0,31      ;Load 10 into R1

; Test immediate arithmetic
 AIR 0,5         ;R0 = 15 + 5 = 20
 SIR 1,3         ;R1 = 10 - 3 = 7

; Test memory arithmetic
 AMR 0,0,31      ;R0 = 20 + 10 = 30
 SMR 1,0,30      ;R1 = 7 - 15 = -8

; Test multiplication
 LDR 2,0,32      ;Load 4 into R2
 LDR 3,0,33      ;Load 3 into R3
 MLT 2,3         ;R2:R3 = 4 * 3 = 12

; Test division
 LDR 0,0,34      ;Load 17 into R0
 LDR 1,0,35      ;Load 5 into R1
 DVD 0,1         ;R0 = 17/5 = 3, R1 = 17%5 = 2

; Test logical operations
 LDR 0,0,36      ;Load 0xF0F0
 LDR 1,0,37      ;Load 0x00FF
 AND 0,1         ;R0 = 0xF0F0 & 0x00FF = 0x00F0
 ORR 0,1         ;R0 = 0x00F0 | 0x00FF = 0x00FF
 NOT 0           ;R0 = ~0x00FF

 HLT

; Data section
 LOC 30
 Data 15         ;Value 1
 Data 10         ;Value 2
 Data 4          ;Value 3
 Data 3          ;Value 4
 Data 17         ;Dividend
 Data 5          ;Divisor
 Data 61680      ;0xF0F0 in decimal
 Data 255        ;0x00FF in decimal
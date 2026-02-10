; Test shift and rotate operations
 LOC 10

; Test logical shift left
 LDR 0,0,30      ;Load 0000000000000011 (3)
 SRC 0,2,1,1     ;Shift left 2, logical -> 0000000000001100 (12)
 STR 0,0,31      ;Store result

; Test logical shift right
 LDR 0,0,32      ;Load 0000000000001100 (12)
 SRC 0,2,0,1     ;Shift right 2, logical -> 0000000000000011 (3)
 STR 0,0,33      ;Store result

; Test arithmetic shift right
 LDR 0,0,34      ;Load 1000000000000000 (negative)
 SRC 0,2,0,0     ;Shift right 2, arithmetic -> 1110000000000000
 STR 0,0,35      ;Store result

; Test rotate left
 LDR 0,0,36      ;Load 1100000000000011
 RRC 0,4,1,1     ;Rotate left 4 -> 0000000000111100
 STR 0,0,37      ;Store result

; Test rotate right
 LDR 0,0,36      ;Load 1100000000000011
 RRC 0,4,0,1     ;Rotate right 4 -> 0011110000000000
 STR 0,0,38      ;Store result

 HLT

; Data section
 LOC 30
 Data 3          ;0000000000000011
 Data 0          ;Result 1
 Data 12         ;0000000000001100
 Data 0          ;Result 2
 Data 32768      ;1000000000000000 (negative)
 Data 0          ;Result 3
 Data 49155      ;1100000000000011
 Data 0          ;Result 4
 Data 0          ;Result 5
; Test branching and loop operations
 LOC 10

; Test JZ (Jump if Zero)
 LDR 0,0,50      ;Load 0
 JZ 0,0,Label1   ;Should jump
 HLT             ;Should not execute

Label1: LDR 0,0,51   ;Load 5
 JZ 0,0,Label2   ;Should NOT jump
 JMA 0,0,Label3    ;Unconditional jump (r, ix  field ignored but required)
 HLT             ;Should not execute

Label2: HLT            ;Should not execute

; Test loop with SOB
Label3: LDR 2,0,52      ;Load counter = 5
Loop: SIR 2,1         ;Decrement counter
 SOB 2,0,Loop    ;Loop while R2 > 0

; Test JNE
 LDR 0,0,50      ;Load 0
 JNE 0,0,Skip    ;Should NOT jump
 JMA 0,0,Continue  ;Unconditional jump (r, ix  field ignored but required)
 HLT             ;Should not execute

Skip: HLT             ;Should not execute

; Test JGE
Continue: LDR 1,0,53      ;Load -5 (will be 65531 in 16-bit)
 JGE 1,0,Bad     ;Should NOT jump (negative)
 LDR 1,0,51      ;Load 5
 JGE 1,0,End     ;Should jump (positive)
 HLT             ;Should not execute

Bad: HLT              ;Should not execute

End: HLT              ;Final halt

; Data section
 LOC 50
 Data 0          ;Zero value
 Data 5          ;Positive value
 Data 5          ;Counter
 Data 65531      ;Negative value -5 in 16-bit unsigned
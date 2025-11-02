# tasm

An assembler for TAM assembly programs.

The executable expects two arguments: the name of the source file containing
assembly code, and the name to write the assembled bytecode to.

```
$ java -jar tasm infile outfile
...assembly output
```

## Assembly format

The basic format of an instruction is the same as that shown in Watt and Brown (Table C.2):

```
LOAD(1) -2[LB]
STORE(1) 3[LB]
```

We make a couple of modifications and additions:

- Comments can be written using the semicolon. A comment begins with a semicolon and runs to the end
  of the line. All text inside a comment including the semicolon is ignored by the parser.
- Numeric values can be given as hexadecimal values. These are denoted with an `h` at the end of
  the number:
  ```
  LOAD(Ah) 1[SB]
  LOADL CAFEh
  ```
- The argument to `LOADL` can be given as a character literal. The character must be a printable
  ASCII character (i.e. character code 32 to 126):
 
  ```
  LOADL 'H'
  LOADL 'i'
  LOADL '!'
  ```
- Instructions can be **labelled**. A label is a sequence of lowercase letters, numbers, or
  underscores beginning with a letter. A labelled instruction consists of a label, followed by a
  comma, followed by the instruction:

  ```
  loop: LOAD(1) -1[LB]
  ```
- `CALL` and `JUMP(I/IF)` instructions can take a label as an argument in place of an absolute
  address. This label must be defined somewhere in the program, but can appear before or after the
  instruction.

  ```
  loop: LOADL 42
        JUMP loop
  ```
- The `n` argument to the `CALL` instruction used as the static link can be specified using a
  register name rather than its index.

  ```
  CALL(SB) myfunction
  ```
- Primitive operations can be called using their name instead of an address. When calling a
  primitive operation, the static link register can be omitted.

  ```
  LOADL 1
  LOADL 2
  CALL add
  CALL(SB) myfunction
  ```

## Example program

To illustrate, here is the ever popular Euclid's algorithm in TAM assembly:

```
  PUSH 2
  LOADA 0[SB]
  CALL getint
  LOADA 1[SB]
  CALL getint
loop:
  LOAD(1) 1[SB]
  LOADL 0
  CALL gt
  JUMPIF(0) end  ; if b <= 0 goto end
  LOAD(1) 1[SB]
  LOAD(1) 0[SB]
  LOAD(1) 1[SB]
  CALL mod
  STORE(1) 1[SB] ; b = a%b
  STORE(1) 0[SB] ; a = b
  JUMP loop
end:
  LOAD(1) 0[SB]
  CALL putint
  CALL puteol
  HALT
```


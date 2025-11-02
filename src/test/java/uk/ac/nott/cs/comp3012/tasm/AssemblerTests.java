package uk.ac.nott.cs.comp3012.tasm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AssemblerTests {

    private Assembler assembler;

    @BeforeEach
    public void setup() {
        assembler = new Assembler();
    }

    @Test
    public void testAssemble() {
        String input = """
                JUMPIF(104) -77[LB]
                CALL(CT) 23[CB]
                HALT""";
        CharStream chars = CharStreams.fromString(input);

        byte[] program = assembler.assemble(chars);
        assertArrayEquals(
                new byte[]{(byte) 0xe8, 0x68, (byte) 0xff, (byte) 0xb3, 0x60, 0x01, 0x00, 0x17,
                        (byte) 0xf0, 0x00, 0x00, 0x00}, program);
    }

    @Test
    public void testAssembleLabels() {
        String input = """
                start: LOADL 1
                       JUMP end
                end:   JUMPIF(1) start""";
        CharStream chars = CharStreams.fromString(input);

        byte[] program = assembler.assemble(chars);
        assertArrayEquals(
                new byte[]{0x30, 0x00, 0x00, 0x01, (byte) 0xc0, 0x00, 0x00, 0x02, (byte) 0xe0, 0x01,
                        0x00, 0x00}, program);
    }

    @Test
    public void testAssembleCharLiteral() {
        String input = "LOADL 'A'";
        CharStream chars = CharStreams.fromString(input);

        byte[] program = assembler.assemble(chars);
        assertArrayEquals(new byte[]{0x30, 0x00, 0x00, 0x41}, program);
    }

    @Test
    public void testAssembleHexNumber() {
        String input = "LOADL A0B3h";
        CharStream chars = CharStreams.fromString(input);

        byte[] program = assembler.assemble(chars);
        assertArrayEquals(new byte[]{0x30, 0x00, (byte) 0xa0, (byte) 0xb3}, program);
    }
}

package uk.ac.nott.cs.comp3012.tasm;

import java.io.FileOutputStream;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import uk.ac.nott.cs.comp3012.tasm.TasmInstruction.InstructionList;

public class Assembler {

    public static void main(String[] args) throws IOException {
        Assembler assembler = new Assembler();

        CharStream chars = CharStreams.fromFileName(args[0]);
        byte[] code = assembler.assemble(chars);

        try (FileOutputStream out = new FileOutputStream(args[1])) {
            out.write(code);
        }
    }

    public byte[] assemble(CharStream input) {
        // construct lexer and parser
        TasmLexer lexer = new TasmLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        TasmParser parser = new TasmParser(tokens);

        // read program
        InstructionList program = (InstructionList) new ProgramBuilder().visitProgram(
                parser.program());
        return program.toByteArray();
    }
}

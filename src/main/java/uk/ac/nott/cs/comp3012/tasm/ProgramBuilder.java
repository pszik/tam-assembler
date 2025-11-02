package uk.ac.nott.cs.comp3012.tasm;

import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import uk.ac.nott.cs.comp3012.tasm.TasmInstruction.Instruction;
import uk.ac.nott.cs.comp3012.tasm.TasmInstruction.InstructionList;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.AnonProgElementContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.CallInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.CallLabelInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.CallPrimitiveInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.CalliInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.HaltInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.JumpInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.JumpLabelInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.JumpiInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.JumpifInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.JumpifLabelInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LabelledProgElementContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LoadInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LoadaInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LoadiInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LoadlCharContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.LoadlInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.PopInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.ProgElementContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.ProgramContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.PushInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.ReturnInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.StoreInstrContext;
import uk.ac.nott.cs.comp3012.tasm.TasmParser.StoreiInstrContext;

public class ProgramBuilder extends TasmBaseVisitor<TasmInstruction> {

    private final Map<String, Integer> labelOffsets = new HashMap<>();

    @Override
    public TasmInstruction visitProgram(ProgramContext ctx) {
        // locate all labels
        int offset = 0;
        for (ProgElementContext ctx1 : ctx.progElement()) {
            if (ctx1 instanceof LabelledProgElementContext lblCtx) {
                labelOffsets.put(lblCtx.LABEL().getText(), offset);
            }
            offset++;
        }

        // parse instructions
        InstructionList instrs = new InstructionList();
        for (ProgElementContext ctx1 : ctx.progElement()) {
            Instruction instr = (Instruction) visit(ctx1);
            instrs.add(instr);
        }

        return instrs;
    }

    @Override
    public TasmInstruction visitAnonProgElement(AnonProgElementContext ctx) {
        return visit(ctx.instruction());
    }

    @Override
    public TasmInstruction visitLabelledProgElement(LabelledProgElementContext ctx) {
        return visit(ctx.instruction());
    }

    @Override
    public TasmInstruction visitLoadInstr(LoadInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int n = parseNumber(ctx.n.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.LOAD, r, n, d);
    }

    @Override
    public TasmInstruction visitLoadaInstr(LoadaInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.LOADA, r, 0, d);
    }

    @Override
    public TasmInstruction visitLoadiInstr(LoadiInstrContext ctx) {
        int n = parseNumber(ctx.n.getText());
        return new Instruction(TasmOpcode.LOADI, TasmRegister.CB, n, 0);
    }

    @Override
    public TasmInstruction visitLoadlInstr(LoadlInstrContext ctx) {
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.LOADL, TasmRegister.CB, 0, d);
    }

    @Override
    public TasmInstruction visitLoadlChar(LoadlCharContext ctx) {
        int d = ctx.c.getText().charAt(1);
        return new Instruction(TasmOpcode.LOADL, TasmRegister.CB, 0, d);
    }

    @Override
    public TasmInstruction visitStoreInstr(StoreInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int n = parseNumber(ctx.n.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.STORE, r, n, d);
    }

    @Override
    public TasmInstruction visitStoreiInstr(StoreiInstrContext ctx) {
        int n = parseNumber(ctx.n.getText());
        return new Instruction(TasmOpcode.STOREI, TasmRegister.CB, n, 0);
    }

    @Override
    public TasmInstruction visitCallInstr(CallInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int n = TasmRegister.valueOf(ctx.n.getText()).value;
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.CALL, r, n, d);
    }

    @Override
    public TasmInstruction visitCallPrimitiveInstr(CallPrimitiveInstrContext ctx) {
        int d = getPrimitiveOffset(ctx.PRIMITIVE().getText());
        return new Instruction(TasmOpcode.CALL, TasmRegister.PB, 0, d);
    }

    @Override
    public TasmInstruction visitCallLabelInstr(CallLabelInstrContext ctx) {
        int offset = labelOffsets.getOrDefault(ctx.LABEL().getText(), -1);
        if (offset < 0) {
            throw new ParseCancellationException(
                    String.format("use of undeclared label '%s' on line %d", ctx.LABEL().getText(),
                            ctx.getStart().getLine()));
        }

        return new Instruction(TasmOpcode.CALL, TasmRegister.CB, 0, offset);
    }

    @Override
    public TasmInstruction visitCalliInstr(CalliInstrContext ctx) {
        return new Instruction(TasmOpcode.CALLI, TasmRegister.CB, 0, 0);
    }

    @Override
    public TasmInstruction visitReturnInstr(ReturnInstrContext ctx) {
        int n = parseNumber(ctx.n.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.RETURN, TasmRegister.CB, n, d);
    }

    @Override
    public TasmInstruction visitPushInstr(PushInstrContext ctx) {
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.PUSH, TasmRegister.CB, 0, d);
    }

    @Override
    public TasmInstruction visitPopInstr(PopInstrContext ctx) {
        int n = parseNumber(ctx.n.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.POP, TasmRegister.CB, n, d);
    }

    @Override
    public TasmInstruction visitJumpInstr(JumpInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.JUMP, r, 0, d);
    }

    @Override
    public TasmInstruction visitJumpLabelInstr(JumpLabelInstrContext ctx) {
        int offset = labelOffsets.getOrDefault(ctx.LABEL().getText(), -1);
        if (offset < 0) {
            throw new ParseCancellationException(
                    String.format("use of undeclared label '%s' on line %d", ctx.LABEL().getText(),
                            ctx.getStart().getLine()));
        }

        return new Instruction(TasmOpcode.JUMP, TasmRegister.CB, 0, offset);
    }

    @Override
    public TasmInstruction visitJumpiInstr(JumpiInstrContext ctx) {
        return new Instruction(TasmOpcode.JUMPI, TasmRegister.CB, 0, 0);
    }

    @Override
    public TasmInstruction visitJumpifInstr(JumpifInstrContext ctx) {
        TasmRegister r = TasmRegister.valueOf(ctx.r.getText());
        int n = parseNumber(ctx.n.getText());
        int d = parseNumber(ctx.d.getText());
        return new Instruction(TasmOpcode.JUMPIF, r, n, d);
    }

    @Override
    public TasmInstruction visitJumpifLabelInstr(JumpifLabelInstrContext ctx) {
        int offset = labelOffsets.getOrDefault(ctx.LABEL().getText(), -1);
        if (offset < 0) {
            throw new ParseCancellationException(
                    String.format("use of undeclared label '%s' on line %d", ctx.LABEL().getText(),
                            ctx.getStart().getLine()));
        }

        int n = parseNumber(ctx.n.getText());
        return new Instruction(TasmOpcode.JUMPIF, TasmRegister.CB, n, offset);
    }

    @Override
    public TasmInstruction visitHaltInstr(HaltInstrContext ctx) {
        return new Instruction(TasmOpcode.HALT, TasmRegister.CB, 0, 0);
    }

    private int parseNumber(String numberText) {
        if (!numberText.endsWith("h")) {
            return Integer.parseInt(numberText);
        }

        String substr = numberText.substring(0, numberText.length() - 1);
        return Integer.parseInt(substr, 16);
    }

    private int getPrimitiveOffset(String primitive) {
        return switch (primitive) {
            case "id" -> 1;
            case "not" -> 2;
            case "and" -> 3;
            case "or" -> 4;
            case "succ" -> 5;
            case "pred" -> 6;
            case "neg" -> 7;
            case "add" -> 8;
            case "sub" -> 9;
            case "mult" -> 10;
            case "div" -> 11;
            case "mod" -> 12;
            case "lt" -> 13;
            case "le" -> 14;
            case "ge" -> 15;
            case "gt" -> 16;
            case "eq" -> 17;
            case "ne" -> 18;
            case "eol" -> 19;
            case "eof" -> 20;
            case "get" -> 21;
            case "put" -> 22;
            case "geteol" -> 23;
            case "puteol" -> 24;
            case "getint" -> 25;
            case "putint" -> 26;
            case "new" -> 27;
            case "dispose" -> 28;
            default -> throw new IllegalArgumentException();
        };
    }
}

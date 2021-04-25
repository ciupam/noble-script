import exceptions.*;
import meta.Definition;
import meta.Value;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import types.DefinitionType;
import types.VarType;

import java.util.*;

import static types.ValueType.*;

public class LLVMActions implements NobleScriptListener {
    private final Map<String, Map<String, Definition>> functionDefs = new HashMap<>();
    private final Stack<String> functionStack = new Stack<>();
    private final Stack<Value> valueStack = new Stack<>();

    private final LLVMGenerator generator;

    private final int logLevel;

    private final String GLOBAL_SCOPE_STACK_ID = "";

    public LLVMActions(LLVMGenerator generator, int logLevel) {
        this.generator = generator;
        this.logLevel = logLevel;
        functionDefs.put(GLOBAL_SCOPE_STACK_ID, new HashMap<>());
    }

    @Override
    public void enterProgram(NobleScriptParser.ProgramContext ctx) {
        log("on enterProgram");
    }

    @Override
    public void exitProgram(NobleScriptParser.ProgramContext ctx) {
        log("on exitProgram");
    }

    @Override
    public void enterStatement(NobleScriptParser.StatementContext ctx) {
        log("on enterStatement");
    }

    @Override
    public void exitStatement(NobleScriptParser.StatementContext ctx) {
        log("on exitStatement");
    }

    @Override
    public void enterAssign_statement(NobleScriptParser.Assign_statementContext ctx) {
        log("on enterAssign_statement");
    }

    @Override
    public void exitAssign_statement(NobleScriptParser.Assign_statementContext ctx) {
        log("on exitAssign_statement");
        final String varId = ctx.ID().getText();
        final Value value = valueStack.pop();

        // Check if variable was defined in function scopes
        Definition varDef = getVarDefinition(varId);

        if (varDef.defType != DefinitionType.VARIABLE) {
            throw new IDFinalException(varId);
        }

        switch (value.type) {
            case VALUE_INT:
            case VALUE_INT_REGISTER:
                if (varDef.type != VarType.INT) {
                    throw new InvalidAssignmentException(varDef, value);
                }
                generator.assign_i32(varId, value.content, varDef.isGlobal);
                break;
            case VALUE_DOUBLE:
            case VALUE_DOUBLE_REGISTER:
                if (varDef.type != VarType.DOUBLE) {
                    throw new InvalidAssignmentException(varDef, value);
                }
                generator.assign_double(varId, value.content, varDef.isGlobal);
                break;
            default:
                // TODO add more types in exitAssign_statement
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void enterReturn_statement(NobleScriptParser.Return_statementContext ctx) {
        log("on enterReturn_statement");
    }

    @Override
    public void exitReturn_statement(NobleScriptParser.Return_statementContext ctx) {
        log("on exitReturn_statement");
    }

    @Override
    public void enterDefinition(NobleScriptParser.DefinitionContext ctx) {
        log("on enterDefinition");
    }

    @Override
    public void exitDefinition(NobleScriptParser.DefinitionContext ctx) {
        log("on exitDefinition");
    }

    @Override
    public void enterStructure_definition(NobleScriptParser.Structure_definitionContext ctx) {
        log("on enterStructure_definition");
    }

    @Override
    public void exitStructure_definition(NobleScriptParser.Structure_definitionContext ctx) {
        log("on exitStructure_definition");
    }

    @Override
    public void enterFunction_definition(NobleScriptParser.Function_definitionContext ctx) {
        log("on enterFunction_definition");
        final VarType newFunType = VarType.getType(ctx.type(0).getText());
        final String newFunId = ctx.ID(0).getText();
        final Definition newFunDef = new Definition(newFunId, newFunType, DefinitionType.FUNCTION, functionStack.isEmpty());

        final String scopeId = functionStack.empty() ? "" : functionStack.peek();
        final String newScopeId = (scopeId.equals("") ? "" : scopeId + ".") + newFunId;

        // Check if there's a function of the same stackId
        if (functionDefs.containsKey(newScopeId)) {
            throw new IDAlreadyDefinedException(newFunId, scopeId);
        }

        // Add function definition to its scope
        if (functionDefs.containsKey(scopeId)) {
            // Check if the ID already exists in the scope
            if (functionDefs.get(scopeId).containsKey(newFunId)) {
                throw new IDAlreadyDefinedException(newFunId, scopeId);
            }

            functionDefs.get(scopeId).put(newFunId, newFunDef);
        } else {
            throw new IllegalStateException("Unable to check if ID {" + newFunId + "} was defined in scope P" + scopeId + "}");
        }

        functionDefs.put(newScopeId, new HashMap<>());
        functionStack.push(newScopeId);

        // TODO add LLVMGenerator actions for function definition in enterFunction_definition
    }

    @Override
    public void exitFunction_definition(NobleScriptParser.Function_definitionContext ctx) {
        log("on exitFunction_definition");
        functionStack.pop();

        // TODO add LLVMGenerator actions for function definition in exitFunction_definition
    }

    @Override
    public void enterVariable_definition(NobleScriptParser.Variable_definitionContext ctx) {
        log("on enterVariable_definition");
        final VarType newVarType = VarType.getType(ctx.type().getText());
        final String newVarId = ctx.ID().getText();
        final Definition newVarDef = new Definition(newVarId, newVarType, DefinitionType.VARIABLE, functionStack.isEmpty());

        // Check function scope
        final String scopeId = functionStack.empty() ? GLOBAL_SCOPE_STACK_ID : functionStack.peek();
        if (functionDefs.containsKey(scopeId)) {
            if (functionDefs.get(scopeId).containsKey(newVarId)) {
                throw new IDAlreadyDefinedException(newVarId, scopeId);
            }
        } else {
            throw new IllegalStateException("Cannot define variable: " + newVarId + " for function: " + scopeId);
        }

        functionDefs.get(scopeId).put(newVarId, newVarDef);
    }

    @Override
    public void exitVariable_definition(NobleScriptParser.Variable_definitionContext ctx) {
        log("on exitVariable_definition");
        final Definition varDef = getVarDefinition(ctx.ID().getText());
        final Value value = valueStack.pop();

        switch (value.type) {
            case VALUE_INT:
            case VALUE_INT_REGISTER:
                if (varDef.type != VarType.INT) {
                    throw new InvalidAssignmentException(varDef, value);
                }
                generator.declare_i32(varDef.id, varDef.isGlobal);
                generator.assign_i32(varDef.id, value.content, varDef.isGlobal);
                break;
            case VALUE_DOUBLE:
            case VALUE_DOUBLE_REGISTER:
                if (varDef.type != VarType.DOUBLE) {
                    throw new InvalidAssignmentException(varDef, value);
                }
                generator.declare_double(varDef.id, varDef.isGlobal);
                generator.assign_double(varDef.id, value.content, varDef.isGlobal);
                break;
            default:
                // TODO add more types in exitVariable_definition
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void enterArray_definition(NobleScriptParser.Array_definitionContext ctx) {
        log("on enterArray_definition");
    }

    @Override
    public void exitArray_definition(NobleScriptParser.Array_definitionContext ctx) {
        log("on exitArray_definition");
    }

    @Override
    public void enterExpression(NobleScriptParser.ExpressionContext ctx) {
        log("on enterExpression");
    }

    @Override
    public void exitExpression(NobleScriptParser.ExpressionContext ctx) {
        log("on exitExpression");
    }

    @Override
    public void enterExpression0(NobleScriptParser.Expression0Context ctx) {
        log("on enterExpression0");

    }

    @Override
    public void exitExpression0(NobleScriptParser.Expression0Context ctx) {
        log("on exitExpression0");
    }

    @Override
    public void enterExpression1(NobleScriptParser.Expression1Context ctx) {
        log("on enterExpression1");
    }

    @Override
    public void exitExpression1(NobleScriptParser.Expression1Context ctx) {
        log("on exitExpression1");

        if (ctx.operator1() != null) {
            Value right = valueStack.pop();
            Value left = valueStack.pop();

            // TODO Type casting
            // Check variable types
            if (right.type.notEquals(left.type)) {
                TerminalNode token = ctx.operator1().MINUS_OP();
                if (token == null) token = ctx.operator1().PLUS_OP();
                throw new TypeMismatchException("Invalid type at line: " + token.getSymbol().getLine());
            }

            final Value result;
            switch (right.type) {
                case VALUE_INT:
                case VALUE_INT_REGISTER:
                    if (ctx.operator1().MINUS_OP() != null) {
                        generator.sub_i32(left.content, right.content);
                    } else {
                        generator.add_i32(left.content, right.content);
                    }
                    result = new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER);
                    break;
                case VALUE_DOUBLE:
                case VALUE_DOUBLE_REGISTER:
                    if (ctx.operator1().MINUS_OP() != null) {
                        generator.sub_double(left.content, right.content);
                    } else {
                        generator.add_double(left.content, right.content);
                    }
                    result = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE_REGISTER);
                    break;
                default:
                    //TODO more types
                    throw new UnsupportedOperationException();
            }
            valueStack.push(result);
        }
    }

    @Override
    public void enterExpression2(NobleScriptParser.Expression2Context ctx) {
        log("on enterExpression2");
    }

    @Override
    public void exitExpression2(NobleScriptParser.Expression2Context ctx) {
        log("on exitExpression2");

        if (ctx.operator2() != null) {
            Value right = valueStack.pop();
            Value left = valueStack.pop();

            // TODO Type casting
            // Check variable types
            if (right.type.notEquals(left.type)) {
                TerminalNode token = ctx.operator2().DIV_OP();
                if (token == null) token = ctx.operator2().MUL_OP();
                throw new TypeMismatchException("Invalid type at line: " + token.getSymbol().getLine());
            }

            final Value result;
            switch (right.type) {
                case VALUE_INT:
                case VALUE_INT_REGISTER:
                    if (ctx.operator2().DIV_OP() != null) {
                        generator.div_i32(left.content, right.content);
                    } else {
                        generator.mul_i32(left.content, right.content);
                    }
                    result = new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER);
                    break;
                case VALUE_DOUBLE:
                case VALUE_DOUBLE_REGISTER:
                    if (ctx.operator2().DIV_OP() != null) {
                        generator.div_double(left.content, right.content);
                    } else {
                        generator.mul_double(left.content, right.content);
                    }
                    result = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE_REGISTER);
                    break;
                default:
                    //TODO more types
                    throw new UnsupportedOperationException();
            }
            valueStack.push(result);
        }
    }

    @Override
    public void enterExpression3(NobleScriptParser.Expression3Context ctx) {
        log("on enterExpression3");
    }

    @Override
    public void exitExpression3(NobleScriptParser.Expression3Context ctx) {
        log("on exitExpression3");
    }

    @Override
    public void enterValue(NobleScriptParser.ValueContext ctx) {
        log("on enterValue");
    }

    @Override
    public void exitValue(NobleScriptParser.ValueContext ctx) {
        log("on exitValue");
        if (ctx.ID() != null) {
            final String valueId = ctx.ID().getText();

            // Check if variable was defined in any scope
            Definition varDef = getVarDefinition(valueId);

            // TODO more types
            final Value value;
            switch (varDef.type) {
                case INT:
                    value = new Value(getValueContent(varDef), VALUE_INT_REGISTER);
                    break;
                case DOUBLE:
                    value = new Value(getValueContent(varDef), VALUE_DOUBLE_REGISTER);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            valueStack.push(value);
        } else if (ctx.function_call_stm() != null) {
            // TODO implement functions calls
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void enterArray_index(NobleScriptParser.Array_indexContext ctx) {
        log("on enterArray_index");
    }

    @Override
    public void exitArray_index(NobleScriptParser.Array_indexContext ctx) {
        log("on exitArray_index");
    }

    @Override
    public void enterFunction_call_stm(NobleScriptParser.Function_call_stmContext ctx) {
        log("on enterFunction_call_stm");
    }

    @Override
    public void exitFunction_call_stm(NobleScriptParser.Function_call_stmContext ctx) {
        log("on exitFunction_call_stm");
    }

    @Override
    public void enterPrint_stm(NobleScriptParser.Print_stmContext ctx) {
        log("on enterPrint_stm");
    }

    @Override
    public void exitPrint_stm(NobleScriptParser.Print_stmContext ctx) {
        log("on exitPrint_stm");
        // TODO handle more types
        Value value = valueStack.pop();
        if (value.type == VALUE_INT_REGISTER || value.type == VALUE_INT) {
            generator.print_i32(value.content);
        } else if (value.type == VALUE_DOUBLE || value.type == VALUE_DOUBLE_REGISTER) {
            generator.printf_double(value.content);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void enterLiteral(NobleScriptParser.LiteralContext ctx) {
        log("on enterLiteral");
    }

    @Override
    public void exitLiteral(NobleScriptParser.LiteralContext ctx) {
        log("on exitLiteral");
    }

    @Override
    public void enterBoolean(NobleScriptParser.BooleanContext ctx) {
        log("on enterBoolean");
    }

    @Override
    public void exitBoolean(NobleScriptParser.BooleanContext ctx) {
        log("on exitBoolean");
        valueStack.push(new Value(ctx.getText(), VALUE_BOOLEAN));
    }

    @Override
    public void enterInt(NobleScriptParser.IntContext ctx) {
        log("on enterInt");
    }

    @Override
    public void exitInt(NobleScriptParser.IntContext ctx) {
        log("on exitInt");
        valueStack.push(new Value(ctx.getText(), VALUE_INT));
    }

    @Override
    public void enterDouble(NobleScriptParser.DoubleContext ctx) {
        log("on enterDouble");
    }

    @Override
    public void exitDouble(NobleScriptParser.DoubleContext ctx) {
        log("on exitDouble");
        valueStack.push(new Value(ctx.getText(), VALUE_DOUBLE));
    }

    @Override
    public void enterString(NobleScriptParser.StringContext ctx) {
        log("on enterString");
    }

    @Override
    public void exitString(NobleScriptParser.StringContext ctx) {
        log("on exitString");
        valueStack.push(new Value(ctx.getText(), VALUE_STRING));
    }

    @Override
    public void enterNull(NobleScriptParser.NullContext ctx) {
        log("on enterNull");
    }

    @Override
    public void exitNull(NobleScriptParser.NullContext ctx) {
        log("on exitNull");
        valueStack.push(new Value(ctx.getText(), VALUE_NULL));
    }

    @Override
    public void enterType(NobleScriptParser.TypeContext ctx) {
        log("on enterType");
    }

    @Override
    public void exitType(NobleScriptParser.TypeContext ctx) {
        log("on exitType");
    }

    @Override
    public void enterPrimitive_type(NobleScriptParser.Primitive_typeContext ctx) {
        log("on enterPrimitive_type");
    }

    @Override
    public void exitPrimitive_type(NobleScriptParser.Primitive_typeContext ctx) {
        log("on exitPrimitive_type");
    }

    @Override
    public void enterOperator2(NobleScriptParser.Operator2Context ctx) {
        log("on enterOperator2");
    }

    @Override
    public void exitOperator2(NobleScriptParser.Operator2Context ctx) {
        log("on exitOperator2");
    }

    @Override
    public void enterOperator1(NobleScriptParser.Operator1Context ctx) {
        log("on enterOperator1");
    }

    @Override
    public void exitOperator1(NobleScriptParser.Operator1Context ctx) {
        log("on exitOperator1");
    }

    @Override
    public void enterOperator0(NobleScriptParser.Operator0Context ctx) {
        log("on enterOperator0");
    }

    @Override
    public void exitOperator0(NobleScriptParser.Operator0Context ctx) {
        log("on exitOperator0");
    }

    @Override
    public void enterCompound_statement(NobleScriptParser.Compound_statementContext ctx) {
        log("on enterCompound_statement");
    }

    @Override
    public void exitCompound_statement(NobleScriptParser.Compound_statementContext ctx) {
        log("on exitCompound_statement");
    }

    @Override
    public void enterLoop_statement(NobleScriptParser.Loop_statementContext ctx) {
        log("on enterLoop_statement");
    }

    @Override
    public void exitLoop_statement(NobleScriptParser.Loop_statementContext ctx) {
        log("on exitLoop_statement");
    }

    @Override
    public void enterIf_statement(NobleScriptParser.If_statementContext ctx) {
        log("on enterIf_statement");
    }

    @Override
    public void exitIf_statement(NobleScriptParser.If_statementContext ctx) {
        log("on exitIf_statement");
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
    }

    private String getValueContent(Definition definition) {
        switch (definition.type) {
            case INT:
                generator.load_i32(definition.id, definition.isGlobal);
                return "%" + (generator.getRegister() - 1);
            case DOUBLE:
                generator.load_double(definition.id, definition.isGlobal);
                return "%" + (generator.getRegister() - 1);
            default:
                // TODO function call, load double etc
                throw new UnsupportedOperationException();
        }
    }

    private Definition getVarDefinition(String varId) {
        // Check if variable was defined in any scope
        final String scopeId = functionStack.empty() ? "" : functionStack.peek();
        String[] scopes = scopeId.split("\\.");
        for (int i = 1; i < scopes.length; i++) {
            scopes[i] = scopes[i - 1] + "." + scopes[i];
        }
        for (int i = scopes.length - 1; i >= 0; i--) {
            if (functionDefs.get(scopes[i]).containsKey(varId)) {
                return functionDefs.get(scopes[i]).get(varId);
            }
        }

        // Check global scope if varDef still null
        if (functionDefs.get(GLOBAL_SCOPE_STACK_ID).containsKey(varId)) {
            return functionDefs.get(GLOBAL_SCOPE_STACK_ID).get(varId);
        } else {
            throw new IDNotDefinedException(varId);
        }
    }

    private void log(String msg) {
        if (logLevel > 0) System.out.println(msg);
    }
}

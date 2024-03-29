import exceptions.*;
import meta.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import types.BlockType;
import types.DefinitionType;
import types.ValueType;
import types.VarType;

import java.util.*;

import static types.ValueType.*;

public class LLVMActions implements NobleScriptListener {
    private final Map<String, Map<String, Definition>> functionDefs = new HashMap<>();
    private final Stack<String> functionStack = new Stack<>();
    private final Stack<Value> valueStack = new Stack<>();
    private final Stack<BlockType> blockStack = new Stack<>();

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
        Value value = valueStack.pop();

        // Check if variable was defined in function scopes
        Definition varDef = getVarDefinition(varId, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());

        // Type casting
        if (varDef.type == VarType.DOUBLE && (value.type == VALUE_INT || value.type == VALUE_INT_REGISTER)) {
            generator.i32_to_double(value.content);
            value = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
        }

        if (varDef.defType != DefinitionType.VARIABLE && varDef.defType != DefinitionType.ARRAY) {
            throw new IDFinalException(varId, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
        }

        switch (value.type) {
            case VALUE_INT:
            case VALUE_INT_REGISTER:
                if (varDef.type != VarType.INT) {
                    throw new InvalidAssignmentException(varDef, value, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
                }
                // ARRAY ASSIGN STATEMENT
                if (ctx.INT_LITERAL() != null) {
                    int index = Integer.parseInt(ctx.INT_LITERAL().getText());
                    int size = ((ArrayDefinition) varDef).size;
                    generator.assign_i32_array(varDef.getLlvmId(), size, index, isBlank(varDef.scope), value.content);
                } else {
                    generator.assign_i32(varDef.getLlvmId(), value.content, isBlank(varDef.scope));
                }
                break;
            case VALUE_DOUBLE:
            case VALUE_DOUBLE_REGISTER:
                if (varDef.type != VarType.DOUBLE) {
                    throw new InvalidAssignmentException(varDef, value, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
                }

                // ARRAY ASSIGN STATEMENT
                if (ctx.INT_LITERAL() != null) {
                    int index = Integer.parseInt(ctx.INT_LITERAL().getText());
                    int size = ((ArrayDefinition) varDef).size;
                    generator.assign_double_array(varDef.getLlvmId(), size, index, isBlank(varDef.scope), value.content);
                } else {
                    generator.assign_double(varDef.getLlvmId(), value.content, isBlank(varDef.scope));
                }
                break;
            default:
                // TODO add more types in exitAssign_statement
                throw new UnsupportedOperationException("Assign operation for type: " + value.type);
        }
    }

    @Override
    public void enterReturn_statement(NobleScriptParser.Return_statementContext ctx) {
        log("on enterReturn_statement");
    }

    @Override
    public void exitReturn_statement(NobleScriptParser.Return_statementContext ctx) {
        log("on exitReturn_statement");
        if (functionStack.isEmpty()) {
            throw new NobleScriptException("Return statement outside of function definition",
                    ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }

        Value value = valueStack.pop();

        if (value.type.notEquals(VALUE_INT)) {
            throw new NobleScriptException("Return statement for values other than int are not supported", ctx.getStart().getLine(), ctx.RETURN().getSymbol().getCharPositionInLine());
        }
        generator.function_return_stm(value);
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
        if (!functionStack.isEmpty()) {
            throw new IllegalFunctionDefinitionException("Function definition inside other function is illegal",
                    ctx.getStart().getLine(), ctx.PAR_OPEN().getSymbol().getCharPositionInLine());
        }
        blockStack.push(BlockType.FUNCTION_BLOCK);

        final VarType newFunType = VarType.getType(ctx.type().getText());
        final String newFunId = ctx.ID().getText();
        final Definition newFunDef = new Definition(newFunId, newFunType, DefinitionType.FUNCTION, functionStack.isEmpty() ? null : functionStack.peek());

        final String scopeId = functionStack.empty() ? "" : functionStack.peek();
        final String newScopeId = (scopeId.equals("") ? "" : scopeId + ".") + newFunId;

        // Check if there's a function of the same stackId
        if (functionDefs.containsKey(newScopeId)) {
            throw new IDAlreadyDefinedException(newFunId, scopeId, ctx.getStart().getLine(), ctx.PAR_OPEN().getSymbol().getCharPositionInLine());
        }

        verifyIdIsAvailableInCurrentScope(newFunId, ctx.getStart().getLine(), ctx.PAR_OPEN().getSymbol().getCharPositionInLine());

        functionDefs.get(scopeId).put(newFunId, newFunDef);
        functionDefs.put(newScopeId, new HashMap<>());
        functionStack.push(newScopeId);

        int index = ctx.function_param().toArray().length + 1;
        List<Definition> params = new ArrayList<>();
        for (NobleScriptParser.Function_paramContext param : ctx.function_param()) {
            params.add(paramContextToDef(param, index));
            index++;
        }
        params.forEach(param -> functionDefs.get(newScopeId).put(param.id, param));
        generator.function_start(newFunDef.getLlvmId(), params);
    }

    private Definition paramContextToDef(NobleScriptParser.Function_paramContext context, int index) {
        return new ParamDefinition(context.ID().getText(),
                VarType.getType(context.type().getText()),
                DefinitionType.VARIABLE, functionStack.isEmpty() ? "" : functionStack.peek(), index);
    }

    @Override
    public void exitFunction_definition(NobleScriptParser.Function_definitionContext ctx) {
        log("on exitFunction_definition");

        generator.function_end();
        functionStack.pop();
    }

    @Override
    public void enterVariable_definition(NobleScriptParser.Variable_definitionContext ctx) {
        log("on enterVariable_definition");
        final VarType newVarType = VarType.getType(ctx.type().getText());
        final String newVarId = ctx.ID().getText();
        final Definition newVarDef;

        if (newVarType == VarType.STRING) {
            newVarDef = new StringDefinition(newVarId, newVarType, DefinitionType.VARIABLE, functionStack.isEmpty() ? "" : functionStack.peek(), -1);
        } else {
            newVarDef = new Definition(newVarId, newVarType, DefinitionType.VARIABLE, functionStack.isEmpty() ? "" : functionStack.peek());
        }

        // Check function scope
        final String scopeId = functionStack.empty() ? GLOBAL_SCOPE_STACK_ID : functionStack.peek();
        verifyIdIsAvailableInCurrentScope(newVarId, ctx.getStart().getLine(), ctx.ID().getSymbol().getCharPositionInLine());

        functionDefs.get(scopeId).put(newVarId, newVarDef);
    }

    @Override
    public void exitVariable_definition(NobleScriptParser.Variable_definitionContext ctx) {
        log("on exitVariable_definition");
        final Definition varDef = getVarDefinition(ctx.ID().getText(), ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine());
        Value value = valueStack.pop();

        // Type casting
        if (varDef.type == VarType.DOUBLE && (value.type == VALUE_INT || value.type == VALUE_INT_REGISTER)) {
            generator.i32_to_double(value.content);
            value = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
        }

        switch (value.type) {
            case VALUE_INT:
            case VALUE_INT_REGISTER:
                if (varDef.type != VarType.INT && varDef.type != VarType.VAR) {
                    throw new InvalidAssignmentException(varDef, value, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
                }
                varDef.type = VarType.INT;
                generator.declare_i32(varDef.getLlvmId(), isBlank(varDef.scope));
                generator.assign_i32(varDef.getLlvmId(), value.content, isBlank(varDef.scope));
                break;
            case VALUE_DOUBLE:
            case VALUE_DOUBLE_REGISTER:
                if (varDef.type != VarType.DOUBLE && varDef.type != VarType.VAR) {
                    throw new InvalidAssignmentException(varDef, value, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
                }
                varDef.type = VarType.DOUBLE;
                generator.declare_double(varDef.getLlvmId(), isBlank(varDef.scope));
                generator.assign_double(varDef.getLlvmId(), value.content, isBlank(varDef.scope));
                break;
            case VALUE_STRING:
                if (varDef.type != VarType.STRING) {
                    throw new InvalidAssignmentException(varDef, value, ctx.getStart().getLine(), ctx.expression().getStart().getCharPositionInLine());
                }
                String content = ctx.expression().expression0().expression1().expression2().expression3().value().getText();
                content = content.substring(1, content.length() - 1);
                ((StringDefinition) varDef).length = content.length();
                generator.assign_string(varDef.id, content, isBlank(varDef.scope), functionStack.empty() ? "" : functionStack.peek());
                break;
            default:
                // TODO add more types in exitVariable_definition
                throw new UnsupportedOperationException("Variable definition for type: " + value.type);
        }
    }

    @Override
    public void enterArray_definition(NobleScriptParser.Array_definitionContext ctx) {
        log("on enterArray_definition");
        final VarType newArrayType = VarType.getType(ctx.type().getText());
        final String newArrayId = ctx.ID().getText();
        final int newArraySize = Integer.parseInt(ctx.INT_LITERAL().getText());
        final ArrayDefinition newArrayDef = new ArrayDefinition(newArrayId, newArrayType, functionStack.isEmpty() ? null : functionStack.peek(), newArraySize);

        // Check function scope
        final String scopeId = functionStack.empty() ? GLOBAL_SCOPE_STACK_ID : functionStack.peek();
        verifyIdIsAvailableInCurrentScope(newArrayId, ctx.getStart().getLine(), ctx.ID().getSymbol().getCharPositionInLine());

        functionDefs.get(scopeId).put(newArrayId, newArrayDef);

        switch (newArrayType) {
            case INT:
                generator.declare_i32_array(newArrayDef.getLlvmId(), newArraySize, functionStack.empty());
                break;
            case DOUBLE:
                generator.declare_double_array(newArrayDef.getLlvmId(), newArraySize, functionStack.empty());
                break;
            default:
                throw new UnsupportedOperationException("Array definition for type: " + newArrayType.type);
        }
    }

    @Override
    public void exitArray_definition(NobleScriptParser.Array_definitionContext ctx) {
        log("on exitArray_definition");
    }

    @Override
    public void enterFunction_param(NobleScriptParser.Function_paramContext ctx) {
        log("on enterFunction_param");
    }

    @Override
    public void exitFunction_param(NobleScriptParser.Function_paramContext ctx) {
        log("on exitFunction_param");
    }

    @Override
    public void enterExpression(NobleScriptParser.ExpressionContext ctx) {
        log("on enterExpression");
    }

    @Override
    public void exitExpression(NobleScriptParser.ExpressionContext ctx) {
        log("on exitExpression");
        if (ctx.expression0() != null) {
            if (ctx.expression0().operator0() != null) {
                Value right = valueStack.pop();
                Value left = valueStack.pop();

                if (ctx.expression0().operator0().EQUAL_OP() != null) {
                    generator.icmp(left.content, right.content, "eq");
                } else if (ctx.expression0().operator0().NOT_EQUAL_OP() != null) {
                    generator.icmp(left.content, right.content, "nq");
                } else if (ctx.expression0().operator0().GREATER_THAN_OP() != null) {
                    generator.icmp(left.content, right.content, "sgt");
                } else if (ctx.expression0().operator0().LESSER_THAN_OP() != null) {
                    generator.icmp(left.content, right.content, "slt");
                } else if (ctx.expression0().operator0().GREATER_THAN_OR_EQUAL_OP() != null) {
                    generator.icmp(left.content, right.content, "sge");
                } else if (ctx.expression0().operator0().LESSER_THAN_OR_EQUAL_OP() != null) {
                    generator.icmp(left.content, right.content, "sle");
                }
                valueStack.push(new Value("%" + (generator.getRegister() - 1), VALUE_BOOLEAN_REGISTER));
            }
        }
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

            // Type casting
            final List<ValueType> intTypes = new ArrayList<ValueType>() {{
                add(VALUE_INT);
                add(VALUE_INT_REGISTER);
            }};
            final List<ValueType> doubleTypes = new ArrayList<ValueType>() {{
                add(VALUE_INT);
                add(VALUE_INT_REGISTER);
            }};

            if (intTypes.contains(left.type) && !doubleTypes.contains(right.type)) {
                generator.i32_to_double(left.content);
                left = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
            } else if (intTypes.contains(right.type) && !doubleTypes.contains(left.type)) {
                generator.i32_to_double(right.content);
                right = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
            }

            // Check variable types
            if (right.type.notEquals(left.type)) {
                TerminalNode token = ctx.operator1().MINUS_OP();
                if (token == null) token = ctx.operator1().PLUS_OP();
                throw new TypeMismatchException("Invalid type at line: " + token.getSymbol().getLine(), token.getSymbol().getLine(), token.getSymbol().getCharPositionInLine());
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
                    throw new UnsupportedOperationException("Expression1 for type: " + right.type);
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

            // Type casting
            final List<ValueType> intTypes = new ArrayList<ValueType>() {{
                add(VALUE_INT);
                add(VALUE_INT_REGISTER);
            }};
            final List<ValueType> doubleTypes = new ArrayList<ValueType>() {{
                add(VALUE_INT);
                add(VALUE_INT_REGISTER);
            }};

            if (intTypes.contains(left.type) && !doubleTypes.contains(right.type)) {
                generator.i32_to_double(left.content);
                left = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
            } else if (intTypes.contains(right.type) && !doubleTypes.contains(left.type)) {
                generator.i32_to_double(right.content);
                right = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE);
            }


            // Check variable types
            if (right.type.notEquals(left.type)) {
                TerminalNode token = ctx.operator2().DIV_OP();
                if (token == null) token = ctx.operator2().MUL_OP();
                throw new TypeMismatchException("Invalid type at line: " + token.getSymbol().getLine(), token.getSymbol().getLine(), token.getSymbol().getCharPositionInLine());
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
                    throw new UnsupportedOperationException("Expression2 for type: " + right.type);
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
            Definition varDef = getVarDefinition(valueId, ctx.getStart().getLine(), ctx.ID().getSymbol().getCharPositionInLine());

            // TODO more types
            final Value value;
            switch (varDef.type) {
                case INT:
                    generator.load_i32(varDef.getLlvmId(), isBlank(varDef.scope));
                    value = new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER);
                    break;
                case DOUBLE:
                    generator.load_double(varDef.getLlvmId(), isBlank(varDef.scope));
                    value = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE_REGISTER);
                    break;
                case STRING:
                    String scopePrefix = isBlank(varDef.scope) ? "" : (varDef.scope + ".");
                    value = new StringValue(scopePrefix + varDef.id, VALUE_STRING_REGISTER, ((StringDefinition) varDef).length);
                    break;
                default:
                    throw new UnsupportedOperationException("Value for type: " + varDef.type);
            }
            valueStack.push(value);

        } else if (ctx.array_index() != null) {
            final String arrayId = ctx.array_index().ID().getText();
            final ArrayDefinition arrayDef = (ArrayDefinition) getVarDefinition(arrayId, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            final int indexValue = Integer.parseInt(ctx.array_index().INT_LITERAL().getText());

            if (indexValue >= arrayDef.size) {
                throw new ArrayIndexOutOfBoundException(arrayDef, indexValue, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            }

            final Value value;
            switch (arrayDef.type) {
                case INT:
                    generator.load_i32_array_index(arrayDef.getLlvmId(), arrayDef.size, indexValue, isBlank(arrayDef.scope));
                    value = new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER);
                    break;
                case DOUBLE:
                    generator.load_double_array_index(arrayDef.getLlvmId(), arrayDef.size, indexValue, isBlank(arrayDef.scope));
                    value = new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE_REGISTER);
                    break;
                default:
                    throw new UnsupportedOperationException("Array index for type: " + arrayDef.type);
            }
            valueStack.push(value);
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

        int argc = ctx.expression().toArray().length;
        List<Value> params = new ArrayList<>();
        while (argc-- > 0) {
            params.add(valueStack.pop());
        }
        Collections.reverse(params);
        if (params.stream().anyMatch(param -> param.type.notEquals(VALUE_INT))) {
            throw new NobleScriptException("Values of type other than int are not supported in function calls",
                    ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        }

        if (ctx.ID() != null) {
            if (!functionDefs.containsKey(ctx.ID().getText())) {
                throw new IDNotDefinedException(ctx.ID().getText(),
                        ctx.getStart().getLine(), ctx.PAR_OPEN().getSymbol().getCharPositionInLine());
            }
            generator.call(ctx.ID().getText(), params);
            valueStack.push(new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER));
        }
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
        } else if (value.type == VALUE_STRING_REGISTER) {
            generator.printf_string(value.content, ((StringValue) value).length);
        } else if (value.type == VALUE_STRING) {
            generator.printf_string_literal(value.content.substring(1, value.content.length() - 1));
        } else {
            throw new UnsupportedOperationException("Print statement for type: " + value.type);
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
        blockStack.push(BlockType.WHILE_BLOCK);
        generator.while_start();
    }

    @Override
    public void exitLoop_statement(NobleScriptParser.Loop_statementContext ctx) {
        log("on exitLoop_statement");
    }

    @Override
    public void enterIf_statement(NobleScriptParser.If_statementContext ctx) {
        log("on enterIf_statement");
        blockStack.push(BlockType.IF_BLOCK);
    }

    @Override
    public void exitIf_statement(NobleScriptParser.If_statementContext ctx) {
        log("on exitIf_statement");
    }

    @Override
    public void enterElif_statement(NobleScriptParser.Elif_statementContext ctx) {
        log("on enterElif_statement");
    }

    @Override
    public void exitElif_statement(NobleScriptParser.Elif_statementContext ctx) {
        log("on exitElif_statement");
    }

    @Override
    public void enterElse_statement(NobleScriptParser.Else_statementContext ctx) {
        log("on enterElse_statement");
    }

    @Override
    public void exitElse_statement(NobleScriptParser.Else_statementContext ctx) {
        log("on exitElse_statement");
    }

    @Override
    public void enterBlock_open(NobleScriptParser.Block_openContext ctx) {
        log("on enterBlock_open");
        String newFunId;
        String scopeId;
        String newScopeId;
        switch (blockStack.peek()) {
            case IF_BLOCK:
                generator.if_start();

                newFunId = "_if" + (generator.getBr() - 1);
                scopeId = functionStack.empty() ? "" : functionStack.peek();
                newScopeId = (scopeId.equals("") ? "" : scopeId + ".") + newFunId;

                functionDefs.put(newScopeId, new HashMap<>());
                functionStack.push(newScopeId);

                break;
            case FUNCTION_BLOCK:
                break;
            case WHILE_BLOCK:
                generator.whilebody_start();

                newFunId = "_while" + (generator.getBr() - 1);
                scopeId = functionStack.empty() ? "" : functionStack.peek();
                newScopeId = (scopeId.equals("") ? "" : scopeId + ".") + newFunId;

                functionDefs.put(newScopeId, new HashMap<>());
                functionStack.push(newScopeId);

                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void exitBlock_open(NobleScriptParser.Block_openContext ctx) {
        log("on exitBlock_open");
    }

    @Override
    public void enterBlock_close(NobleScriptParser.Block_closeContext ctx) {
        log("on enterBlock_close");
    }

    @Override
    public void exitBlock_close(NobleScriptParser.Block_closeContext ctx) {
        log("on exitBlock_close");
        switch (blockStack.pop()) {
            case IF_BLOCK:
                generator.if_end();
                functionStack.pop();
                break;
            case FUNCTION_BLOCK:
                break;
            case WHILE_BLOCK:
                generator.whilebody_end();
                functionStack.pop();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void enterRead_op(NobleScriptParser.Read_opContext ctx) {
        log("on enterRead_op");
    }

    @Override
    public void exitRead_op(NobleScriptParser.Read_opContext ctx) {
        log("on exitRead_op");

        if (ctx.READ_INT() != null) {
            generator.scanf_i32();
            valueStack.push(new Value("%" + (generator.getRegister() - 1), VALUE_INT_REGISTER));
        } else if (ctx.READ_DOUBLE() != null) {
            generator.scanf_fouble();
            valueStack.push(new Value("%" + (generator.getRegister() - 1), VALUE_DOUBLE_REGISTER));
        }
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

    private Definition getVarDefinition(String varId, int line, int col) {
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
            throw new IDNotDefinedException(varId, line, col);
        }
    }

    private void verifyIdIsAvailableInCurrentScope(String id, int line, int col) {
        // Check function scope
        final String scopeId = functionStack.empty() ? GLOBAL_SCOPE_STACK_ID : functionStack.peek();
        if (functionDefs.containsKey(scopeId)) {
            if (functionDefs.get(scopeId).containsKey(id)) {
                throw new IDAlreadyDefinedException(id, scopeId, line, col);
            }
        } else {
            throw new IllegalStateException("ID {" + id + "} is not accessible in current scope {" + scopeId + "}");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isEmpty() || value.trim().isEmpty();
    }

    private void log(String msg) {
        if (logLevel > 0) System.out.println(msg);
    }
}

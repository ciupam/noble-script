grammar NobleScript;

program: statement* EOF;

statement: compound_statement
         | definition
         | function_call_stm SEMICOL
         | assign_statement SEMICOL
         | return_statement SEMICOL
         ;
assign_statement: ID ASSIGN_OP expression
                | ID BRACKET_OPEN INT_LITERAL BRACKET_CLOSE ASSIGN_OP expression
                ;
return_statement: RETURN expression;

definition: variable_definition SEMICOL
          | structure_definition
          | function_definition
          | array_definition SEMICOL
          ;

structure_definition: 'struct' ID block_open (variable_definition SEMICOL)+ block_close;
function_definition: type ID PAR_OPEN function_param?(',' function_param)* PAR_CLOSE block_open statement* block_close;
variable_definition: type ID ASSIGN_OP expression;
array_definition: type ID BRACKET_OPEN INT_LITERAL BRACKET_CLOSE;
function_param: type ID;

expression : expression0;

expression0 : expression1 | expression0 operator0 expression0;
expression1 : expression2 | expression1 operator1 expression1;
expression2 : expression3 | expression2 operator2 expression2;
expression3 : PAR_OPEN expression0 PAR_CLOSE
            | value;

value: literal
     | function_call_stm
     | ID
     | array_index
     ;

array_index: ID BRACKET_OPEN INT_LITERAL BRACKET_CLOSE;

function_call_stm: ID '(' (expression)? (','expression)* ')' | read_op | print_stm;
print_stm: 'print(' expression ')';

literal: primitive_literal;
primitive_literal: BOOLEAN_LITERAL #boolean | INT_LITERAL #int | DOUBLE_LITERAL #double | STRING_LITERAL #string | NULL #null;

type: primitive_type;

primitive_type: BOOLEAN_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | NULL | VAR_TYPE;

operator2: DIV_OP | MUL_OP;
operator1: PLUS_OP | MINUS_OP;
operator0: LESSER_THAN_OP | GREATER_THAN_OP | EQUAL_OP | NOT_EQUAL_OP | GREATER_THAN_OR_EQUAL_OP | LESSER_THAN_OR_EQUAL_OP;

compound_statement: if_statement | loop_statement;
loop_statement: WHILE PAR_OPEN expression PAR_CLOSE block_open statement* block_close;
if_statement: IF PAR_OPEN expression PAR_CLOSE block_open statement* block_close (elif_statement)* (else_statement)?;
elif_statement: ELIF PAR_OPEN expression PAR_CLOSE block_open statement* block_close;
else_statement: ELSE block_open statement* block_close;

block_open: BRACES_OPEN;
block_close: BRACES_CLOSE;

read_op: READ_INT | READ_DOUBLE;

READ_DOUBLE: 'readDouble()';
READ_INT: 'readInt()';
RETURN: 'return';

WHILE: 'while';
IF: 'if';
ELIF: 'elif';
ELSE: 'else';

PAR_OPEN: '(';
PAR_CLOSE: ')';
BRACES_OPEN: '{';
BRACES_CLOSE: '}';
BRACKET_OPEN: '[';
BRACKET_CLOSE: ']';

NULL: 'null';
INT_LITERAL: '-'?([1-9]+[0-9]*)|('0');
DOUBLE_LITERAL: '-'?([1-9]+[0-9]*'.'[0-9]+)|'-'?('0.'[0-9]+);
BOOLEAN_LITERAL: 'true' | 'false';
STRING_LITERAL: '"'(~('"'))*'"';

BOOLEAN_TYPE: 'boolean';
INT_TYPE: 'int';
DOUBLE_TYPE: 'double';
STRING_TYPE: 'string';
VAR_TYPE: 'var';

ID: [a-zA-Z][a-zA-Z0-9_-]*;

SEMICOL: ';';
ASSIGN_OP: '=';
LESSER_THAN_OP: '<';
LESSER_THAN_OR_EQUAL_OP: '<=';
GREATER_THAN_OP: '>';
GREATER_THAN_OR_EQUAL_OP: '>=';
EQUAL_OP: '==';
NOT_EQUAL_OP: '!=';
PLUS_OP: '+';
MINUS_OP: '-';
POW_OP: '^';
DIV_OP: '/';
MUL_OP: '*';

WHITESPACE: [ \t] -> skip;
NEWLINE: [\r\n] -> skip;
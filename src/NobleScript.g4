grammar NobleScript;

program: statement* EOF;

statement: compound_statement
         | definition
         | function_call_stm SEMICOL
         | assign_statement SEMICOL
         | return_statement SEMICOL
         ;
assign_statement: ID ASSIGN_OP expression;
return_statement: RETURN expression;

definition: variable_definition SEMICOL
          | structure_definition
          | function_definition
          ;

structure_definition: 'struct' ID BRACES_OPEN (variable_definition SEMICOL)+ BRACES_CLOSE;
function_definition: type ID PAR_OPEN (type ID)?(',' type ID)* PAR_CLOSE BRACES_OPEN statement* BRACES_CLOSE;
variable_definition: type ID ASSIGN_OP expression;

expression : expression0;

expression0 : expression1 | expression1 operator0 expression1;
expression1 : expression2 | expression2 operator1 expression2;
expression2 : expression3 | expression3 operator2 expression3;
expression3 : PAR_OPEN expression0 PAR_CLOSE
            | value;

value: literal
     | function_call_stm
     | ID
     ;

function_call_stm: ID '(' (expression)? (','expression)* ')' | READ | print_stm;
print_stm: 'print(' expression ')';

literal: primitive_literal | array_literal;
primitive_literal: BOOLEAN_LITERAL #boolean | INT_LITERAL #int | DOUBLE_LITERAL #double | STRING_LITERAL #string | NULL #null;
array_literal: primitive_type ARRAY_SIZE_LITERAL;

type: primitive_type
    | array_type
    ;
array_type: primitive_type '[]';

primitive_type: BOOLEAN_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | NULL;

operator3: POW_OP;
operator2: DIV_OP | MUL_OP;
operator1: PLUS_OP | MINUS_OP;
operator0: LESSER_THAN_OP | GREATER_THAN_OP | EQUAL_OP | NOT_EQUAL_OP;

compound_statement: if_statement | loop_statement;
loop_statement: WHILE PAR_OPEN expression PAR_CLOSE BRACES_OPEN statement* BRACES_CLOSE;
if_statement: IF PAR_OPEN expression PAR_CLOSE BRACES_OPEN statement* BRACES_CLOSE (ELIF PAR_OPEN expression PAR_CLOSE BRACES_OPEN statement* BRACES_CLOSE)* (ELSE BRACES_OPEN statement* BRACES_CLOSE)?;

READ: 'read()';
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
BRAKCET_CLOSE: ']';

NULL: 'null';
INT_LITERAL: '-'?[1-9]+[0-9]*;
DOUBLE_LITERAL: '-'?[1-9]+[0-9]*'.'[0-9]+;
BOOLEAN_LITERAL: 'true' | 'false';
STRING_LITERAL: '"'[^"]*'"';
ARRAY_SIZE_LITERAL: '['[1-9]+[0-9]*']';

BOOLEAN_TYPE: 'boolean';
INT_TYPE: 'int';
DOUBLE_TYPE: 'double';
STRING_TYPE: 'string';

ID: [a-zA-Z][a-zA-Z0-9_-]*;

SEMICOL: ';';
ASSIGN_OP: '=';
LESSER_THAN_OP: '<';
GREATER_THAN_OP: '>';
EQUAL_OP: '==';
NOT_EQUAL_OP: '!=';
PLUS_OP: '+';
MINUS_OP: '-';
POW_OP: '^';
DIV_OP: '/';
MUL_OP: '*';

WHITESPACE: [ \t] -> skip;
NEWLINE: [\r\n] -> skip;
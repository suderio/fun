grammar Fun;


prog      : line* EOF;

line      : expression SEMICOLON;

expression
    : logicalOrExpression      # ExpressionLogicalOr
//    | unaryOperatorDefinition  # UnaryOperatorDefinitionExpr
//    | unaryOperatorCall        # UnaryOperatorCallExpr
//    | tableConstructor         # TableExpr
//    | tableConcatenation       # TableConcatExpr
    ;


// Definição de tabelas
tableConstructor
    : LBRACK (tableElement)* RBRACK  # TableConstruct
    ;

tableConcatenation
    : (unaryOperatorCall|logicalOrExpression) (SEPARATOR tableElement)+      # TableConcat
    ;

// Elementos da tabela
tableElement
    : expr=expression                             # TableElementExpr
    | expression ASSIGN expression           # TableKeyValue
    ;
// Definição de operadores unários
unaryOperatorDefinition
    : ID ASSIGN LCURBR expression RCURBR  # DefineUnaryOperator
    ;

// Chamada de operadores unários
unaryOperatorCall
    : ID expression                       # CallUnaryOperator
    ;

logicalOrExpression
    : logicalOrExpression OR_SHORT logicalAndExpression  # LogicalOr
    | logicalAndExpression                                # LogicalOrTerm
    ;

logicalAndExpression
    : logicalAndExpression AND_SHORT comparisonExpression  # LogicalAnd
    | comparisonExpression                                # LogicalAndTerm
    ;

comparisonExpression
    : additionExpression (comparisonOperator additionExpression)*  # Comparison
    ;

comparisonOperator
    : EQ    # Equals
    | NE    # NotEquals
    | LT    # LessThan
    | LE    # LessThanOrEqual
    | GT    # GreaterThan
    | GE    # GreaterThanOrEqual
    ;

additionExpression
    : additionExpression (PLUS | MINUS) multiplicationExpression  # Addition
    | multiplicationExpression                                   # AdditionTerm
    ;

multiplicationExpression
    : multiplicationExpression op=(ASTERISK | DIVISION | MOD) powerExpression  # Multiplication
    | powerExpression                                                      # MultiplicationTerm
    ;

powerExpression
    : unaryExpression (EXP unaryExpression)*  # Power
    ;

unaryExpression
    : (PLUS | MINUS | NOT) unaryExpression    # UnaryOperator
    | assignmentOrIdExpression                    # UnaryTerm
    ;

assignmentOrIdExpression
    : ID (ASSIGN expression)*   # Assignment
//    | ID                        # Variable
    | primaryExpression         # Primary
    ;

primaryExpression
    : NUMBER                                  # Number
    | STRING                                  # String
    | TRUE                                    # TrueLiteral
    | FALSE                                   # FalseLiteral
    | NULL                                    # NullLiteral
    | LPAREN expression RPAREN               # Parenthesized
    ;

// Whitespace
NEWLINE            : '\r\n' | '\r' | '\n' ;
WS                 : [\r\n\t ]+ -> skip ;
COMMENT            : '#' [.]*? NEWLINE -> channel(HIDDEN)  ;
BLOCK_COMMENT : NEWLINE? [ \t]* '###' .*? NEWLINE [ \t]* '###' -> channel(HIDDEN);

// Keywords
THIS                : 'this' ;
IT                  : 'it'   ;
TRUE                : 'true' ;
FALSE               : 'false';
NULL                : 'null' ;

// Literals
INTEGER            : [0-9]+;
DECIMAL            : '0'|[1-9][0-9]* '.' [0-9]+ ;
NUMBER             : INTEGER | DECIMAL;
SIMPLESTRING       : '"' (~["\\] | '\\' .)* '"';
DOCSTRING          : '"""' .*? '"""';
STRING             : SIMPLESTRING | DOCSTRING;

// Operators

SEPARATOR          : ',' ;
ASSIGN             : ':' ;
LPAREN             : '(' ;
RPAREN             : ')' ;
LBRACK             : '[' ;
RBRACK             : ']' ;
LCURBR             : '{' ;
RCURBR             : '}' ;
SEMICOLON          : ';' ;

// Arithmetic
PLUS               : '+' ;
MINUS              : '-' ;
ASTERISK           : '*' ;
DIVISION           : '/' ;
EXP                : '**' ;
MOD                : '%' ;
// Logical
NOT                : '~' ;
AND                : '&' ;
OR                 : '|' ;
AND_SHORT          : '&&';
OR_SHORT           : '||';
XOR                : '^' ;
// Comparison
EQ                 : '=' ;
LE                 : '<=';
LT                 : '<' ;
GE                 : '>=';
GT                 : '>' ;
NE                 : '<>' | '~=';

// OTHER
LSHIFT             : '<<';
RSHIFT             : '>>';
ELIPSIS            :'...';
RANGE              : '..';
DEREF              : '.' ;
REDIRECT           : '@' ;
STRINGSUBST        : '$' ;
NULLTEST           : '?' ;
// Function


// Identifiers
ID                 : [_]+[A-Za-z0-9_]* | [A-Za-z]+[A-Za-z0-9_]*;
// Should not be used
ANY                : . ;


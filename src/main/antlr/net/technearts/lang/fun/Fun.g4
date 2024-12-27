grammar Fun;

// Whitespace
NEWLINE            : '\r\n' | '\r' | '\n' ;
WS                 : [\r\n\t ]+ -> skip ;
COMMENT            : '#' [.]*? NEWLINE -> channel(HIDDEN)  ;
STRING             : '"' (~["\\] | '\\' .)* '"';
BLOCK_COMMENT : NEWLINE? [ \t]* '###' .*? NEWLINE [ \t]* '###' -> channel(HIDDEN);

// Keywords
THIS                : 'this' ;
IT                  : 'it'   ;
TRUE                : 'true' ;
FALSE               : 'false';
NULL                : 'null' ;

// Literals
NUMBER             : [0-9]+;

// Operators
LSHIFT             : '<<';
RSHIFT             : '>>';
SEPARATOR          : ',' ;
ASSIGN             : ':' ;
LPAREN             : '(' ;
RPAREN             : ')' ;
LBRACK             : '[' ;
RBRACK             : ']' ;
LCURBR             : '{' ;
RCURBR             : '}' ;
ELIPSIS            :'...';
RANGE              : '..';
DEREF              : '.' ;
REDIRECT           : '@' ;
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
NE                 : '<>';
// Function
LREF               : '$' ;
RREF               : '?' ;
SEMICOLON          : ';' ;
// Identifiers
ID                 : [_]+[A-Za-z0-9_]* | [A-Za-z]+[A-Za-z0-9_]*;
// Should not be used
ANY                : . ;


prog      : line* EOF;

line      : expression SEMICOLON;

expression
    : assignmentExpression     # ExpressionAssignment
    | logicalOrExpression      # ExpressionLogicalOr
    ;

assignmentExpression
    : ID ASSIGN expression     # Assignment
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
    : multiplicationExpression (ASTERISK | DIVISION | MOD) powerExpression  # Multiplication
    | powerExpression                                                      # MultiplicationTerm
    ;

powerExpression
    : unaryExpression (EXP unaryExpression)*  # Power
    ;

unaryExpression
    : (PLUS | MINUS | NOT) unaryExpression    # UnaryOperator
    | primaryExpression                       # UnaryTerm
    ;

primaryExpression
    : NUMBER                                  # Number
    | STRING                                  # String
    | TRUE                                    # TrueLiteral
    | FALSE                                   # FalseLiteral
    | NULL                                    # NullLiteral
    | ID                                      # Variable
    | LPAREN expression RPAREN               # Parenthesized
    ;

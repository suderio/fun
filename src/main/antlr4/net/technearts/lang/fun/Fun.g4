grammar Fun;

file                : assignExpression*
                    ;
assignExpression    : ID ASSIGN expression                          #assignExp
                    | ID ASSIGN LCURBR expression RCURBR                #operatorExp
                    | expression                                        #nonAssignExp
                    ;

expression          : LPAREN expression RPAREN                          #parenthesisExp
                    | (PLUS | MINUS | NOT) expression                   #unaryExp
                    | expression DEREF expression                       #derefExp
                    | <assoc=right>  expression EXP expression          #powerExp
                    | expression (ASTERISK|SLASH|PERCENT) expression    #mulDivModExp
                    | expression (PLUS|MINUS) expression                #addSubExp
                    | expression comparisonOperator expression          #comparisonExp
                    | expression (AND|AND_SHORT) expression             #andExp
                    | expression (OR|OR_SHORT) expression               #orExp
                    | expression XOR expression                         #xorExp
                    | expression SEMICOLON (expression SEMICOLON)*      #tableConcatSemi
                    | expression (SEPARATOR expression)+                #tableConcatSep
                    | LBRACK (expression)* RBRACK                       #tableConstruct
                    | expression NULLTEST expression                    #nullTestExp
                    | expression TEST expression                        #testExp
                    | ID expression                                     #callExp
                    | THIS expression                                   #thisExp
                    | STRING                                            #stringLiteral
                    | TRUE                                              #trueLiteral
                    | FALSE                                             #falseLiteral
                    | NULL                                              #nullLiteral
                    | INTEGER                                           #integerExp
                    | DECIMAL                                           #decimalExp
                    | ID                                                #idAtomExp
                    | IT                                                #itAtomExp
                    ;

comparisonOperator
    : EQ    # Equals
    | NE    # NotEquals
    | LT    # LessThan
    | LE    # LessThanOrEqual
    | GT    # GreaterThan
    | GE    # GreaterThanOrEqual
    ;
// Whitespace
NEWLINE            : '\r\n' | '\r' | '\n' ;
WS                 : [\r\n\t ]+ -> channel(HIDDEN) ;
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
SLASH              : '/' ;
EXP                : '**';
PERCENT            : '%' ;

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
ACC                : '+=';
DECC               : '-=';
MULT               : '*=';
DIV                : '/=';
MOD                : '%=';
ELIPSIS            :'...';
RANGE              : '..';
DEREF              : '.' ;
REDIRECT           : '@' ;
DOLAR              : '$' ;
TEST               : '?' ;
NULLTEST           : '??';

// Identifiers
ID                 : [_]+[A-Za-z0-9_]* | [A-Za-z]+[A-Za-z0-9_]*;

// Should not be used
ANY                : . ;


lexer grammar FunLexer;

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

// Identifiers
ID                 : [_]+[A-Za-z0-9_]* | [A-Za-z]+[A-Za-z0-9_]*;

// Should not be used
ANY                : . ;


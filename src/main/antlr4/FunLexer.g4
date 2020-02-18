lexer grammar FunLexer;

// Whitespace
NEWLINE            : '\r\n' | 'r' | '\n' ;
WS                 : [\t ]+ -> skip;
COMMENT            : '#' ;
// Keywords
TRUE               : 'true' ;
FALSE              : 'false';
THIS               : 'this' ;
IT                 : 'it'   ;
ANY                : 'any'  ;
NULL               : 'null' ;

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
DECLIT             : '0'|[1-9][0-9]* '.' [0-9]+ ;

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
ASTERISK           : '*' ;
DIVISION           : '/' ;
ASSIGN             : ':' ;
MODULE             : '%' ;
LT                 : '<' ;
GT                 : '>' ;
LE                 : '<=';
GE                 : '>=';
NE                 : '!=';
EQ                 : '=' ;
AND                : '&' ;
ANDCC              : '&&';
OR                 : '|' ;
ORCC               : '||';
NOT                : '!' ;
XOR                : '^' ;
OUTPUT             : '<<';
INPUT              : '>>';
ERROR              : '<!';
FILE               : '@' ;
COND               : '?' ;


LPAREN             : '(' ;
RPAREN             : ')' ;
LBRACK             : '[' ;
RBRACK             : ']' ;
LBRACE             : '{' ;
RBRACE             : '}' ;
SEPARATOR          : ';' ;
// Identifiers
ID                 : [_]*[a-z][A-Za-z0-9_]* ;

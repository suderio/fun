grammar Fun;

file                : (assign SEMICOLON)*                                                   #fileTable
                    ;

assign              : ID ASSIGN expression                                                  #assignExp
                    | expression                                                            #expressionExp
                    ;
expression          : LPAREN expression RPAREN                                              #parenthesisExp
                    | LCURBR op=expression RCURBR                                           #operatorExp
                    | expression DEREF expression                                           #derefExp
                    | <assoc=right> (PLUS|MINUS|NOT|INC|DEC) expression                     #unaryExp
                    | <assoc=right> expression EXP expression                               #powerExp
                    | expression (ASTERISK|SLASH|PERCENT) expression                        #mulDivModExp
                    | expression (PLUS|MINUS) expression                                    #addSubExp
                    | expression DOLAR expression                                           #substExp
                    | expression (RSHIFT|LSHIFT) expression                                 #shiftExp
                    | expression (LT|LE|GE|GT) expression                                   #comparisonExp
                    | expression (EQ|NE) expression                                         #equalityExp
                    | expression (AND_SHORT) expression                                     #andShortExp
                    | expression (AND) expression                                           #andExp
                    | expression XOR expression                                             #xorExp
                    | expression (OR_SHORT) expression                                      #orShortExp
                    | expression (OR) expression                                            #orExp
                    | <assoc=right> expression NULLTEST expression                          #nullTestExp
                    | <assoc=right> expression TEST                                         #testExp
                    | expression (SUM|SUB|MULT|DIV|MOD|LSH|RSH|ANDA|XORA|ORA) expression    #assignOpExp
                    | expression ID expression                                              #biCallExp
                    | expression (SEPARATOR expression)+                                    #tableConcatSepExp
                    | LBRACK (expression|keyValue)* RBRACK                                  #tableConstructExp
                    | expression RANGE expression                                           #rangeExp
                    | expression REDIRECT expression                                        #redirectWriteExp
                    | REDIRECT expression                                                   #redirectReadExp
                    | ID                                                                    #idAtomExp
                    | ID expression                                                         #callExp
                    | THIS expression                                                       #thisExp
                    | SIMPLESTRING                                                          #stringLiteral
                    | DOCSTRING                                                             #docStringLiteral
                    | TRUE                                                                  #trueLiteral
                    | FALSE                                                                 #falseLiteral
                    | NULL                                                                  #nullLiteral
                    | DECIMAL                                                               #decimalLiteral
                    | INTEGER                                                               #integerLiteral
                    | URL                                                                   #urlLiteral
                    | LEFT                                                                  #leftAtomLiteral
                    | RIGHT                                                                 #rightAtomLiteral
                    ;
keyValue            : ID ASSIGN expression;
// Whitespace
NEWLINE             : '\r\n' | '\r' | '\n' ;
WS                  : [\r\n\t ]+ -> channel(HIDDEN) ;
COMMENT             : '#' [.]*? NEWLINE -> channel(HIDDEN)  ;
BLOCK_COMMENT       : NEWLINE? [ \t]* '###' .*? NEWLINE [ \t]* '###' -> channel(HIDDEN);

// Keywords
THIS                : 'this' ;
//IT                  : 'it'   ;
LEFT                : 'left'   ;
RIGHT               : 'right'   ;
TRUE                : 'true' ;
FALSE               : 'false';
NULL                : 'null' ;

// Literals
INTEGER            : [0-9]+;
DECIMAL            : '0'|[1-9][0-9]* '.' [0-9]+ ;
SIMPLESTRING       : '"' (~["\\] | '\\' .)* '"';
DOCSTRING          : '"""' .*? '"""';

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
SUM                : ':+';
SUB                : ':-';
MULT               : ':*';
DIV                : ':/';
MOD                : ':%';
LSH                :':<<';
RSH                :':>>';
ANDA               : ':&';
XORA               : ':^';
ORA                : ':|';
INC                : '++';
DEC                : '--';
RANGE              : '..';
DEREF              : '.' ;
REDIRECT           : '@' ;
DOLAR              : '$' ;
TEST               : '?' ;
NULLTEST           : '??';
ELVIS              : '?:';

// Identifiers
ID                 : [_]+[A-Za-z0-9_]* | [A-Za-z]+[A-Za-z0-9_]*;
URL                : (('http' | 'https' | 'ftp' | 'file') '://' (~[ \n\r\t])+);

// Should not be used
ANY                : . ;

// ?: <=> -> <-
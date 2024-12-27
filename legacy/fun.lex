%{
#define YYSTYPE union
{

  /* NUMBER  */
  double NUMBER;
  /* expression  */
  double expression;
  /* VAR  */
  symrec* VAR;
  /* FUN  */
  symrec* FUN;
}
#include "fun.tab.h"
#include <stdlib.h>
%}

white [ \t]+
digit [0-9]
integer {digit}+
exponent [eE][+-]?{integer}
real {integer}("."{integer})?{exponent}?
identifier [_a-zA-Z][_a-zA-Z0-9]*
%option yylineno
%%

{white} { }
{real} { yylval=atof(yytext); 
  return NUMBER;
}
{identifier} { yylval=yytext;
  return VAR;
}

"+" return PLUS;
"-" return MINUS;
"*" return TIMES;
"/" return DIVIDE;
"^" return POWER;
"(" return LPAREN;
")" return RPAREN;
"\n" return END;

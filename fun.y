
%{
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#define YYSTYPE double
int yylex (void);
void yyerror (char const *);
%}

%token NUMBER
%token LEFT RIGHT
%token END

%left PLUS MINUS
%left TIMES DIVIDE
%precedence NEG
%right POWER

%start Fun
%%

Fun:
    
     | Fun Line
;

Line:
     END
     | Expression END { printf("Result: %f\n", $1); }
     | error END { yyerrok; }
;

Expression:
     NUMBER { $$=$1; }
| Expression PLUS Expression { $$=$1+$3; }
| Expression MINUS Expression { $$=$1-$3; }
| Expression TIMES Expression { $$=$1*$3; }
| Expression DIVIDE Expression
    {
      if ($3)
        $$ = $1 / $3;
      else
        {
          $$ = 1;
          fprintf (stderr, "%d.%d-%d.%d: division by zero",
                   @3.first_line, @3.first_column,
                   @3.last_line, @3.last_column);
        }
    }
| MINUS Expression %prec NEG { $$=-$2; }
| Expression POWER Expression { $$=pow($1,$3); }
| LEFT Expression RIGHT { $$=$2; }
;

%%

extern FILE* yyin;
int main(int argc, char *argv[]) {
  if(argc > 0) {
    yyin = fopen(argv[1], "r");
  } 
  if (yyparse())
     fprintf(stderr, "Successful parsing.\n");
  else
     fprintf(stderr, "error found.\n");
}

extern char* yytext;
extern int yylineno;
void yyerror(char const *s) {
  printf("%s on line %d - %s\n", s, yylineno, yytext);
}

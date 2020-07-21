
%{
#include "fun.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
int yylex (void);
void yyerror (char const *);
%}

%define api.value.type union /* Generate YYSTYPE from these types: */
%token <double>  NUMBER     /* Double precision number. */
%token <symrec*> VAR FUN /* Symbol table pointer: variable/function. */
%nterm <double>  expression

%token LPAREN RPAREN
%token END

%left PLUS MINUS
%left TIMES DIVIDE
%precedence NEG
%right POWER

%start fun
%%

fun:
    
     | fun expressions
;

expressions:
  END
| expression END { printf("fun> %f\n", $1); }
| error END { yyerrok; }
;

expression:
  NUMBER { $$=$1; }
| VAR { $$ = $1->value.var; }
| VAR ':' expression { $$ = $3; $1->value.var = $3; }
| FUN LPAREN expression RPAREN { $$ = $1->value.fun ($3); }
| expression PLUS expression { $$=$1+$3; }
| expression MINUS expression { $$=$1-$3; }
| expression TIMES expression { $$=$1*$3; }
| expression DIVIDE expression {
    if ($3)
      $$ = $1 / $3;
    else {
      $$ = 1;
      fprintf (stderr, "%d.%d-%d.%d: division by zero",
               @3.first_line, @3.first_column,
               @3.last_line, @3.last_column);
    }
  }
| MINUS expression %prec NEG { $$=-$2; }
| expression POWER expression { $$=pow($1,$3); }
| LPAREN expression RPAREN { $$=$2; }
;

%%

struct init {
  char const *name;
  func_t *fun;
};

struct init const arith_funs[] = {
  { "atan", atan },
  { "cos",  cos  },
  { "exp",  exp  },
  { "ln",   log  },
  { "sin",  sin  },
  { "sqrt", sqrt },
  { 0, 0 },
};

/* The symbol table: a chain of 'struct symrec'. */
symrec *sym_table;

/* Put arithmetic functions in table. */
static void
init_table (void) {
  for (int i = 0; arith_funs[i].name; i++)
    {
      symrec *ptr = putsym (arith_funs[i].name, FUN);
      ptr->value.fun = arith_funs[i].fun;
    }
}

/* The mfcalc code assumes that malloc and realloc
   always succeed, and that integer calculations
   never overflow.  Production-quality code should
   not make these assumptions.  */
#include <stdlib.h> /* malloc, realloc. */
#include <string.h> /* strlen. */

symrec * putsym (char const *name, int sym_type) {
  symrec *res = (symrec *) malloc (sizeof (symrec));
  res->name = strdup (name);
  res->type = sym_type;
  res->value.var = 0; /* Set value to 0 even if fun. */
  res->next = sym_table;
  sym_table = res;
  return res;
}

symrec * getsym (char const *name) {
  for (symrec *p = sym_table; p; p = p->next)
    if (strcmp (p->name, name) == 0)
      return p;
  return NULL;
}

extern FILE* yyin;
extern int yydebug;

int main(int argc, char *argv[]) {
  /* Enable parse traces on option -p. */
  if (argc == 2 && strcmp(argv[1], "-p") == 0)
    yydebug = 1;
  init_table ();
  
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

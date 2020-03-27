##
# fun
#
# @file
# @version 0.1
#

fun: lex.yy.c fun.tab.c
	gcc -o fun lex.yy.c fun.tab.c -lfl -lm

lex.yy.c: fun.lex
	flex fun.lex

fun.tab.c: fun.y
	bison -d fun.y
# end

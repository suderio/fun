##
# fun
#
# @file
# @version 0.1

fun: fun.y fun.lex
	bison -d fun.y
	flex  -o fun.lex.c fun.lex
	gcc  -o fun fun.lex.c fun.tab.c -lfl -lm
# end

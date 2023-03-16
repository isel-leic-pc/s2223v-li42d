.PHONY: clean

uthreads.exe: prog.c uthread.c context_switch.s
	gcc -o uthreads.exe prog.c uthread.c context_switch.s list.c gate.c

clean:
	del uthreads.exe

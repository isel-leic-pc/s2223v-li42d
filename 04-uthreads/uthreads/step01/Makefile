.PHONY: clean

uthreads.exe: prog.c uthread.c context_switch.s
	gcc -o uthreads.exe prog.c uthread.c context_switch.s

clean:
	del uthreads.exe

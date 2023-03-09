#include <stdio.h>

#include "uthread.h"

uthread_t thrm_instance;

uthread_t * thrm;
uthread_t * thr1;
uthread_t * thr2;
uthread_t * thr3;

void thread_function1();
void thread_function2();
void thread_function3();

void function1_2() {
	puts("[T1] step 1.2");
	
	context_switch(thr1, thr2);
}

void function1_1() {
	puts("[T1] step 1.1");
	function1_2();
	context_switch(thr1, thrm);
}

void thread_function1() {
	puts("[T1] :: starting ::");
	
	puts("[T1] step 1");
	
	thr3 = ut_create(thread_function3);
	
	context_switch(thr1, thr2);
	
	puts("[T1] step 2");

	function1_1();
	
	context_switch(thr1, thr2);
}

void thread_function2() {
	puts("[T2] :: starting ::");
	
	puts("[T2] step 1");
	
	context_switch(thr2, thr3);
	
	puts("[T2] step 2");
	
	context_switch(thr2, thr1);
}

void thread_function3() {
	puts("[T3] :: starting ::");
	
	puts("[T3] step 1");
	
	context_switch(thr3, thr1);
}

int main() {
	thrm = &thrm_instance;

	puts("[TM] :: starting ::");
	
	thr1 = ut_create(thread_function1);
	thr2 = ut_create(thread_function2);
	
	context_switch(thrm, thr1);
	
	puts("[TM] back to main");
	
	return 0;
}


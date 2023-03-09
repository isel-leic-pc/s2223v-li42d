#include <stdio.h>

#include "uthread.h"

uthread_t * thr1;
uthread_t * thr2;
uthread_t * thr3;

void thread_function1();
void thread_function2();
void thread_function3();

void function1_2() {
	ut_yield();
	puts("[T1] step 1.2");
}

void function1_1() {
	puts("[T1] step 1.1");
	function1_2();
}

void thread_function1() {
	puts("[T1] :: starting ::");
	
	puts("[T1] step 1");
	
	thr3 = ut_create(thread_function3, NULL);
	
	function1_1();

	ut_yield();
	
	puts("[T1] step 2");
}

void thread_function2() {
	puts("[T2] :: starting ::");
	
	puts("[T2] step 1");
	
	ut_yield();
	
	puts("[T2] step 2");
}

void thread_function3() {
	puts("[T3] :: starting ::");
	
	ut_yield();
	
	puts("[T3] step 1");
}

int main() {
	ut_init();

	puts("[TM] :: starting ::");
	
	thr1 = ut_create(thread_function1, NULL);
	thr2 = ut_create(thread_function2, NULL);
	
	ut_run();
	
	puts("[TM] back to main");
	
	return 0;
}


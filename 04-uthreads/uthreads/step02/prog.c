#include <stdio.h>

#include "uthread.h"

uthread_t * thr1;
uthread_t * thr2;
uthread_t * thr3;

void thread_function1();
void thread_function2();
void thread_function3();

void function1_2() {
	puts("[T1] step 1.2");
	
	ut_switch_to(thr2);
}

void function1_1() {
	puts("[T1] step 1.1");
	function1_2();
}

void thread_function1() {
	puts("[T1] :: starting ::");
	
	puts("[T1] step 1");
	
	thr3 = ut_create(thread_function3, NULL);
	
	ut_switch_to(thr2);
	
	puts("[T1] step 2");

	function1_1();

	// ut_switch_to(thrm);  // automatic
}

void thread_function2() {
	puts("[T2] :: starting ::");
	
	puts("[T2] step 1");
	
	ut_switch_to(thr3);
	
	puts("[T2] step 2");
	
	ut_switch_to_and_free(thr1);
}

void thread_function3() {
	puts("[T3] :: starting ::");
	
	puts("[T3] step 1");
	
	ut_switch_to_and_free(thr1);
}

int main() {
	ut_init();

	puts("[TM] :: starting ::");
	
	thr1 = ut_create(thread_function1, NULL);
	thr2 = ut_create(thread_function2, NULL);
	
	ut_switch_to(thr1);
	
	puts("[TM] back to main");
	
	return 0;
}


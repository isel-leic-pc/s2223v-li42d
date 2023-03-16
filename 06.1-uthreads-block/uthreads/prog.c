#include <stdio.h>

#include "uthread.h"
#include "gate.h"

ut_gate_t the_gate;

uthread_t * thr1;
uthread_t * thr2;
uthread_t * thr3;
uthread_t * thr4;
uthread_t * thr5;

void thread_function1();
void thread_function2();
void thread_function3();
void thread_function4();
void thread_function5();

void thread_function1() {
	puts("[T1] :: starting ::");

	for (int i = 1; i <= 3; ++i) {
		printf("[T1] step1.%d\n", i);
		ut_yield();
	}

	thr3 = ut_create(thread_function3, NULL);
	thr4 = ut_create(thread_function4, NULL);

	for (int i = 1; i <= 12; ++i) {
		printf("[T1] step2.%d\n", i);
		ut_yield();
	}

	puts("[T1] :: ending ::");
}

void thread_function2() {
	puts("[T2] :: starting ::");

	for (int i = 1; i <= 4; ++i) {
		printf("[T2] step1.%d\n", i);
		ut_yield();
	}

	thr5 = ut_create(thread_function5, NULL);

	for (int i = 1; i <= 5; ++i) {
		printf("[T2] step2.%d\n", i);
		ut_yield();
	}

	puts("[T2]         :: GO ::");
	ut_gate_open(&the_gate);

	for (int i = 1; i <= 5; ++i) {
		printf("[T2]         step3.%d\n", i);
		ut_yield();
	}
	
	puts("[T2] :: ending ::");
}

void thread_function3() {
	puts("[T3] :: starting ::");
	
	for (int i = 1; i <= 3; ++i) {
		printf("[T3] step1.%d\n", i);
		ut_yield();
	}

	ut_gate_wait(&the_gate);

	for (int i = 1; i <= 2; ++i) {
		printf("[T3]                 step2.%d\n", i);
		ut_yield();
	}

	puts("[T3] :: ending ::");
}

void thread_function4() {
	puts("[T4] :: starting ::");
	
	puts("[T4] step1");
	
	//ut_yield();
	ut_gate_wait(&the_gate);

	for (int i = 1; i <= 5; ++i) {
		printf("[T4]                 step2.%d\n", i);
		ut_yield();
	}

	puts("[T4] :: ending ::");
}

void thread_function5() {
	puts("[T5] :: starting ::");
	
	puts("[T5] step1");

	//ut_yield();
	ut_gate_wait(&the_gate);

	for (int i = 1; i <= 4; ++i) {
		printf("[T5]                 step2.%d\n", i);
		ut_yield();
	}

	puts("[T5] :: ending ::");
}

int main() {
	ut_init();

	puts("[TM] :: starting ::");

	ut_gate_init(&the_gate);
	
	thr1 = ut_create(thread_function1, NULL);
	thr2 = ut_create(thread_function2, NULL);
	
	ut_run();
	
	puts("[TM] back to main");
	
	return 0;
}

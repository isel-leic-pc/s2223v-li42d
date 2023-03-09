#include "uthread.h"

#include <stdlib.h>

#define STACK_SIZE (8 * 1024)

void context_switch(
  uthread_t * curr_thread,  // rcx / rdi
  uthread_t * next_thread   // rdx / rsi
);

void context_switch_and_free(
  uthread_t * curr_thread,  // rcx / rdi
  uthread_t * next_thread   // rdx / rsi
);

uthread_t thread_main_instance;

uthread_t * thread_main;
uthread_t * thread_running;

list_entry_t ready_queue;

void ut_init() {
	thread_main = &thread_main_instance;
	thread_running = thread_main;
	list_init(&ready_queue);
}

void ut_switch_to(uthread_t * next_thread) {
	uthread_t * curr_thread = thread_running;
	thread_running = next_thread;
	context_switch(curr_thread, next_thread);
}

void schedule() {
	uthread_t * next_thread =
		list_is_empty(&ready_queue) ?
			thread_main :
			node_of(list_remove_head(&ready_queue), uthread_t, entry);
	
	ut_switch_to(next_thread);
}

void ut_switch_to_and_free(uthread_t * next_thread) {
	uthread_t * curr_thread = thread_running;
	thread_running = next_thread;
	context_switch_and_free(curr_thread, next_thread);
}

void schedule_and_free() {
	uthread_t * next_thread =
		list_is_empty(&ready_queue) ?
			thread_main :
			node_of(list_remove_head(&ready_queue), uthread_t, entry);
	
	ut_switch_to_and_free(next_thread);
}

void internal_start() {
	thread_running->start_routine(thread_running->arg);
	schedule_and_free();
}

uthread_t * ut_create(uthread_function_t start_routine, void * arg) {
	uthread_t * thread = (uthread_t *)malloc(STACK_SIZE);

	thread->start_routine = start_routine;
	thread->arg = arg;

	uint64_t * stack_end =
		(uint64_t *)(((uint8_t *)thread) + STACK_SIZE);

	*--stack_end = (uint64_t)internal_start;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	
	thread->rsp = (uint64_t)stack_end;
	
	list_add_tail(&ready_queue, &(thread->entry));
	
	return thread;
}

void ut_run() {
	schedule();
}

void ut_yield() {
	if (!list_is_empty(&ready_queue)) {
		list_add_tail(&ready_queue, &(thread_running->entry));
		schedule();
	}
}

#include "uthread.h"

#include <stdlib.h>

#define STACK_SIZE (8 * 1024)

uthread_t * ut_create(uthread_function_t start_routine) {
	uthread_t * thread = (uthread_t *)malloc(STACK_SIZE);

	uint64_t * stack_end =
		(uint64_t *)(((uint8_t *)thread) + STACK_SIZE);

	*--stack_end = (uint64_t)start_routine;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	*--stack_end = 0;
	
	thread->rsp = (uint64_t)stack_end;
	
	return thread;
}

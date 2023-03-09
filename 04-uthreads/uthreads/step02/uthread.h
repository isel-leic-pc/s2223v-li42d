#include <stdint.h>

typedef struct thread uthread_t;

typedef void (*uthread_function_t)(void * arg);

struct thread {
	uint64_t rsp; // must always be the first field
	uthread_function_t start_routine;
	void * arg;
};

void ut_init();

void ut_switch_to(uthread_t * next_thread);
void ut_switch_to_and_free(uthread_t * next_thread);

uthread_t * ut_create(
	uthread_function_t start_routine,
	void * arg
);

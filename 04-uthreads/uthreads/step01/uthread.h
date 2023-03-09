#include <stdint.h>

struct thread {
	uint64_t rsp; // must always be the first field
};

typedef struct thread uthread_t;

typedef void (*uthread_function_t)();

uthread_t * ut_create(uthread_function_t start_routine);

void context_switch(
  uthread_t * curr_thread,  // rcx / rdi
  uthread_t * next_thread   // rdx / rsi
);
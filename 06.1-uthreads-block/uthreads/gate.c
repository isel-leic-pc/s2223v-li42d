#include "gate.h"
#include "uthread.h"

extern uthread_t * thread_running;
extern list_entry_t ready_queue;
extern void schedule();

void ut_gate_init(ut_gate_t * gate) {
	gate->isOpen = false;
	list_init(&(gate->wait_set));
}

void ut_gate_wait(ut_gate_t * gate) {
	if (!gate->isOpen) {
		list_add_tail(&(gate->wait_set), &(thread_running->entry));
		schedule();
	}
}

void ut_gate_open(ut_gate_t * gate) {
	if (!gate->isOpen) {
		gate->isOpen = true;
		while (!list_is_empty(&(gate->wait_set))) {
			list_add_tail(
				&ready_queue,
				list_remove_head(&(gate->wait_set))
			);
		}
	}
}

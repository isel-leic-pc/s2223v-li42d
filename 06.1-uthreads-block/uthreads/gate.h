#ifndef GATE_H
#define GATE_H

#include <stdbool.h>
#include "list.h"

typedef struct gate {
	bool isOpen;
	list_entry_t wait_set;
} ut_gate_t;

void ut_gate_init(ut_gate_t * gate);

void ut_gate_wait(ut_gate_t * gate);
void ut_gate_open(ut_gate_t * gate);

#endif // GATE_H

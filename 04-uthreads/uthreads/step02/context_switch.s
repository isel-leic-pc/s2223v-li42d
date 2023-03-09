	.text
	
	.global context_switch
	.global context_switch_and_free

#
# void context_switch(
#   uthread_t * curr_thread,  // rcx / rdi
#   uthread_t * next_thread,  // rdx / rsi
# );
#
# Linux:	
#   - rdi -> curr_thread
#   - rsi -> next_thread
#
# Windows:	
#   - rcx -> curr_thread
#   - rdx -> next_thread
#
context_switch:
	pushq %rbp
	pushq %rbx
	pushq %rdi   # Windows calling convention
	pushq %rsi   # Windows calling convention
	pushq %r12
	pushq %r13
	pushq %r14
	pushq %r15
	
	movq %rsp, (%rcx)  # save stack pointer
	                   # in curr_thread->rsp
	movq (%rdx), %rsp  # restore stack pointer
	                   # from next_thread->rsp
	popq %r15
	popq %r14
	popq %r13
	popq %r12
	popq %rsi   # Windows calling convention
	popq %rdi   # Windows calling convention
	popq %rbx
	popq %rbp
	
	ret

#
# void context_switch_and_free(
#   uthread_t * curr_thread,  // rcx / rdi
#   uthread_t * next_thread,  // rdx / rsi
# );
#
# Linux:	
#   - rdi -> curr_thread
#   - rsi -> next_thread
#
# Windows:	
#   - rcx -> curr_thread
#   - rdx -> next_thread
#
context_switch_and_free:
	movq (%rdx), %rsp  # restore stack pointer
	                   # from next_thread->rsp

	call free          # free(curr_thread)

	popq %r15
	popq %r14
	popq %r13
	popq %r12
	popq %rsi   # Windows calling convention
	popq %rdi   # Windows calling convention
	popq %rbx
	popq %rbp
	
	ret

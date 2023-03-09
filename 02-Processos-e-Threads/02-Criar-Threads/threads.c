#include <windows.h>
#include <process.h>
#include <stdio.h>

unsigned int __stdcall Task1(void * arg) {
	printf("[T1] :: STARTING ::\n");
	for (int i = 0; i < 8; ++i) {
		Sleep(800);
		printf("[T1] :: >> %d << ::\n", i);
	}
	Sleep(800);
	printf("[T1] :: DONE ::\n");
	return 0;
}

unsigned int __stdcall Task2(void * arg) {
	printf("[T2] :: STARTING ::\n");
	for (int i = 0; i < 8; ++i) {
		Sleep(1200);
		printf("[T2] :: >> %d << ::\n", i);
	}
	Sleep(1200);
	printf("[T2] :: DONE ::\n");
	return 0;
}

int main() {
	printf("[MN] :: STARTING ::\n");
	
	HANDLE threads[2];
	threads[0] = (HANDLE)_beginthreadex(NULL, 0, Task1, NULL, 0, NULL);
	threads[1] = (HANDLE)_beginthreadex(NULL, 0, Task2, NULL, 0, NULL);
	
	printf("[MN] :: WAITING ::\n");
	WaitForMultipleObjects(2, threads, TRUE, INFINITE);

	CloseHandle(threads[0]);
	CloseHandle(threads[1]);

	printf("[MN] :: DONE ::\n");
	return 0;
}
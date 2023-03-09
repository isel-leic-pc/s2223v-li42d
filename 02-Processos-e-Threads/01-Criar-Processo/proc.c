#include <windows.h>
#include <stdio.h>

#define WINWORD_EXE "C:\\Program Files\\Microsoft Office\\root\\Office16\\WINWORD.EXE"

int main() {
	STARTUPINFO si;
	PROCESS_INFORMATION pi;

	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );

	printf(":: STARTING ::\n");

	BOOL res = CreateProcess(
		NULL,
		WINWORD_EXE,
		NULL,
		NULL,
		FALSE,
		0,
		NULL,
		NULL,
		&si,
		&pi
	);

	if (!res) {
		fprintf(stderr, "CreateProcess failed (%d).\n", GetLastError());
		return 1;
	}
	
	printf(":: NEW PROCESS RUNNING WITH PID %d ::\n", pi.dwProcessId);

	WaitForSingleObject(pi.hProcess, INFINITE);
	puts(":: FINISHED ::");
	
	CloseHandle(pi.hThread);
	CloseHandle(pi.hProcess);

	printf(":: DONE ::\n");
	return 0;
}

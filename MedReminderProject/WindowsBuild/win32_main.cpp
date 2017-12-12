#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <cstdio>

int WINAPI WinMain(HINSTANCE instance, HINSTANCE prev_instance, LPSTR cmd_line, int show_code) {
  STARTUPINFO si = {sizeof(STARTUPINFO)};
  PROCESS_INFORMATION pi = {0};
  if (!CreateProcess(0, "java -cp classes -enableassertions medreminderproject.MedReminderProject", 0, 0, false, 0, 0, 0, &si, &pi)) {
      printf("Error Code %i\n", GetLastError());
      return -1;
    }

  CloseHandle(pi.hProcess);
  CloseHandle(pi.hThread);
  return 0;
}

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>


int readline(int hDFich,char *msg);
int getProperties(char *filename,char* key,char *value,char* sep);
void writeLog(int iCli,char * msg);

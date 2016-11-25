#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h> 
#include <netinet/in.h>
#include <errno.h>
#include <unistd.h>
#include "LibSocket.h"


void MakeRequete(char* command,char** parameters,int NbrParam,char *request);
int getCommand(char *request);
int getParameters(char * request,char** parameters);


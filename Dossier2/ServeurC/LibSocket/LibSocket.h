#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h> 
#include <netinet/in.h>
#include <errno.h>
#include <unistd.h>
#include "../File/FileAccess.h"

//#define PORT 50000

int crea_socket();
int connect_server();
int connect_client();
int connect_ser_app();
int wait_connect(int ListenSocket,struct sockaddr_in* SocketAdressClient);
int send_msg(int Socket,char* msg);
int receiveSep(int Socket,char *chaine,char *sep);
char marqueurRecu (char *m, int nc,char *sep);

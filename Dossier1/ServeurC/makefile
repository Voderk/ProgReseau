.SILENT:

LIBSOCKET = LibSocket
FILE = File
DMMP = DMMP

CC = gcc -I$(LIBSOCKET) -I$(FILE) -I$(DMMP)

all:	Serveur Client

Serveur:	Serveur.c LibSocket.o FileAccess.o DMMP.o AccessSerAp.o
			echo Creation Serveur
			$(CC) -pthread LibSocket.o FileAccess.o DMMP.o AccessSerAp.o -o Serveur Serveur.c

Client:	Client.c LibSocket.o FileAccess.o DMMP.o AccessSerAp.o
		echo Creation Client
		$(CC) LibSocket.o FileAccess.o DMMP.o AccessSerAp.o -o Client Client.c

LibSocket.o:	$(LIBSOCKET)/LibSocket.h $(LIBSOCKET)/LibSocket.c
				echo Creation de librairie des sockets
				$(CC) -c $(LIBSOCKET)/LibSocket.c

FileAccess.o:	$(FILE)/FileAccess.h $(FILE)/FileAccess.c
				echo Creation FileAcess
				$(CC) -c $(FILE)/FileAccess.c

DMMP.o:	$(DMMP)/DMMP.h $(DMMP)/DMMP.c
		echo Creation de la librairie DMMP
		$(CC) -c $(DMMP)/DMMP.c

AccessSerAp.o:	$(FILE)/AccessSerAp.h $(FILE)/AccessSerAp.c
				echo Creation AccessSerAp
				$(CC) -c $(FILE)/AccessSerAp.c
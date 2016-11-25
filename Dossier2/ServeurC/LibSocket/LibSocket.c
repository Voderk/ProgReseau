#include "LibSocket.h"

/*************************************************************************
** I: /                                                                 **
** P: Création d'une socket  								            **
** O: Handle de la Socket 												**
*************************************************************************/
int crea_socket()
{
	int Socket;


	Socket = socket(AF_INET,SOCK_STREAM,0); //Création de la socket
	if(Socket == -1)
	{
		printf("Error during the creation of the socket: %d\n",errno);
		exit(1);
	}
	
	return Socket;
}


/*************************************************************************
** I: /                                                                 **
** P: Connexion du serveur grâce au socket 								**
** O: Handle de la Socket 												**
*************************************************************************/
int connect_server()
{
	int Socket,PORT;
	struct hostent *infoHost; //Information sur l'host
	struct in_addr IPAdress;
	struct sockaddr_in SocketAdress;
	char temp[20];

	Socket = crea_socket();

	//Récupération du port sur le fichier config
	getProperties("Serv_mouvements.conf","Port_Service",temp,"=");
	PORT = atoi(temp);

	//Récupération du nom de la machine sur le fichier
	getProperties("Serv_mouvements.conf","Machine_Name",temp,"=");
	
	//Récupération des informations de la machine
	if((infoHost = gethostbyname(temp)) == 0)
	{
		//Récupération des infos sur le serveur
		printf("Error when asking information from the host: %d\n",errno);
		return -1;
	}

	memcpy(&IPAdress,infoHost->h_addr,infoHost->h_length);

	//Préparation du socket
	memset(&SocketAdress,0,sizeof(struct sockaddr_in));
	SocketAdress.sin_family = AF_INET;  //Domaine
	SocketAdress.sin_port = htons(PORT);
	memcpy(&SocketAdress.sin_addr, infoHost->h_addr, infoHost->h_length);

	//Liaison avec le socket
	if(bind(Socket,(struct sockaddr *)&SocketAdress,sizeof(struct sockaddr_in))==-1)
	{
		printf("Error during the bind %d\n",errno);
		return -1;
	}
	
 	return Socket;
}

/*************************************************************************
** I: /                                                                 **
** P: Connexion du client grâce au socket 								**
** O: Handle de la Socket 												**
*************************************************************************/
int connect_client()
{
	int Socket,PORT; //handle du socket
	struct hostent *infoHost; //Information sur l'host
	struct in_addr IPAdress;
	struct sockaddr_in SocketAdress;
	char temp[20];
	int ret;
	Socket = crea_socket();

	//Récupération du port sur le fichier config
	getProperties("Serv_mouvements.conf","Port_Service",temp,"=");
	PORT = atoi(temp);

	if((infoHost = gethostbyname("localhost")) == 0)
	{
		//Récupération des infos sur le serveur
		printf("Error when asking information from the host: %d\n",errno);
		return -1;
	}

	memcpy(&IPAdress,infoHost->h_addr,infoHost->h_length);

	//Préparation du socket
	memset(&SocketAdress,0,sizeof(struct sockaddr_in));
	SocketAdress.sin_family = AF_INET;  //Domaine
	SocketAdress.sin_port = htons(PORT);
	memcpy(&SocketAdress.sin_addr, infoHost->h_addr, infoHost->h_length);


	//Tentative de connexion
	if((ret = connect (Socket,(struct sockaddr *)&SocketAdress,sizeof(struct sockaddr_in))) == -1)
	{
		printf("Error: Connection to the server impossible: %d\n",errno);
		close(Socket);
		exit(1);	
	}
	return Socket;
}


/*************************************************************************
** I: /                                                                 **
** P: Connexion du client au serveur Appareil 							**
** O: Handle de la Socket 												**
*************************************************************************/
int connect_ser_app()
{
	int Socket,PORT; //handle du socket
	struct hostent *infoHost; //Information sur l'host
	struct in_addr IPAdress;
	struct sockaddr_in SocketAdress;
	char temp[20];
	int ret;
	Socket = crea_socket();

	//Récupération du port sur le fichier config
	getProperties("Serv_mouvements.conf","Port_Serv_App",temp,"=");
	PORT = atoi(temp);

	getProperties("Serv_mouvements.conf","Serveur_App",temp,"=");

	if((infoHost = gethostbyname(temp)) == 0)
	{
		//Récupération des infos sur le serveur
		printf("Error when asking information from the host: %d\n",errno);
		return -1;
	}

	memcpy(&IPAdress,infoHost->h_addr,infoHost->h_length);

	//Préparation du socket
	memset(&SocketAdress,0,sizeof(struct sockaddr_in));
	SocketAdress.sin_family = AF_INET;  //Domaine
	SocketAdress.sin_port = htons(PORT);
	memcpy(&SocketAdress.sin_addr, infoHost->h_addr, infoHost->h_length);


	//Tentative de connexion
	if((ret = connect (Socket,(struct sockaddr *)&SocketAdress,sizeof(struct sockaddr_in))) == -1)
	{
		printf("Error: Connection to the server impossible: %d\n",errno);
		close(Socket);
		exit(1);	
	}

	return Socket;
}




/*************************************************************************
** I: Socket d'écoute                                                   **
** P: Connexion du client au serveur     								**
** O: Handle de la Socket, 												**
*************************************************************************/
int wait_connect(int ListenSocket,struct sockaddr_in*SocketAdressClient)
{

	//Mise en écoute du serveur
	if(listen(ListenSocket,SOMAXCONN) == -1) // Mise en écoute du serveur
	 {
	 	printf("Error ! Impossible to listen on the socket: %d\n", errno);
	 	close(ListenSocket);
	 	exit(1);
	 }

	int tailleSockAddr_in,ServiceSocket;
	tailleSockAddr_in = sizeof(struct sockaddr_in);
	//Aceptation d'une connexion
 	//Faire une boucle pour que le serveur écoute constamment pour de nouvelles connexion
	if((ServiceSocket = accept(ListenSocket,(struct sockaddr *)SocketAdressClient,&tailleSockAddr_in)) == -1)
	{
		printf("Erreur on the accept: %d\n",errno);
		close(ListenSocket);
		exit(1);
	}
	return ServiceSocket;
}

/*************************************************************************
** I: Socket, Message                                                   **
** P: Envoie d'un message sur le réseau  								**
** O: /					 												**
*************************************************************************/
int send_msg(int Socket,char* msg)
{
	int length;

	length = strlen(msg);
	//Indicateur de fin de message
	*(msg + length) = '\r';
	*(msg + length+1) = '\n';

	//Envoi d'un message
	if (send(Socket, msg, strlen(msg)+1, 0) == -1)
	{
		printf("Erreur sur le send de la socket %d\n", errno);
		close(Socket); /* Fermeture de la socket */
		exit(1);
	}

	return 1;
}

/*************************************************************************
** I: Socket, Buffer, Séparateur                                        **
** P: Réception d'un Message 			 								**
** O: Message reçu		 												**
*************************************************************************/
int receiveSep(int Socket,char *msgClient,char *sep)
{
	int nbreBytesRecus,finDetectee = 0,tailleMsgRecu = 0;
	char chaine[200];
	do
	{
		//Reception d'une partie du message
		if ((nbreBytesRecus = recv(Socket,chaine, 200, 0)) == -1)
		{
			printf("Erreur sur le recv de la socket %d\n", errno);
			close(Socket);
			exit(1);
		}
		else
		{
			//Vérification si on a atteint la fin du message
			finDetectee = marqueurRecu (chaine, nbreBytesRecus,sep);
			memcpy((char *)msgClient + tailleMsgRecu, chaine,nbreBytesRecus);
			tailleMsgRecu += nbreBytesRecus;
		}
	}while (nbreBytesRecus != 0 && nbreBytesRecus != -1 && !finDetectee);

	return 1;
}

/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
char marqueurRecu (char *m, int nc,char *sep)
{
	static char demiTrouve=0;
	int i;
	char trouve=0;
	//A la dernière vérifaction on a trouvé un premier sep mais pas un deuxième
	if (demiTrouve==1 && m[0]==*(sep+1)) 
		return 1;
	else 
		demiTrouve=0;
	
	for (i=0; i<nc-1 && !trouve; i++)
	
	if (m[i]==*(sep) && m[i+1]==*(sep+1)) 
		trouve=1;
	if (trouve) 
		return 1;
	else
	{ 
		if (m[nc]==*(sep))
		{
			demiTrouve=1;
			return 0;
		}
		else 
			return 0;
	}
}
#include <stdio.h>
#include <stlib.h>
#include <sys/socket.h>
#include <sys/type.h>
#include <netdb.h> //p-e à suppimer ?

#define PORT 50000

int main()
{
	int ListenSocket, ServiceSocket; //handle du socket
	struct hostent *infoHost; //Information sur l'host
	struct in_adrr IPAdress;
	struct sockaddr_in SocketAdress,SocketAdressClient;

	ListenSocket = socket(AF_INET,SOCK_STREAM,0); //Création de la socket
	if(ListenSocket == -1)
	{
		printf("Erreur de la création du socket: %d\n",errno);
		exit(1);
	}
	printf("Socket créé\n");

	if((infoHost = gethostbyname(localhost)) == 0) //localhost pou nom de la machine ?
	{
		//Récupération des infos sur le serveur
				printf("Erreur d'acquisition d'inforation sur le host: %d\n",errno);
				exit(1);
	}

	printf("Infmorations acquises\n");

	memcopy(&IPAdress,infoHost->h_addr,infoHost->h_length);

	//Préparation du socket
	memset(&SocketAdress,0,sizeof(sockaddr_in));
	SocketAdress.sin_family = AF_INET;  //Domaine
	SocketAdress.sin_port = htons(PORT);
	memcpy(&SocketAdress.sin_addr, infoHost->h_adrre, infoHost->h_length);

	//Liaison avec le socket
	if(bind(ListenSocket,(struct sockaddr *)&SocketAdress,sizeof(struct sockaddr_in))==-1)
	{
		printf("Erreur sur le bind de la socket %d\n",errno);
		exit(1);
	}
	 printf("Adresse bind et poert socket ok\n");

	 if(listen(ListenSocket,SOMAXCONN) == -1) // Mise en écoute du serveur
	 {
	 	printf("Erreur ! Impossible de listen sur le socket: %d\n", errno);
	 	close(ListenSocket);
	 	exit(1);
	 }

 	printf("Listen socket OK\n");

 	//Aceptation d'une connexion
 	//Faire une boucle pour que le serveur écoute constamment pour de nouvelles connexion
	 if((ServiceSocket = accept(ListenSocket,&SocketAdressClient,sizeof(struct sockaddr_in))) == -1)
	{
		printf("Erreur sur l'accept du socket: %d\n",errno);
		close(ListenSocket);
		exit(1);
	}

	//Lancer un thread avec l'handle ServiceSocket pour qu le serveur puisse faire autre chose
	printf("Connexion établie\n");
	//

	close(ServiceSocket);
	printf("Socket connected to the client closed");
	close(ListenSocket);
	printf("Server Socket closed\n");
	 return 0;
}
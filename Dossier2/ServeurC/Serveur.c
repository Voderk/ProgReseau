#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h> //p-e à suppimer ?
#include <netinet/in.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <ctype.h>
#include <signal.h>
#include "LibSocket.h"
#include "FileAccess.h"
#include "AccessSerAp.h"
#include "DMMP.h"

#define PORT 50000
#define CONNECT 1
#define INPUT_DEVICES1 2
#define INPUT_DEVICES2 3
#define GET_DELIVERY1 4
#define GET_DELIVERY2 5
#define GET_DELIVERY_END 6
#define DECONNECT 7
#define EOC 8
#define DEFAULT_STATE 9

int state = DEFAULT_STATE;

//Déclaration ThreadClient
void * ThreadClient(void *);

//Signal
void handlerFin(int signal);

//Déclaration Mutex
pthread_mutex_t mutexNbrClients;
pthread_mutex_t mutexFile;
pthread_mutex_t mutexLog;
pthread_cond_t condNbrClients;
int NbClient = -1;
int* ServiceSocket,SerAppSocket;

int main()
{
	pthread_t threadHandle[5];

	char value[20];
	char message[1050];

	//Initialisation des mutex
	pthread_mutex_init(&mutexNbrClients,NULL);
	pthread_mutex_init(&mutexFile,NULL);
	pthread_mutex_init(&mutexLog,NULL);
	pthread_cond_init(&condNbrClients,NULL);

	int ListenSocket,ClientSocket,i,j; //handle du socket
	struct hostent *infoHost; //Information sur l'host
	struct in_addr IPAdress;
	struct sockaddr_in SocketAdress,SocketAdressClient;
	unsigned int tailleSockAddr_in;	

	int nbreRecv,MAX_THREADS;

	struct sigaction sigAct;
	sigAct.sa_handler = handlerFin;
	sigemptyset(&(sigAct.sa_mask));
	sigaction(SIGINT, &sigAct, NULL );



	ServiceSocket = malloc(MAX_THREADS*sizeof(int));

	SerAppSocket = connect_ser_app();
	printf("Connexion au servur appareil réussi !");



	getProperties("Serv_mouvements.conf","MAX_THREADS",value,"=");
	MAX_THREADS = atoi(value);

	for(j = 0;j<MAX_THREADS;j++)
	{
		*(ServiceSocket+j) = -1;
	}

	for (i=0; i<MAX_THREADS; i++)
	{
		pthread_create(&threadHandle[i],NULL,(void *(*)(void *))ThreadClient,NULL);
		pthread_detach(threadHandle[i]);
	}

	writeLog(1000,"Lancement du serveur");

	ListenSocket = connect_server();

	while(1)
	{	
		//sleep(5);

		ClientSocket = wait_connect(ListenSocket,&SocketAdressClient);
		//Lancer un thread avec l'handle ServiceSocket pour que le serveur puisse faire autre chose
		pthread_mutex_lock(&mutexLog);
		writeLog(1000,"En attente d'un client");
		pthread_mutex_unlock(&mutexLog);
		//printf("\nRecherche d'une connexion libre...");
		for(j = 0;j<MAX_THREADS && *(ServiceSocket+j) != -1;j++);

		if(j == MAX_THREADS)
		{	

			printf("Plus de connexion disponible\n");
			strcpy(message,"DOC");
			if (send(ClientSocket, message, strlen(message)+1, 0) == -1)
			{
				printf("Erreur sur le send de la socket %d\n", errno);
				close(ClientSocket); /* Fermeture de la socket */
				exit(1);
			}
			close(ClientSocket);
		}
		else
		{	
			pthread_mutex_lock(&mutexNbrClients);
			*(ServiceSocket+j) = ClientSocket;
			NbClient = j;
			pthread_mutex_unlock(&mutexNbrClients);
			pthread_cond_signal(&condNbrClients);
		}
	}

	close(ListenSocket);
	return 0;
}


void * ThreadClient(void * i)
{
	int SocketClient,command,j=0,NbrParameter,ret,iCliTraite,deconnect = 0,findfournisseur,cmp;
	char msgServeur[1600],requete[1600],value[50],msgClient[1600],zone[100],msgServeurApp[1600];
	char * parameters[10];
	struct Article tempArticle;
	int x,y,FindZone;

	memset(msgServeurApp,0,1050);

	for(j = 0;j<10;j++)
	{
		parameters[j] = (char *)malloc(100*sizeof(char)); 
	}
	while(1)
	{
		pthread_mutex_lock(&mutexNbrClients);
		while (NbClient == -1)
			pthread_cond_wait(&condNbrClients, &mutexNbrClients);
		iCliTraite = NbClient;
		NbClient = -1;
		SocketClient =  *(ServiceSocket + iCliTraite);
		pthread_mutex_unlock(&mutexNbrClients);

		strcpy(msgServeur,"OK");
		send_msg(SocketClient,msgServeur);

		pthread_mutex_lock(&mutexLog);
		writeLog(iCliTraite,"Connexion avec le client établi");
		pthread_mutex_unlock(&mutexLog);

		do
		{
			receiveSep(SocketClient,msgServeur,"\r\n");

			strcpy(requete,msgServeur);

			printf("\nMessage : %s\n",msgServeur);

			//Récupération de la commande
			command = getCommand(requete);

			NbrParameter = getParameters(msgServeur,parameters);

			switch(command)
			{

				case CONNECT:
					state = DEFAULT_STATE;
					send_msg(SerAppSocket,msgServeur);
					receiveSep(SerAppSocket,msgServeurApp,"\r\n");
					printf("\nMessage reçu: %s",msgServeurApp);
					fflush(stdout);
					
					cmp = strncmp(msgServeurApp,"ERRORBD",7);
					if(cmp == 0)
					{
						strcpy(parameters[0],"ERRORBD");
					}
					else
					{
						cmp = strncmp(msgServeurApp,"NOKLOGIN",8);
						
						//ret = getProperties("login.csv",*(parameters),value,";");
						
						if(cmp == 0)
						{
							strcpy(parameters[0],"NO");
							strcpy(parameters[1],"Login introuvable");
							NbrParameter = 2;	
						}
						else
						{
							printf("\nMot de passe : %s",value);
							ret = strncmp(msgServeurApp,"NOKPASSWORD",11);
							if(ret == 0)
							{
								strcpy(parameters[0],"NO");
								strcpy(parameters[1],"Mot de passe incorrect");
								NbrParameter = 2;
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Connexion invalide..");
								pthread_mutex_unlock(&mutexLog);
							}
							else
							{
								strcpy(parameters[0],"YES");
								NbrParameter = 1;
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Connexion réussi !!");
								pthread_mutex_unlock(&mutexLog);

							}
						}
					}
					MakeRequete("CONNECT",parameters,NbrParameter,msgClient);
					send_msg(SocketClient,msgClient);	
				break;


				case INPUT_DEVICES1:
					state = INPUT_DEVICES1;
					//pthread_mutex_lock(&mutexFile);
					//findfournisseur = getFournisseur((*parameters));
					if(state == INPUT_DEVICES1)
					{
						send_msg(SerAppSocket,msgServeur);
						receiveSep(SerAppSocket,msgServeurApp,"\r\n");
						cmp = strncmp(msgServeurApp,"ERRORBD",7);
						if(cmp == 0)
						{
							fflush(stdout);
							strcpy(parameters[0],"ERRORBD");
						}
						else
						{
							cmp = strncmp(msgServeurApp,"OK",2);

							if(cmp == 0)
							{
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Fournisseur trouvé !!");
								pthread_mutex_unlock(&mutexLog);
								strcpy(parameters[0],"YES");
								NbrParameter = 1;
							}
							else
							{
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Erreur : Fournisseur introuvable !!");
								pthread_mutex_unlock(&mutexLog);

								strcpy(parameters[0],"NO");
								strcpy(parameters[1],"Fournisseur introuvable");
								NbrParameter = 2;
							}
						}
						MakeRequete("INPUT_DEVICES1",parameters,NbrParameter,msgClient);
						send_msg(SocketClient,msgClient);
					}
					else
					{
						printf("\nBad Server state");
						state = DEFAULT_STATE;
						send_msg(SocketClient, "SERVER STATE ERROR");
					}
				break;


				case INPUT_DEVICES2:
					printf("\nMessage reçu :%s",msgServeur);
					//fflush(stdout);
					send_msg(SerAppSocket,msgServeur);
					receiveSep(SerAppSocket,msgServeurApp,"\r\n");
					printf("Message Serveur : %s",msgServeurApp);

					/*for(j = 0;j<NbrParameter;j++)
					{
						InitArticle(parameters[j],&tempArticle);
						FindZone = FindDispoZone(&x,&y);
						printf("\nINPUT_DEVICE: %d ==> %d,%d",FindZone,x,y);
						fflush(stdout);
						if(FindZone == 1)
						{
							pthread_mutex_lock(&mutexLog);
							writeLog(iCliTraite,"Ecriture dans le fichier Entrepot !!");
							pthread_mutex_unlock(&mutexLog);
							InitZone(&tempArticle);
							WriteArticle(x,y,&tempArticle);
							sprintf(parameters[j],"%d-%d",x,y);
							printf("\nParameter : %s",parameters[j]);
						}
						else
							strcpy(parameters[j],"Plus d'espace disponible");
					}
					MakeRequete("INPUT_DEVICES2",parameters,NbrParameter,msgClient);*/
					send_msg(SocketClient,msgServeurApp);				

				break;
				case GET_DELIVERY1:
					state = GET_DELIVERY1;
					send_msg(SerAppSocket,msgServeur);
					receiveSep(SerAppSocket,msgServeurApp,"\r\n");
					/*pthread_mutex_lock(&mutexFile);
					FindZone = 0;
					strcpy(zone,parameters[1]);
					FindZone = existDelivery(zone);
					if(FindZone == 1)
					{
						if(getId(zone, &tempArticle) != -1)
						{
							pthread_mutex_lock(&mutexLog);
							writeLog(iCliTraite,"Début de livraison!!");
							pthread_mutex_unlock(&mutexLog);
							printf("\nGET_DELIVERY1: %d/%s/%s/%d",tempArticle.NumSerie,tempArticle.nom,tempArticle.zone,tempArticle.Quantite);
							fflush(stdout);
							strcpy(parameters[0],"YES");
							sprintf(parameters[1],"%d",tempArticle.NumSerie);
							strcpy(parameters[2],tempArticle.nom);
							sprintf(parameters[3],"%d",tempArticle.Quantite);
							MakeRequete("GET_DELIVERY1",parameters,4,msgClient);
						}
						
					}
					else
					{
						strcpy(parameters[0],"NO");
						MakeRequete("GET_DELIVERY1",parameters,1,msgClient);
					}*/
					send_msg(SocketClient,msgServeurApp);

				break;

				case GET_DELIVERY2:
					if(state == GET_DELIVERY1 || state == GET_DELIVERY2)
					{
						send_msg(SerAppSocket,msgServeur);
						receiveSep(SerAppSocket,msgServeurApp,"\r\n");


					/*FindZone = 0;
					FindZone = existDelivery(zone);
					if(FindZone == 1)
					{
						if(getId(zone, &tempArticle) != -1)
						{
							pthread_mutex_lock(&mutexLog);
							writeLog(iCliTraite,"Livraison en cours!!");
							pthread_mutex_unlock(&mutexLog);
							printf("\nGET_DELIVERY2: %d/%s/%s/%d",tempArticle.NumSerie,tempArticle.nom,tempArticle.zone,tempArticle.Quantite);
							fflush(stdout);
							strcpy(parameters[0],"YES");
							sprintf(parameters[1],"%d",tempArticle.NumSerie);
							strcpy(parameters[2],tempArticle.nom);
							sprintf(parameters[3],"%d",tempArticle.Quantite);
							MakeRequete("GET_DELIVERY2",parameters,4,msgClient);
						}
						
					}
					else
					{
						strcpy(parameters[0],"NO");
						MakeRequete("GET_DELIVERY2",parameters,1,msgClient);
						pthread_mutex_unlock(&mutexFile);
					}*/
						send_msg(SocketClient,msgServeurApp);
					}
					else
					{
						printf("\nBad Server state");
						state = DEFAULT_STATE;
						send_msg(SocketClient, "SERVER STATE ERROR");
					}


				break;

				case GET_DELIVERY_END:
					if(state == GET_DELIVERY1 || state == GET_DELIVERY2)
					{
						send_msg(SerAppSocket,msgServeur);
						receiveSep(SerAppSocket,msgServeurApp,"\r\n");

						pthread_mutex_lock(&mutexLog);
						writeLog(iCliTraite,"Fin de livraison!!");
						pthread_mutex_unlock(&mutexLog);

						printf("\nGET_DELIVERY_END");
						fflush(stdout);


						strcpy(parameters[0],"OK");
						MakeRequete("GET_DELIVERY_END",parameters,1,msgClient);
						send_msg(SocketClient,msgClient);
					}
					else
					{
						printf("\nBad Server state");
						state = DEFAULT_STATE;
						send_msg(SocketClient, "SERVER STATE ERROR");
					}

				break;

				case DECONNECT:
					state = DEFAULT_STATE;
					pthread_mutex_lock(&mutexLog);
					writeLog(iCliTraite,"Fin de session!!");
					pthread_mutex_unlock(&mutexLog);



					send_msg(SerAppSocket,msgServeur);
					receiveSep(SerAppSocket,msgServeurApp,"\r\n");


					cmp = strncmp(msgServeurApp,"ERRORBD",7);
					if(cmp == 0)
					{
						strcpy(parameters[0],"ERRORBD");
					}
					else
					{

						cmp = strncmp(msgServeurApp,"NOKLOGIN",8);
						
						//ret = getProperties("login.csv",*(parameters),value,";");
					
						if(cmp == 0)
						{
							strcpy(parameters[0],"NO");
							strcpy(parameters[1],"Login introuvable");
							NbrParameter = 2;	
						}
						else
						{
							printf("\nMot de passe : %s",value);
							ret = strncmp(msgServeurApp,"NOKPASSWORD",11);
							if(ret == 0)
							{
								strcpy(parameters[0],"NO");
								strcpy(parameters[1],"Mot de passe incorrect");
								NbrParameter = 2;
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Connexion invalide..");
								pthread_mutex_unlock(&mutexLog);
							}
							else
							{
								strcpy(parameters[0],"YES");
								NbrParameter = 1;
								pthread_mutex_lock(&mutexLog);
								writeLog(iCliTraite,"Connexion réussi !!");
								pthread_mutex_unlock(&mutexLog);

							}
						}
					}
					MakeRequete("DECONNECT",parameters,NbrParameter,msgClient);
					send_msg(SocketClient,msgClient);
					deconnect = 1;
				break;
				case EOC:
					deconnect = 1;

			}

		}while(deconnect == 0);
		pthread_mutex_lock(&mutexNbrClients);
		*(ServiceSocket + iCliTraite) = -1;
		pthread_mutex_unlock(&mutexNbrClients);
		printf("\nSocket connected to the client closed\n");
		close(SocketClient);

	}
		
}

void handlerFin(int s)
{
	char temp[10];
	strncpy(temp,"EOC:Fin#",8);
	send_msg(SerAppSocket,temp);
	close(SerAppSocket);
	exit(0);
}
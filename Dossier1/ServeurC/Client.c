#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h> //p-e à suppimer ?
#include <netinet/in.h>
#include <errno.h>
#include <unistd.h>
#include <ctype.h>
#include "LibSocket.h"
#include "FileAccess.h"
#include "AccessSerAp.h"
#include "DMMP.h"

void EncodeNom(char *pvec,int LIM);
int EncodeEntier(int min,int max);


#define CONNECT 1
#define INPUT_DEVICES1 2
#define INPUT_DEVICES2 3
#define GET_DELIVERY1 4
#define GET_DELIVERY2 5
#define GET_DELIVERY_END 6
#define DECONNECT 7

int main()
{
	int ClientSocket,test,nbrAppareil,deliveryContinue; //handle du socket
	struct hostent *infoHost; //Information sur l'host
	struct in_addr IPAdress;
	struct sockaddr_in SocketAdress;
	int i,choix,j,NbrParameter,cmp,command;
	char msgServeur[1000],login[10],password[10],requete[1000],c;
	char fournisseur[50],NumVehicule[100],Zone[100];
	char temp[100],Encode[2];
	char * parameters[10];
	char * article[3];

	int connected = 0,waiting =0,deconnected = 0;


	for(j = 0;j<3;j++)
	{
		article[j] = (char *)malloc(30*sizeof(char)); 
	}



	ClientSocket = connect_client();

	receiveSep(ClientSocket,msgServeur,"\r\n");
	
	cmp = strcmp(msgServeur,"DOC");
	printf("%s\n\n",msgServeur);
	if(cmp == 0)
	{
		printf("\nConnexion au serveur impossible");
		printf("\nAppuyer sur ""Enter"" pour continuer..");
		while ( getchar() != '\n' );
		close(ClientSocket);
		exit(1);
	}

	for(j = 0;j<10;j++)
	{
		parameters[j] = (char *)malloc(100*sizeof(char)); 
	}

	do
	{
		printf("Bienvenue à la société InCheapDev\n");
		printf("\n1.Connexion");
		printf("\n2.Quitter");
		printf("\nFaîtes votre choix: ");
		choix = EncodeEntier(1,2);
		waiting = 1;

		system("clear");
		switch(choix)
		{
			case CONNECT:
			
			printf("Connexion au serveur");
			printf("\nVeullez rentrer votre login: ");
			EncodeNom(login,10);
			printf("\nVeuillez rentre votre mot de passe: ");
			EncodeNom(password,10);
			parameters[0] = login;
			parameters[1] = password;

			MakeRequete("CONNECT",parameters,2,msgServeur);
			send_msg(ClientSocket,msgServeur);

			//printf("Requête: %s",msgServeur);

			receiveSep(ClientSocket,msgServeur,"\r\n");

			strcpy(requete,msgServeur);

			command = getCommand(requete);
			NbrParameter = getParameters(msgServeur,parameters);
			system("clear");

			if(command != CONNECT)
			{
				printf("\nError ! Wrong response received");
			}
			else
			{
				cmp = strcmp(parameters[0],"YES");
				if(cmp == 0)
				{
					printf("\nConnexion établie ! Vous êtes maintenant connecté au serveur !");					
					connected = 1;
					waiting = 0;
				}
				else
				{
					printf("\nConnexion échouée !");
					printf("\nRaison: %s",parameters[1]);

				}
			}
			break;

			case 2:
				printf("\nAu revoir !\n");
				printf("\nAppuyer sur ""Enter"" pour continuer..");
				while ( getchar() != '\n' );
				waiting = 0;
				exit(0);
			break;
		}
		printf("\nAppuyer sur ""Enter"" pour continuer..");
		while ( getchar() != '\n' );
	}while (waiting == 1 && connected == 0);

	if(waiting == 0 && connected ==1)
	{
		do
		{
			system("clear");
			printf("Bienvenue sur le serveur de la société InCheapDev !!\n");
			printf("\nQuelle est la raison de votre visite ?");
			printf("\n1.Livraison");
			printf("\n2.Delivrement");
			printf("\n3.Deconnexion");
			printf("\nFaîtes votre choix: ");
			choix = EncodeEntier(1,3);

			system("clear");
			switch(choix)
			{
				case 1:
					printf("\nNom du fournisseur: ");
					EncodeNom(fournisseur,50);
					parameters[0] = fournisseur;
					MakeRequete("INPUT_DEVICES1",parameters,1,msgServeur);
					send_msg(ClientSocket,msgServeur);

					receiveSep(ClientSocket,msgServeur,"\r\n");

					strcpy(requete,msgServeur);

					command = getCommand(requete);
					printf("\nCommande : %d", command);
					NbrParameter = getParameters(msgServeur,parameters);

					if(command != INPUT_DEVICES1)
					{
						printf("\nError ! Wrong response received");
					}
					else
					{
						cmp = strcmp(parameters[0],"YES");
						if(cmp == 0)
						{
							printf("\nCombien d'appareil(s) apportez-vous ?");
							printf("\nNombre : ");
							nbrAppareil = EncodeEntier(1,10);

							for(i = 0;i<nbrAppareil;i++)
							{
								system("clear");
								printf("Article n°%d",i+1);
								printf("\nVeuillez entre le numéro de série de l'article: ");
								EncodeNom(article[0],30);

								printf("\nVeuillez rentrer le nom de l'article: ");
								EncodeNom(article[1],30);

								printf("\nnVeuillez rentre la nombre d'articles livrés: ");
								EncodeNom(article[2],30);

								MakeArticle(article, temp);
								strcpy(parameters[i],temp);
							}
							MakeRequete("INPUT_DEVICES2",parameters,nbrAppareil,msgServeur);
							send_msg(ClientSocket,msgServeur);

							receiveSep(ClientSocket,msgServeur,"\r\n");

							strcpy(requete,msgServeur);

							command = getCommand(requete);
							printf("\nCommande : %d", command);
							NbrParameter = getParameters(msgServeur,parameters);

							if(command != INPUT_DEVICES2)
							{
								printf("\nError ! Wrong response received");
							}
							else
							{
								for(i = 0;i<NbrParameter;i++)
								{
									printf("\nArticle n°%d à l'emplacement : %s",i+1,parameters[i]);
								}
								printf("\nAppuyer sur ""Enter"" pour continuer..");
								while ( getchar() != '\n' );
							}
						}
						else
						{
							printf("\nLivraison refusée !");
							printf("\nRaison: %s",parameters[1]);

						}
					}
					break;
				case 2:
					system("clear");
					deliveryContinue = 0;
					printf("Délivrement : ");
					printf("\nVeuillez rentrer le numéro d'entrpise du véhicule: ");
					EncodeNom(NumVehicule,50);
					printf("\nVeuillez rentrer la zone de destination: ");
					EncodeNom(Zone,50);

					strcpy(parameters[0],NumVehicule);
					strcpy(parameters[1],Zone);

					MakeRequete("GET_DELIVERY1",parameters,2,msgServeur);
					send_msg(ClientSocket,msgServeur);

					receiveSep(ClientSocket,msgServeur,"\r\n");

					strcpy(requete,msgServeur);

					command = getCommand(requete);
					printf("\nCommande : %d", command);
					NbrParameter = getParameters(msgServeur,parameters);

					if(command != GET_DELIVERY1)
					{
						printf("\nError ! Wrong response received");
					}
					else
					{
						cmp =strcmp("YES",parameters[0]);
						if(cmp == 0)
						{
							printf("\nNumSerie Article: %s",parameters[1]);
							printf("\nNom Article: %s",parameters[2]);
							printf("\nQuantité Article: %s",parameters[3]);
							deliveryContinue = 1;
						}
						else
							printf("\nPas d'article pour cette zone !");
					}
					if(deliveryContinue == 1)
					{
						do
						{
							printf("\nVoulez-vous encore des articles (Y/N) ?");
							EncodeNom(Encode,2);
							cmp = strcmp("Y",Encode);
							if(cmp == 0)
							{
								MakeRequete("GET_DELIVERY2",parameters,1,msgServeur);
								send_msg(ClientSocket,msgServeur);
							}
							else
							{
								MakeRequete("GET_DELIVERY_END",parameters,1,msgServeur);
								send_msg(ClientSocket,msgServeur);
								deliveryContinue = 0;
							}

							receiveSep(ClientSocket,msgServeur,"\r\n");

							strcpy(requete,msgServeur);

							command = getCommand(requete);
							printf("\nCommande : %d", command);
							NbrParameter = getParameters(msgServeur,parameters);

							if(command == GET_DELIVERY2)
							{
								cmp =strcmp("YES",parameters[0]);
								if(cmp == 0)
								{
									printf("\nNumSerie Article: %s",parameters[1]);
									printf("\nNom Article: %s",parameters[2]);
									printf("\nQuantité Article: %s",parameters[3]);
									deliveryContinue = 1;
								}
								else
								{
									printf("\nPas d'article pour cette zone !");
									deliveryContinue = 0;
								}
							}
							else
							{
								printf("\nDélivrement terminé !");
								deliveryContinue = 0;
							}
						}while(deliveryContinue == 1);
					}





					break;

				case 3:
					printf("Deconnexion au serveur");
					printf("\nVeullez rentrer votre login: ");
					EncodeNom(login,10);
					printf("\nVeuillez rentre votre mot de passe: ");
					EncodeNom(password,10);
					parameters[0] = login;
					parameters[1] = password;

					MakeRequete("DECONNECT",parameters,2,msgServeur);
					send_msg(ClientSocket,msgServeur);

					receiveSep(ClientSocket,msgServeur,"\r\n");

					strcpy(requete,msgServeur);

					command = getCommand(requete);
					NbrParameter = getParameters(msgServeur,parameters);

					if(command != DECONNECT)
					{
						printf("\nError ! Wrong response received");
					}
					else
					{
						cmp = strcmp(parameters[0],"YES");
						if(cmp == 0)
						{
							printf("\nDeconnexion accepté ! Vous pouvez quitter le serveur");					
							deconnected = 1;
							printf("\nAppuyer sur ""Enter"" pour continuer..");
							while ( getchar() != '\n' );
						}
						else
						{
							printf("\nDeconnexion refusée !");
							printf("\nRaison: %s",parameters[1]);

						}
					}
			}


		}while(deconnected == 0);
	}
	

	close(ClientSocket);
}


void EncodeNom(char *pvec,int LIM)
{
    int compt,error,len;
    char encode[127];

    do
    {
        compt=0;
        error=0;
        fflush(stdin);
        //Encodage de la chaîne
        fgets(encode,127,stdin);
        len = strlen(encode);
        encode[len-1] = 0;

        //On compte le nombre de caractères
        compt=strlen(encode);
        if(compt+1>LIM)
        {
            printf("Vous avez saisi trop de caract%cres.\nVeuillez recommencer: ",138);
            error=-1;
        }
        else
            strcpy(pvec,encode);
            //Si la chaîne ne dépasse pas la longuer limite, on peut la copier
    }while(error!=0);
}


int EncodeEntier(int min,int max)
{
    char nombre[11];
    int valeur,error,i;
    do
    {
        //Reinitilaisation des compteurs
        i=0;
        error=0;
        fflush(stdin);
        EncodeNom(nombre,10);

        //Un int ne dépassera ne dépassera jamaiss les 9 chiffres (+ le signe - si on a un nombre négatif)

        while(error!=1&&*(nombre+i)!=0)
        {
            //Si on trouve autre chose qu'un nombre ou un signe '-' en première position, on arrete
            if(isdigit(*(nombre+i))==0&&*nombre!='-')
            {
                error=1;
                printf("\nVous avez rentr%c un caract%cre.\nVeuillez recommencer: ",130,138);
            }
            i++;
        }

        if(i==0)
            printf("\nCe champ est obligatoire.\nVeuillez recommmencer: ");
        if(error==0)
        {
            //Si la valeur rentrée est correcte, on convertit la chaine en int
            valeur=atoi(nombre);
            //Il faut vérifier qu'elle se trouve bien entre les valeurs min et max
            if(valeur<min||valeur>max)
            {
                error=1;
                printf("\nVeuillez rentrez un nombre entre %d et %d: ",min,max);
            }
        }

    }while(error==1);

    return valeur;
}
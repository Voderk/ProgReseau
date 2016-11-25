#include "DMMP.h"

void MakeRequete(char* command,char** parameters,int NbrParam,char *request)
{
	int i,len;
	memset(request,0,1000);
	//Formation de la requete
	//Format de la requete: Command:parameter/parameter/parameter/....# 
	strcat(request,command);
	strcat(request,":");

	for(i = 0;i<NbrParam;i++)
	{
		strcat(request,*(parameters+i));
		strcat(request,"/");
	}
	len = strlen(request);
	*(request+len-1) = '#';

}

int getCommand(char *request)
{
	char* command;
	command = strtok(request,":");

	request = strtok(NULL,":");

	//Renvoie un nombre en fonction de la commande. Nombre choisi avec les define dans Serveur et client

	if(strcmp(command,"CONNECT") == 0)
		return 1;
	if(strcmp(command,"INPUT_DEVICES1") == 0)
		return 2;
	if(strcmp(command,"INPUT_DEVICES2") == 0)
		return 3;
	if(strcmp(command,"GET_DELIVERY1") == 0)
		return 4;
	if(strcmp(command,"GET_DELIVERY2") == 0)
		return 5;
	if(strcmp(command,"GET_DELIVERY_END") == 0)
		return 6;
	if(strcmp(command,"DECONNECT") == 0)
		return 7;

}

int getParameters(char * request,char** parameters)
{
	int nbrparameter = 0,i,end =0,j,k;
	char parameter[100];
	char temp[1000];
	char c;

	i = 0;

	//Suppression de la commande
	do
	{
		c = *(request+i);
		i++;
	}while(c != ':');

	strcpy(temp,request+i);
	//temp = request sans commande

	printf("\nGetParameter : %s",temp);

	k=0;
	do
	{
		j=0;
		memset(temp,0,30);
		do
		{
			c = *(request+i);
			parameter[j] = c;
			j++;
			i++;
		}while(c != '/' && c != '#');	
		
		if(c == '/')
		{
			parameter[j-1] = 0;
			strcpy(*(parameters+k),parameter);
			k++;
		}
		else
		{
			parameter[j-1] = 0;
			strcpy(*(parameters+k),parameter);
			end =1;
			k++;
		}

	}while(end == 0);
	
	return k;
}


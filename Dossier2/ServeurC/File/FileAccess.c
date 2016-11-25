#include "FileAccess.h"

int readline(int hdFich,char* msg)
{
	int NbLu,i;
	char c;

	i=0;
	do
	{
		//On lit caractère par caractère jusqu'à qu'on atteigne la fin de la ligne (donc "\n")
		if((NbLu = read(hdFich,&c,1)) <0)
		{
			printf("\nError reading file: %d",errno);
			exit(1);
		}
		else
		{
			if(NbLu != 0)
			{
				*(msg+i) = c;
				i++;
			}
		}


	}while(c != '\n' && NbLu != 0);

	//Fin de fichier 
	if(NbLu == 0)
		return -1;

	*(msg+i) = 0;
	return i;
}

int getProperties(char *filename,char* key,char *value,char* sep)
{
	int hdFich,length,find,ret;
	char temp[100];
	char *token;

	find = 0;

	//Ouverture du fichier du properties
	if((hdFich = open(filename,O_RDONLY)) == -1)
	{
		printf("\nError trying to open file %s: %d",filename,errno);
		exit(1);
	}

	while(find != 1)
	{
		//Lecture de la première ligne
		ret = readline(hdFich,temp);
		if(ret == -1)
		{
			close(hdFich);
			return -1;
		}
		//Récupération de la première valeur
		token = strtok(temp,sep);

		if(strcmp(key,token) == 0)
			find = 1;
	}
	//récupération de la clé
	token = strtok(NULL,sep);
	length = strlen(token);
	*(token+length-1) = 0;

	strcpy(value,token);
	
	close(hdFich);
	return 1;
}

void writeLog(int iCli,char * msg)
{
	int len,ret,hdFich;
	char message[50];
	if((hdFich = open("LogServeur.txt",O_WRONLY|O_CREAT|O_APPEND),0x777) == -1)
	{
		printf("\nError trying to open file LogServeur: %d",errno);
		exit(1);
	}

	sprintf(message,"Thread %d: %s\n",iCli,msg);
	len = strlen(message);

	if((ret = write(hdFich,message,len*sizeof(char))) <0)
	{
		printf("\nError writing file Emplacement.data: %d",errno);
		exit(1);
	}
	close(hdFich);

}

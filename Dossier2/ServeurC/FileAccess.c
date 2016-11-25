#include "FileAccess.h"

int readline(int hdFich,char* msg)
{
	int NbLu,i;
	char c;

	i=0;
	fflush(stdout);
	do
	{
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

	if((hdFich = open(filename,O_RDONLY)) == -1)
	{
		printf("\nError trying to open file %s: %d",filename,errno);
		exit(1);
	}

	while(find != 1)
	{
		ret = readline(hdFich,temp);
		if(ret == -1)
		{
			close(hdFich);
			return -1;
		}

		token = strtok(temp,sep);

		if(strcmp(key,token) == 0)
			find = 1;
	}
	
	token = strtok(NULL,sep);
	length = strlen(token);
	*(token+length-1) = 0;

	strcpy(value,token);
	
	close(hdFich);
	return 1;
}

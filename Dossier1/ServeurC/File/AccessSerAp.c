#include "AccessSerAp.h"


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int getFournisseur(char *t)
{
	int hdFich,find,cmp,ret,len;
	char temp[100];

	find = 0;
	ret = 0;

	//Ouverture du fichier fournisseur
	if((hdFich = open("fournisseurs.txt",O_RDONLY)) == -1)
	{
		printf("\nError trying to open file fournisseur.txt: %d",errno);
		exit(1);
	}

	do
	{
		//Lecture d'un élément
		ret = readline(hdFich,temp);
		len = strlen(temp);
		temp[len - 1] = 0;
		//Comparaison avec le fournisseur fourni en paramètre
		cmp = strcmp(t,temp);
		//Si identique, on sort de la boucle et on renvoie true
		if(cmp == 0)
		{
			find = 1;
		}

	}while(find == 0 && ret != -1);

	close(hdFich);

	if(find != 0)
		return 1;
	else
		return 0;
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int FindDispoZone(int* x,int* y)
{
	int hdFich,NbLu,i,j,find,deplacement,k = 1,ret;
	char c;

	//Ouverture du fichier Emplacement
	if((hdFich = open("Emplacement.data",O_RDWR)) == -1)
	{
		printf("\nError trying to open file: %d",errno);
		exit(1);
	}
	find = 0;

	//Recherche d'un emplacement libre => 0 dans le fichier
	for(i = 0;i<100 && find == 0 && NbLu !=0 ;i++)
	{
		if((NbLu = read(hdFich,&c,1)) <0)
		{
			printf("\nError reading file: %d",errno);
			exit(1);
		}
		if(c == 0)
		{
			find = 1;
		}
	}
	//Si trouvé, déplacement dans le fichier et écriture de 1 à cet emplacement (1 = Emplacement occupé)
	if(find == 1)
	{
		i = i-1;
		*x = i/10;
		*y = ((i)%10);


		printf("Find : %d/%d",*x,*y);

		deplacement = ((*x)*10) + *y;

		if((ret = lseek(hdFich,deplacement*sizeof(char),0)) == -1)
		{
			printf("\nError during lseek in Emplacement.data: %d",errno);
			exit(1);
		}

		if((ret = write(hdFich,&k,sizeof(char))) <0)
		{
			printf("\nError writing file Emplacement.data: %d",errno);
			exit(1);
		}
		return 1;
	}
	//Non trouvé Retour -1
	else
		return -1;

}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
void MakeArticle(char** articles, char *request)
{
	int i,len;
	memset(request,0,100);
	//Formation de la requete
	//Format de l'article sous forme chaine de caractère => NumSérie;Nom;Quantité# 

	for(i = 0;i<3;i++)
	{
		strcat(request,*(articles+i));
		strcat(request,";");
	}
	len = strlen(request);
	*(request+len-1) = 0;
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
void InitArticle(char * article,struct Article * NewArticle)
{
	char * token;

	//Initialisation de la structure article en fonction de la chaine de caractère

	token = strtok(article,";");
	printf("\nInitArticle : %s",token);
	NewArticle->NumSerie = atoi(token);

	token = strtok(NULL,";");
	printf("\nInitArticle : %s",token);
	fflush(stdout);
	strcpy(NewArticle->nom,token);

	token = strtok(NULL,";");
	printf("\nInitArticle : %s",token);
	NewArticle->Quantite = atoi(token);

	printf("\nNom : %s",NewArticle->nom);
	fflush(stdout);
}



/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int WriteArticle(int x,int y,struct Article* NewArticle)
{
	int deplacement,ret,hdFich;

	deplacement = 10*x + y;

	//Ouverture du fichier Entrepot
	if((hdFich = open("Entrepot.data",O_RDWR,0x777)) == -1)
	{
		printf("\nError trying to open file Emplacement.data: %d",errno);
		exit(1);
	}

	//Déplacement à un emplacement vide
	if((ret = lseek(hdFich,deplacement*sizeof(struct Article),0)) == -1)
	{
		printf("\nError during lseek in Entrepot.data: %d",errno);
		exit(1);
	}

	//Ecriture de l'article dans le fichier
	if((ret = write(hdFich,NewArticle,sizeof(struct Article))) <0)
	{
		printf("\nError writing file Entrepot.data: %d",errno);
		exit(1);
	}
	close(hdFich);
	return 1;
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
void InitZone(struct Article* NewArticle)
{
	int temp;
	temp = NewArticle->NumSerie%3;

	switch(temp)
	{
		case 0:
			strcpy(NewArticle->zone,"Anvers");
		break;
		case 1:
			strcpy(NewArticle->zone,"Liege");
		break;
		case 2:
			strcpy(NewArticle->zone,"Bruxelles");
		break;
	}
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int getId(char * zone, struct Article * dest)
{
	int hdFich, NbLu, find = 0, ret, i = 0,k;
	struct Article temp, to_overwrite;
	if((hdFich = open("Entrepot.data",O_RDWR)) == -1)
	{
		printf("\nError trying to open file: %d",errno);
		exit(1);
	}
	//Recherche d'un article dans la zone donnée
	do
	{
		NbLu = read(hdFich,&temp,sizeof(struct Article));
		if(NbLu < 0)
		{
			printf("\nError reading file: %d",errno);
			exit(1);
		}
		if(strcmp(zone, temp.zone) == 0 && temp.NumSerie != -1)
		{
			find = 1;
		}
		else i++;
	}while(find == 0 && NbLu != 0);
	
	if(find == 1)
	{
		//Si trouvé, renvoie de l'article plus écriture de -1 dans le fichier
		memset(&to_overwrite, 0,sizeof(struct Article));
		
		to_overwrite.NumSerie = -1;

		//Déplacment à l'endroit où se trouve l'article
		if((ret = lseek(hdFich, i * (sizeof(struct Article)), 0)) == -1)
		{
			printf("\nError during lseek in Entrepot.data: %d",errno);
			exit(1);
		}

		//Ecriture là où se trouve l'article
		if((ret = write(hdFich,&to_overwrite,sizeof(struct Article))) <0)
		{
			printf("\nError writing file Entrepot.data: %d",errno);
			exit(1);
		}
		close(hdFich);
		memcpy(dest, &temp, sizeof(struct Article));


		if((hdFich = open("Emplacement.data",O_RDWR)) == -1)
		{
			printf("\nError trying to open file: %d",errno);
			exit(1);
		}
		find = 0;

		if((ret = lseek(hdFich,i*sizeof(char),0)) == -1)
		{
			printf("\nError during lseek in Emplacement.data: %d",errno);
			exit(1);
		}
		k = 0;
		if((ret = write(hdFich,&k,sizeof(char))) <0)
		{
			printf("\nError writing file Emplacement.data: %d",errno);
			exit(1);
		}

		close(hdFich);

		return temp.NumSerie;
	}
	else 
	{
		close(hdFich);
		return -1;
	}
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int existDelivery(char * zone)
{
	int hdFich, NbLu, find = 0, ret;
	struct Article temp;
	if((hdFich = open("Entrepot.data",O_RDWR)) == -1)
	{
		printf("\nError trying to open file: %d",errno);
		exit(1);
	}
	do
	{
		NbLu = read(hdFich,&temp,sizeof(struct Article));
		if(NbLu < 0)
		{
			printf("\nError reading file: %d",errno);
			exit(1);
		}
		if(strcmp(zone, temp.zone) == 0 && temp.NumSerie != -1)
		{
			find = 1;
		}
	}while(find == 0 && NbLu != 0);
	close(hdFich);
	return find;
	
}


/*************************************************************************
** I: Message, longeur du message,séparateur                            **
** P: Recherche du marqueur de fin de fichier							**
** O: Message reçu		 												**
*************************************************************************/
int getPosition(int ref, int * x, int * y)
{
	int hdFich, NbLu, find = 0, ret;
	struct Article temp;
	if((hdFich = open("Entrepot.data",O_RDWR)) == -1)
	{
		printf("\nError trying to open file: %d",errno);
		exit(1);
	}
	do
	{
		NbLu = read(hdFich,&temp,sizeof(struct Article));
		if(NbLu < 0)
		{
			printf("\nError reading file: %d",errno);
			exit(1);
		}
		if(ref == temp.NumSerie && temp.NumSerie != -1)
		{
			find = 1;
		}
	}while(find == 0 && NbLu != 0);
	if(find == 1)
	{
		*x = temp.x;
		*y = temp.y;
	}


	close(hdFich);
	return find;
}
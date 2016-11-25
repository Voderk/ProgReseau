#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include "FileAccess.h"

struct Article
{
	int NumSerie;
	char nom[100];
	int Quantite;
	char zone[30];
	int x;
	int y;
};

int getFournisseur(char *f);
int FindDispoZone(int* x,int* y);
int WriteArticle(int x, int y,struct Article* NewArticle);
void MakeArticle(char **article,char *request);
void InitArticle(char * article,struct Article* Newarticle);
void InitZone(struct Article* NewArticle);
int getId(char * zone, struct Article * dest);
int existDelivery(char * zone);
int getPosition(int ref, int * x, int * y);

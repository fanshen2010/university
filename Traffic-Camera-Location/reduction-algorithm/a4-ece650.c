#include "SAT.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define  MaxV  500

 // AdjList elem defination
typedef struct Enode{
	int edata;
	struct Enode *next;
}E;

 // AdjList defination
typedef struct Vnode{
	int vdata;
	E *first;
}V, AdjList[MaxV];

 // Gragh definition
typedef struct Gragh{
	int vcount;
	int ecount;
	AdjList adjlist;
}G;


void VertexCover(G *G){
	int v=G->vcount;
	int e=G->ecount;
	int k;// kth total
	//int result;
	//int result_n;
	int i,j,m;// print for || i for n
	E *p;
	int *c1 =(int*)malloc(v*sizeof(int));
	int c2[2];
	int c3[2];
	int *c4;
	int *output = (int*)malloc(v*sizeof(int));

	//vertex cover pipe
	int pfd[2], bak;
	pipe(pfd);
	bak = dup(STDOUT_FILENO);
	dup2(pfd[1], STDOUT_FILENO);

	for(i=0;i<v;i++){
		output[i]=0;
	}

	for(k=1;k<=v;k++){
		c4 = (int*)malloc(2*k*sizeof(int));
		SAT_Manager mgr = SAT_InitManager();
		SAT_SetNumVariables(mgr, v*k);//k from 1 to n

		//at least one of n is ith
		for(i=0;i<k;i++){
			for(j=1;j<=v;j++){
				c1[j-1] = ((i*v+j) << 1);
			}
			SAT_AddClause(mgr, c1, v);
		}
		
		// m not both pth and qth
		for(i=0;i<k;i++){
			for(j=i+1;j<k;j++){
				for(m=1;m<=v;m++){
					c2[0] = ((i*v+m) << 1) + 1;
					c2[1] = ((j*v+m) << 1) + 1;
					SAT_AddClause(mgr, c2, 2);
				}
			}
		}

		// mth not map to both p and q
		for(i=1;i<=v;i++){
			for(j=i+1;j<=v;j++){
				for(m=0;m<k;m++){
					c3[0] = ((m*v+i) << 1) + 1;
					c3[1] = ((m*v+j) << 1) + 1;
					SAT_AddClause(mgr, c3, 2);
				}
			}
		}

		// at least one of i and j are th
		for(i=0;i<v;i++){
			p=G->adjlist[i].first;
			while(p){
				//search e
				j=p->edata;
				if(i<j){
					for(m=0;m<k;m++){
						c4[m] = ((m*v+i+1) << 1);
						c4[k+m] = ((m*v+j+1) << 1);
					}
					SAT_AddClause(mgr, c4, 2*k);
				}
				p=p->next;
			}
		}
		free(c4);

		// k found
		int result = SAT_Solve(mgr);
		if(result == SATISFIABLE) {
			int result_n = SAT_NumVariables(mgr);
			dup2(bak, STDOUT_FILENO);
			//print
			for(i = 1; i <= result_n; i++) {
				int a = SAT_GetVarAsgnment(mgr, i);
				if(a == 1) {
					if(i%v==0){
						output[v-1] = 1;
						//printf("%d ", (v-1));
					}else{
						output[i%v-1] = 1;
						//printf("%d ", (i%v-1));
					}
				}
			}
			j=0;
			for(i = 0; i <v; i++){
				if(output[i]==1){
					if(j==0){
						printf("%d",i); fflush(stdout);
					}else{
						printf(" %d",i); fflush(stdout);
					}
					j++;
				}
			}
			break;
		}
	}
	free(c1);
	free(output);
}

 //initV
void InitV(G *G,int vcount){
	int i;
	G->vcount = vcount;
	for (i = 0; i < vcount; i++){
	  G->adjlist[i].vdata = i;
	  G->adjlist[i].first = NULL;
	}
}


int CheckEdgeInit(G *G,int i,int j){
	int flag=1;
	if(i>=G->vcount || i<0){
		printf("Error:vertex %d does not exist \n",i);
		flag=0;
	}
	if(j>=G->vcount || j<0){
		printf("Error:vertex %d does not exist \n",j);
		flag=0;
	}
	if(flag==0){
		return 0;
	}
	return 1;
}


void printG(G *G){
	int i;
	E *p;
	for(i=0;i<G->vcount;i++){
		printf("v:%d \n",G->adjlist[i].vdata);
		p=G->adjlist[i].first;
		while(p){
			printf("e:%d",p->edata);
			p=p->next;
		}
		printf("\n");
	}
}

 //initE
void InitE(G *G,int edge[],int ecount){
	int i=0;
	int v1;
	int v2;
	E *s;
	E *p;
	G->ecount = ecount/2;
	while(i < ecount-1){
		v1=edge[i];
		v2=edge[i+1];
		if(v1!=v2){
			p=G->adjlist[v1].first;
			while(p){
				if(v1==G->adjlist[v1].vdata && v2==p->edata){
					break;
				}
				p=p->next;
			}
			if(!p){
				s=(E*)malloc(sizeof(E));
				s->edata = v2;
				s->next=G->adjlist[v1].first;
				G->adjlist[v1].first=s;
				//free(s);
				//s=NULL;

				s=(E*)malloc(sizeof(E));
				s->edata = v1;
				s->next=G->adjlist[v2].first;
				G->adjlist[v2].first=s;
				//free(s);
				//s=NULL;
			}			
		}
		i=i+2;
	}
}

void Delete(G *G){
	int i;
	E *q;
	E *p;
    for (i = 0; i < G->vcount ; i++){
        p = G->adjlist[i].first;
        while(p){
            q = p;
            p = p->next;
			free(q);
			q = NULL;
        }
        G->adjlist[i].first = NULL;
    }
	G->ecount=0;
	G->vcount=0;
}



int main()
{
	G G;
	char a[5000];
	char cmd;

	int vcount;
	
	int eline[5000];
	char *split = " E{<,>}";
	char *p;

	int i;
	int j;
	int start;
	int end;
	
	while(fgets(a,5000,stdin)){
		cmd = a[0];
		if(cmd=='V'){
			sscanf(a,"V %d",&vcount);
			Delete(&G);			
			InitV(&G,vcount);
		}
		else if(cmd=='E'){
			// transfer input into array
			i=0;
			j=0;
			p = strtok(a,split);
			while(p!=NULL) {
				sscanf(p,"%d",&eline[i]);
				//printf("%d ",eline[i]);
				p = strtok(NULL,split);
				i=i+1;
			}
			// check array
			for(j=0;j<i-1;j=j+2){
				if(CheckEdgeInit(&G,eline[j],eline[j+1])==0){
					fflush(stdout);
					break;
				}
			}
			if(j<i-1){
				Delete(&G);
				continue;
				
			}else{
				//  init edge
				InitE(&G,eline,i-1);
				if(G.ecount>0){
					VertexCover(&G);
				}
			}
			//printG(&G);
		}
	}
	return 0;
}

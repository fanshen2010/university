#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define  MaxV  500
#define  MaxQ  500


 // AdjList elem defination
typedef struct Enode{
	int edata;
	struct Enode *next;
}E;

 // AdjList defination
typedef struct Vnode{
	int vdata;
	int path;
	E *first;
}V, AdjList[MaxV];

 // Gragh definition
typedef struct Gragh{
	int vcount;
	int ecount;
	AdjList adjlist;
}G;
 // Queue definition
typedef struct Queue{
    int front;
    int rear;
    int count;
    int data[MaxQ];
}Q;

int visit[MaxV]={0};

void InitVisit(G *G){
	int i;
	for (i = 0; i < MaxV; i++){
		visit[i]=0;
	}
	for (i = 0; i < G->vcount; i++){
	  G->adjlist[i].path = -1;
	}
}

 //initV
void InitV(G *G,int vcount){
	int i;
	G->vcount = vcount;
	for (i = 0; i < vcount; i++){
	  G->adjlist[i].vdata = i;
	  G->adjlist[i].path = -1;
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


void InitQ(Q *Q){
    Q->front = Q->rear = 0;
    Q->count = 0;
}
void InQ(Q *Q, int elem){
    Q->data[Q->rear] = elem;
    Q->rear = (Q->rear + 1) % MaxQ;
	Q->count++;
}
int OutQ(Q *Q){
    int output = Q->data[Q->front];
    Q->front = (Q->front + 1) % MaxQ;
	Q->count--;
    return output;
}


void ShowPath(G *G, int start, int end){
	// find path from end to start
	V v;
	v = G->adjlist[end];
	if(v.path!=-1){
		ShowPath(G,start,v.path);
		printf("-%d",end);	
	}else{
		if(v.vdata==start){
			printf("%d",end);
		}else{		
			printf("Error:the shortest path does not exist between them \n");
			return;
		}
	}
}



void BFS(G *G, int start, int end){
	// init path
    int i;
    Q Q;
    E *p;
	InitVisit(G);
    InitQ(&Q);
    visit[start] = 1;
    InQ(&Q, start);
    while (Q.count != 0){
        i = OutQ(&Q);
        p= G->adjlist[i].first;
		while (p){
            if (!visit[p->edata]){
                visit[p->edata] = 1;
                InQ(&Q, p->edata);
				G->adjlist[p->edata].path=i;
            }
            p = p->next;
        }
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
			printf("V = %d\n",vcount);
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
					break;
				}
			}
			if(j<i-1){
				Delete(&G);
				//printf("Econtinue\n");
				//fflush(stdout);
				continue;
				
			}else{
				//  init edge
				InitE(&G,eline,i-1);
				printf("E = {");
				for(j=0;j<i-1;j=j+2){
					if(j<i-3){
						printf("<%d,%d>,",eline[j],eline[j+1]);
					}else{
						printf("<%d,%d>",eline[j],eline[j+1]);
					}
				}
				printf("}\n");
				fflush(stdout);
			}
			//printG(&G);
		}
		else if(cmd=='s'){
			sscanf(a,"s %d %d",&start,&end);
			//printf("s_command receive: %d,%d\n",start,end);	
			// check start end
			if(CheckEdgeInit(&G,start,end)==0){
				//printf("scontinue\n");
				//fflush(stdout);
				continue;
			}else if(start==end){
				// output same point
				printf("%d-%d\n",start,end);
			}else{
				// output short path
				BFS(&G,start,end);
				ShowPath(&G,start,end);
				printf("\n");
			}
			//printG(&G);
			fflush(stdout);
		}
	}
	return 0;
}

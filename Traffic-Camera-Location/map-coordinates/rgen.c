#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<fcntl.h>
#include<string.h>

int check(int p1[],int p2[],int p3[],int p4[]){
	float a,b,c,d;
	float a1,c1;
	float x1=(float)p1[0];
	float x2=(float)p2[0];
	float x3=(float)p3[0];
	float x4=(float)p4[0];
	float y1=(float)p1[1];
	float y2=(float)p2[1];
	float y3=(float)p3[1];
	float y4=(float)p4[1];

	if(x3 == x4 && y3 == y4){
		return 0;		
	}

	if((x1 == x3 && y1 == y3 && x2 == x4 && y2 == y4)
		||(x1 == x4 && y1 == y4 && x2 == x3 && y2 == y3)){
		return 0;
	}

	if(x1 == x2 && x1 == x3 && x1 == x4){
		if(y1<y3<y2 || y2<y3<y1 || y1<y4<y2 || y2<y4<y1
			|| y3<y1<y4 || y4<y1<y3 || y3<y2<y4 || y4<y2<y3){
			return 0;
		}
	}
	else{
		a1=(x1 - x2);
		c1=(x3 - x4);
		if(a1!=0 && c1!=0){
			a = (y1 - y2)/a1;
			b = y1 - a * x1;
           	c = (y3 - y4)/c1;
			d = y3 - c * x3;
			if(a==0.0 && c==0.0){
				if((x1<x3<x2 || x2<x3<x1 || x1<x4<x2 || x2<x4<x1
					|| x3<x1<x4 || x4<x1<x3 || x3<x2<x4 || x4<x2<x3)){
					return 0;
				}	
			}
			else if(a==c){
				if(y3==a*x3+b &&
					(y1<y3<y2 || y2<y3<y1 || y1<y4<y2 || y2<y4<y1
					|| y3<y1<y4 || y4<y1<y3 || y3<y2<y4 || y4<y2<y3)){
					return 0;
				}
			}
		}
	}
	return 1;
}


int random_num(int min,int max){
	int result;
	static int fd =-1;
    char *next_random_byte;
	int bytes_to_read;
	unsigned random_value;

	if (fd == -1){
		fd =open("/dev/urandom",O_RDONLY);
	} 
	next_random_byte =(char*)&random_value;
	bytes_to_read = sizeof(random_value);
	do{
		int bytes_read;
		bytes_read = read(fd,next_random_byte,bytes_to_read);
		bytes_to_read -=bytes_read;
		next_random_byte +=bytes_read;
	}while(bytes_to_read>0);

	result =min+ (random_value %(max-min+1));
}

void print_s(int street[100][500][2],int sn,int count[100],int remove){
	//printtest
	int j,k;
	// r command
	for(j=0;j<remove;j++){
		printf("r \"a%d\"\n",j);
	}
	// a command
	for(j=0;j<sn;j++){
		printf("a \"a%d\"",j);
		for(k=0;k<count[j];k++){
			printf("(%d,%d)",street[j][k][0],street[j][k][1]);
		}
		printf("\n");
	}
	// g command
	printf("g\n");
}

void initcount(int count[100]){
	int i;
	for (i = 0; i < 100; i++){
		count[i]=0;
	}
}

int main(int argc,char* argv[]){

	int snmax=10; // [2,k]
	int slmax=5; //[1,k]
	int secondmax=5; //[5,k]
	int pointmax=20; //[-k,k]

	int sn,sl,second,pointX,pointY;
	int street[100][500][2]={0};

	int i,j,k;
	int m,n,t;

	int error=0;
	int temp[2];
	int checkresult;
	int count[100]={0};
	int remove=0;

	if(argc>1){
		
		int param;		
		for(i=1;i<argc-1;i++){
			param = atoi(argv[i+1]);
			if(strcmp(argv[i],"-s")==0){
				if(param<2){
					fprintf(stderr,"Error: -s should not be less than 2\n");
					return 0;
				}else{
					snmax = param;
				}
			}
			else if(strcmp(argv[i],"-n")==0){
				if(param<1){
					fprintf(stderr,"Error: -n should not be less than 1\n");
					return 0;
				}else{
					slmax = param;
				}
			}
			else if(strcmp(argv[i],"-l")==0){
				if(param<5){
					fprintf(stderr,"Error: -l should not be less than 5\n");
					return 0;
				}else{
					secondmax = param;
				}
			}
			else if(strcmp(argv[i],"-c")==0){
				pointmax = param;
			}
		}
	}

	
	second = random_num(5,secondmax);

	while(1){
		sn = random_num(2,snmax);
		j=0;//street number
		initcount(count);
		error = 0;
		while(j<sn){
			sl = random_num(2,slmax+1);
			k=0;//street point
			while(k<sl){
				if (error>=25){
					fprintf(stderr,"Error: failed to generate valid input for 25 stimultance attempts\n");
					return 0;
				}
				pointX = random_num(-pointmax,pointmax);
				pointY = random_num(-pointmax,pointmax);
				temp[0]=pointX;
				temp[1]=pointY;
				
				//same point
				if(k>0){
					if(street[j][k-1][0]==temp[0] && street[j][k-1][1]==temp[1]){
						error++;
						continue;
					}
				}
				

				//self
				for(n=0;n<k-1&&k>0;n++){
					checkresult = check(street[j][n],street[j][n+1],street[j][k-1],temp);
					if(checkresult == 0){
						error++;
						break;
					}
				}
				//self wrong no k++
				if(n<k-1&&k>0){
					continue;
				}

				//other
				t=0;
				for(m=0;m<j&&k>0;m++){
					for(n=0;n<count[t]-1;n++){
						checkresult = check(street[m][n],street[m][n+1],street[j][k-1],temp);
						if(checkresult == 0){
							error++;
							break;
						}
					}
					//other wrong no k++
					if(n<count[t]-1){
						break;
					}
					t++;
				}
				//other wrong no k++
				if(m<j&&k>0){
					continue;
				}

				street[j][k][0]=pointX;
				street[j][k][1]=pointY;
				count[j]++;
				k++;
			}
			j++;
		}

		print_s(street,sn,count,remove);
		fflush(stdout);
		usleep(second*1000000);
		remove = sn;
	}
	return 0;
}


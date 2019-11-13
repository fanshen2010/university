#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/wait.h>
#include<errno.h>
#include<string.h>


int main(int argc, char *argv[]){

	int pfd_rgen_a1[2];
	int pfd_a1_a2[2];
	char s[5000];
	if(pipe(pfd_rgen_a1)== -1){
		perror("pipe_rgen_a1");
		exit(1);
	}
	if(pipe(pfd_a1_a2)== -1){
		perror("pipe_a1_a2");
		exit(1);
	}

    pid_t a3_rgen=fork();
   	if(a3_rgen == 0){
		close(pfd_rgen_a1[0]);
		dup2(pfd_rgen_a1[1],STDOUT_FILENO);
		execvp("./rgen", argv);
    }else{
		pid_t a3_a1= fork();
		if(a3_a1 == 0){
			close(pfd_rgen_a1[1]);
			dup2(pfd_rgen_a1[0],STDIN_FILENO);

			close(pfd_a1_a2[0]);
			dup2(pfd_a1_a2[1],STDOUT_FILENO);

			execl("/usr/bin/python", "/usr/bin/python", "./a1-ece650.py", (char *)NULL);
		}else{
			pid_t a3_a2= fork();
			if(a3_a2 == 0){
				close(pfd_a1_a2[1]);
				dup2(pfd_a1_a2[0],STDIN_FILENO);
				execvp("./a2-ece650",NULL);
				
			}
		}
	}


	while(fgets(s,5000,stdin)){
		write(pfd_a1_a2[1],s,strlen(s));
	}


	/*
	FILE * rstream;
	FILE * wstream;
	FILE * a2;
	char buffer[800];
	char buffer2[800];
	rstream=popen("./rgen","r");
	if(wstream == NULL && rstream == NULL){
		perror("Fail to popen\n");
		exit(1);
	}else{
		fread(buffer,sizeof(buffer),sizeof(char),rstream);
		wstream = popen("python a1-ece650.py","w");
		fprintf(wstream,buffer);
		pclose(rstream);
		a2 = popen("./a2-ece650","r");
	}
	pclose(wstream);
	*/
	return 0;
}



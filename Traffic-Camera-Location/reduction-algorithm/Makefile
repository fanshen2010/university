all: a4-ece650 a5-ece650

a4-ece650:a4-ece650.c
	g++ a4-ece650.c -Lzchaff/ -Izchaff  -lsat -o a4-ece650

a5-ece650:a5-ece650.c
	g++ a5-ece650.c -L. -lsat -lpthread -lrt -o a5-ece650

clean:
	rm -rf a4-ece650.o a4-ece650 a5-ece650.o a5-ece650

// Fill out your copyright notice in the Description page of Project Settings.

#include "ServerThread.h"
#include <RunnableThread.h>
#include <WS2tcpip.h>
#include <vector>
#include <cstdlib>


AServerThread* AServerThread::Runnable = NULL;

/*
	Constructor of the class AServerThread: it neeed the port that the server
	is going to be listening to.
	It creates the thread that runs the server
*/
AServerThread::AServerThread(int p)
{
	port = p;
	Thread = FRunnableThread::Create(this, TEXT("ServerTCP"), 0, TPri_BelowNormal); //windows default = 8mb for thread, could specify more
	

}

/*
	Destructor of the class AServerThread
*/
AServerThread::~AServerThread()
{
	delete Thread;
	Thread = NULL;
}


/*
	This method inititilializes the server with the port given in the constructor
*/
bool AServerThread::Init()
{
	return TheServer.Initialize(port);

}

/*
	Method to start the server
*/
uint32 AServerThread::Run()
{
	FPlatformProcess::Sleep(0.03);
	TheServer.Run();
	
	return 0;
}
/*
	Method to stop the server
*/
void AServerThread::Stop()
{

	StopTaskCounter.Increment();

	TheServer.Stop();
	
}


/*
	This method stops the server and wait until the thread has finished
*/
void AServerThread::EnsureCompletion()
{

	Stop();
	
    Thread->WaitForCompletion();
	
}

/*
	This method must be called whenever you want to finish the server.
	It stops it and make sure it finish properly
*/
void AServerThread::Shutdown()
{
	if (Runnable)
	{
		Runnable->EnsureCompletion();
		delete Runnable;
		Runnable = NULL;
	}
}

/*
	It returns the las message the server received
	It return null if the server hasn't received any messages
*/
TArray<FString> AServerThread::getLastMessage()
{
	return TheServer.getLastMessage();
}

/*
	Constructor of the class ServerTCP
	Initially it's not running
*/
AServerThread::ServerTCP::ServerTCP()
{
	
	running = false;
}

/*
	Method to initialize the ServerTCP
	It creates all the sockets it needs with the given port
*/
bool AServerThread::ServerTCP::Initialize(int p)
{
	port = p;
	
	//Winsock initializing
	WSADATA wsData;
	WORD version = MAKEWORD(2, 2);
	int iniOK = WSAStartup(version, &wsData);

	if (iniOK != 0)
		return false;

	//Creation of the listening socket
	listening = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	if (listening == INVALID_SOCKET)
		return false;

	//Add IP and port to the socket
	sockaddr_in hint;
	hint.sin_family = AF_INET;
	hint.sin_port = htons(port);
	hint.sin_addr.S_un.S_addr = INADDR_ANY;

	bind(listening, (sockaddr*)&hint, sizeof(hint));

	//Set the socket as a listening socket
	listen(listening, SOMAXCONN);

	//Set the socket to non blocking mode
	u_long iMode = 1; //Any value different from zero is non blocking
	ioctlsocket(listening, FIONBIO, &iMode);
	
	//Set running to true
	running = true;
	return true;
}

/*
	This method is called after the thread is initialized and it starts
	the main loop of the server
*/
void AServerThread::ServerTCP::Run()
{
	
	//Struct with sockets
	FD_SET readfds; 

	//Buffer where we receive the data
	char buffer[4096];

	
	//Main loop of the server
	while ( running ) {

		//Set to zero all the bits of buffer
		ZeroMemory(buffer, 4096);

		//Clean readfds
		FD_ZERO(&readfds); 

		//Add the listening socket to readfds
		FD_SET(listening, &readfds); 

		//Fill readfds with the sockets
		for (int i = 0; i < connections.Num(); i++) {
			FD_SET(connections[i], &readfds);
		}

		//Wait until there is a message
		select(0, &readfds, NULL, NULL, NULL); 

		
		//If the listening socket is in readfds means there is a new connection
		if (FD_ISSET(listening, &readfds))
		{
			SOCKET newConnection = accept(listening, NULL, NULL);
			//If the new connection has been created succesfully
			if (newConnection != INVALID_SOCKET) { 

				connections.Add(newConnection);

			}

		}
		//For all the connections
		for (int i = 0; i < connections.Num(); i++) {

			//If the socket is in readfds means there is a new message in it
			if (FD_ISSET(connections[i], &readfds)) 
			{
				//Clean the buffer
				ZeroMemory(buffer, 4096);

				//Receive the message
				int retval = recv(connections[i], buffer, 4096, NULL);
				//If we received something we set the attribute lastMessage to the new message
				if (retval > 0) {
					FString msg = buffer;
					msg.ParseIntoArrayLines(lastMessage);
				}
				
			}
		}

	}
	
}

/*
	Method to stop the ServerTCP
	It closes all the sockets and cleans the winsock
*/
void AServerThread::ServerTCP::Stop()
{

	running = false;
	closesocket(listening);

	for (int i = 0; i < connections.Num(); i++)
		closesocket(connections[i]);

	WSACleanup();

}

/*
	Method that returns the last message the ServerTCP has received and
	set it to null afterwards
*/
TArray<FString> AServerThread::ServerTCP::getLastMessage()
{
	TArray<FString> msg(lastMessage);
	lastMessage.Empty();
	return msg;
}

// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include <WS2tcpip.h>

/**
	This class handles the messages reception by creating a new thread where
	this task will be done
 */
class SERVERTCP3_API AServerThread : public FRunnable
{
public:
	// Sets default values for this actor's properties
	AServerThread(int port);
	~AServerThread();

	///////////////SERVER CLASS//////////////
	class ServerTCP {
	private:
		int port;
		SOCKET listening;
		TArray<SOCKET> connections;
		TArray<FString> lastMessage;
		bool running;
		

	public:
		ServerTCP();
		bool Initialize(int port);
		void Run();
		void Stop();
		TArray<FString> getLastMessage();
	};

	/////////////FRUNNABLE INTERFACE/////////
private:
	static AServerThread* Runnable;
	ServerTCP TheServer;
	FRunnableThread* Thread;
	FThreadSafeCounter StopTaskCounter;
	int port;

public:
	virtual bool Init();
	virtual uint32 Run();
	virtual void Stop();
	void EnsureCompletion();
	static void Shutdown();
	TArray<FString> getLastMessage();
};
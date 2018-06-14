// Fill out your copyright notice in the Description page of Project Settings.

#pragma once
#include "ServerThread.h"
#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "ControladorServer.generated.h"

UCLASS()
class SERVERTCP3_API AControladorServer : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	AControladorServer();

protected:
	
	 void BeginPlay() override;
	 void EndPlay(const EEndPlayReason::Type EndPlayReason) override;

public:	
	
	virtual void Tick(float DeltaTime) override;

	AServerThread* theServer;

	UFUNCTION(BlueprintCallable, Category="Get")
		TArray<FString> getLastMessages();

	UFUNCTION(BlueprintCallable, Category = "Get")
		void getLastVectors(TArray<FVector> & allVecs, TArray<float> & allZooms);

	UFUNCTION(BlueprintCallable, Category = "Get")
		void getLastAxis(TArray<float> & allVal, TArray<FString> & allAxis);
	
	UFUNCTION(BlueprintCallable, Category = "Get")
		void getLastJSONLR(TArray<FVector> & valR, TArray<FVector> & valL, TArray<bool> & handR, TArray<bool> & handL, bool & recibido);

	UFUNCTION(BlueprintCallable, Category = "State")
		void Init(const int port);

	UFUNCTION(BlueprintCallable, Category = "Movement") 
		FRotator getNewRotation(const FVector & rads);

	UFUNCTION(BlueprintCallable, Category = "Movement")
		float getNewAxisRotation(const float & rads);

	float radsToDeg(float & rads);


	
};

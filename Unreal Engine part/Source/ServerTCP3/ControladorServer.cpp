// Fill out your copyright notice in the Description page of Project Settings.

#include "ControladorServer.h"
#include "ServerThread.h"
#include <string>


/*
	Constructor of the class AControladorServer
*/
AControladorServer::AControladorServer()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;

}

/* 
	Called when the game starts or when spawned
*/
void AControladorServer::BeginPlay()
{
	Super::BeginPlay();
	
}

/*
	Called when the game is finished
	It stops the server
*/
void AControladorServer::EndPlay(const EEndPlayReason::Type EndPlayReason)
{

	if (theServer) {

		theServer->Shutdown();
		delete theServer;
		theServer = nullptr;

	}

	Super::EndPlay(EndPlayReason);

}

// Called every frame
void AControladorServer::Tick(float DeltaTime)
{
	Super::Tick(DeltaTime);

}

/*
	Method to get the last message the server received
	It returns an empty TArray if it hasn´t received anything
*/
TArray<FString> AControladorServer::getLastMessages()
{
	TArray<FString> msg;

	if(theServer)
		msg = theServer->getLastMessage();
	return msg;
}


/*
	Method that returns the last messages with the information of the sensors of the phone
	in a TArray of FVector and a TArray with the zoom.

	This methos is used in the "Free rotation interaction"

	The messages has to be sent with the format: <FString>: "x y z zoom\n"

	It returns a TArray<Fvector> with every "xyz" tha it's received.
	It also returns a TArray<float> with all the zooms that are received

	Each element "i" of allVecs corresponds with the same element "i" of allZooms
*/
void AControladorServer::getLastVectors( TArray<FVector> & allVecs, TArray<float> & allZooms)
{
	//Declaration of all the variables
	TArray<FString> msg;
	FString ls;
	FVector *v;
	float xf, yf, zf, zoomf;
	FString xs, ys, zs,zooms, aux1, aux2;


	
	if (theServer) {

		//Get the last message of the server
		msg = theServer->getLastMessage();
		
		//If there are messages
		if(msg.Num() > 0)
			//For each message it's transformed into a FVector
			for (int i = 0; i < msg.Num(); i++) {

				v = new FVector();

				ls = msg[i];
				//We get every value by spliting the FString
				ls.Split(" ", &xs, &aux1);
				aux1.Split(" ", &ys, &aux2);
				aux2.Split(" ", &zs, &zooms);

				xf = FCString::Atof(*xs);
				yf = FCString::Atof(*ys);
				zf = FCString::Atof(*zs);
				zoomf = FCString::Atof(*zooms);


				v->Component(0) = xf;
				v->Component(1) = yf;
				v->Component(2) = zf;

				//Add the FVector and the zoom to the TArray
				allVecs.Add(*v);
				allZooms.Add(zoomf);
			}

	}

}

/*
	This method is used in the "Axis rotation interaction".
	It returns a TArray<float> with all the values of the axis Y of the sensor of the phone.
	It also returns a TArray<Fstring> with the axis

	The messages have to be sent in the format: <FString>: "Y Axis"

	Axis needs to be one of these values: "X","Y","Z","NONE"

	Each element "i" of allVal corresponds with the same element "i" of allAxis

*/
void AControladorServer::getLastAxis(TArray<float> & allVal, TArray<FString> & allAxis)
{

	//Declaration of the variables
	TArray<FString> msg;
	FString ls;
	float yf;
	FString Axiss, ys;


	if (theServer) {

		//Get the last message of the server
		msg = theServer->getLastMessage();

		//If we received messages
		if (msg.Num() > 0)
			//Each message it's added to the right TArray
			for (int i = 0; i < msg.Num(); i++) {

				ls = msg[i];

				ls.Split(" ", &Axiss, &ys);
				yf = FCString::Atof(*ys);
				
				allVal.Add(yf);
				allAxis.Add(Axiss);
			}

	}

}

/*
	This method is used in the "Full interaction method".
	It returns:

	valR: TArray<FVector> where all the "xyz" values sent by the right device are stored
	valL: TArray<FVector> where all the "xyz" values sent by the left device are stored
	handR: TArray<bool> where each element "i" is true if there is a message in the element "i" of valR
	handL: TArray<bool> where each element "i" is true if there is a message in the element "i" of valL
	recibido: bool that is true if there is at least one message

	The messages needs to be sent in JSON format in the next way:
	{
		"getL":
		{
			"hand":"L",
			"x":"value_x_L",
			"y":"value_y_L",
			"z":"value_z_L"
		},
		"getR":
		{
			"hand":"R",
			"x":"value_x_R",
			"y":"value_y_R",
			"z":"value_z_R"
		}

	}

*/
void AControladorServer::getLastJSONLR(TArray<FVector>& valR, TArray<FVector>& valL, TArray<bool> & handR, TArray<bool> & handL, bool & recibido)
{
	TArray<FString> msgs;
	
	recibido = false;

	if (theServer) {

		
		msgs = theServer->getLastMessage();
		
		//If there are messages
		if (msgs.Num() > 0) {
			
			//For each message
			for (int i = 0; i < msgs.Num(); i++) {
				
				FString msg = msgs[i];
				TSharedPtr<FJsonObject> JsonObject;
				TSharedRef< TJsonReader<> > Reader = TJsonReaderFactory<>::Create(msg);

				//Create the JSONObject from the received String
				if (FJsonSerializer::Deserialize(Reader, JsonObject)) {

					//Create the JSON subobjects for each device
					auto JsonR = JsonObject->GetObjectField("getR");
					auto JsonL = JsonObject->GetObjectField("getL");

					//Vectors with the values from the messages
					FVector *vecR = new FVector(); 
					FVector *vecL = new FVector();

					//For each device, if it exists, we get the data and add it to the vectors
					if (JsonR->GetStringField("hand").Equals("R")) {
						
						vecR->Component(0) = JsonR->GetNumberField("x");
						vecR->Component(1) = JsonR->GetNumberField("y");
						vecR->Component(2) = JsonR->GetNumberField("z");
						
						handR.Add(true);
					}
					else
						//It's importan to add values to the vectors even though we haven't receive data from a device so that we assort the indices when we return the vectors
						handR.Add(false);

					valR.Add(*vecR);

					if (JsonL->GetStringField("hand").Equals("L")) {
						vecL->Component(0) = JsonL->GetNumberField("x");
						vecL->Component(1) = JsonL->GetNumberField("y");
						vecL->Component(2) = JsonL->GetNumberField("z");
						handL.Add(true);
					}
					else
						handL.Add(false);

					valL.Add(*vecL);

					recibido = true;

				}
			}
		}
		
	}

}

/*
	Method to init the server listening to the given port
*/
void AControladorServer::Init(const int port)
{
	theServer = new AServerThread(port);
}

/*
	Method to transform an FVector of rotations into an FRotator
	Used in "Free rotation interaction"
*/
FRotator AControladorServer::getNewRotation(const FVector & rads)
{

	//Components of rads
	float rads_x = rads.Component(0);
	float rads_y = rads.Component(1);
	float rads_z = rads.Component(2);



	//Transformation to the new rotation 
	FRotator rot_out;
	rot_out.Roll = radsToDeg(rads_x); 
	rot_out.Pitch = -radsToDeg(rads_y);//Rotations on the device are not the same in unreal so we need to change some values
	rot_out.Yaw = -radsToDeg(rads_z);


	return rot_out;
}

/*
	Method that receives a value of radiasn/second and returns the rotation to apply to the model
	Used in "Axis rotation interaction"
*/
float AControladorServer::getNewAxisRotation(const float & rads)
{
	float r = rads;
	return radsToDeg(r);
}

/*
	This method interpolates a values of radians/second into a degree of rotation to the model
*/
float AControladorServer::radsToDeg(float & rads)
{
	//Variable to check if randias are negative
	bool negativo = false;

	if (rads < 0) {
		negativo = true;
		rads *= -1;	//We set the radians to possitive for its use in the interpolation
	}

    //Degrees
	float deg = 0;

	//Movement filter
	if (rads > 0.5) {

		if (rads < 1)
			deg = 1;
		else if (rads >= 1 && rads <= 4)
			deg = 1.5 * rads - 0.5; //Interpolation function for 1 <= x <= 4
		else if (rads > 4)
			deg = rads + 4;			//Interpolation function for 4 < x


	}
	if (negativo)				//If we received negative radians we change back the degrees to negative
		deg *= -1;

	

	return deg;

}





package com.example.adrin.tfginteraccion;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Utilities.MessageSender;


/**
 * Main activity of the application, here it can be found the buttons to launch the different interaction
 * activities with the 3D model.
 *
 * This activity has a test feature to send messages to the server with the IP that is in the text box. (This feature is deprecated)
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //Sensor manager of the device
    private SensorManager mSensorManager;
    //Text areas where the information is shown
    TextView txtX, txtY, txtZ, txtZoom;
    //Current zoom of the model
    int n_zoom = 0;
    //"y" coordinate that is obtained by pressing the zoom button
    int ZoomButtonY = -1;
    //Object that sends messages to the server
    private MessageSender sender;
    //String where is stored the Ip inserted by the user
    private String IPimput;

    //State variables for the buttons
    boolean auto;


    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    /////////////////////// on Create //////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Access to the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //textView initialization of the activity
        txtX = (TextView)findViewById(R.id.xVar);
        txtY = (TextView)findViewById(R.id.yVar);
        txtZ = (TextView)findViewById(R.id.zVar);
        txtZoom = (TextView)findViewById(R.id.ZoomVar);
        txtZoom.setText(String.valueOf(n_zoom+"%"));

        //Initially auto is set on false
        auto = false;

        //messageSender initialization
        IPimput = "192.168.1.39";
        sender = new MessageSender();
        String args[] = {IPimput,"7800"};
        //sender.execute(args); //Uncomment if you want to use the main activity as a test to send messages


        /**
         * AUTO button of the activity
         *
         * When pressed, if sender is connected to the server and is active, this button
         * will automatically send messages to the server with the gyroscope's information to the server.
         *
         * When pressed again, the sender will stop sending messages.
         */
        final Button AutoButton = findViewById(R.id.Auto_button);
        AutoButton.setBackgroundColor(Color.GRAY);
        AutoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Get information of the indicated IP
                IPimput = ((TextView) findViewById(R.id.IPimput)).getText().toString();

                //If auto mode is enabled it will set auto mode to false
                if(auto) {
                    auto = false;
                    AutoButton.setBackgroundColor(Color.GRAY);
                }
                /*
                    If auto mode is false it will set it to true, stop the previous sender and start a new one
                    that will send continuously messages to the server
                */
                else {
                    auto = true;
                    AutoButton.setBackgroundColor(Color.BLUE);
                    sender.stop();
                    sender = new MessageSender();
                    String args[] = {IPimput,"7800"};
                    sender.execute(args);
                }


            }
        });

        /**
         * TEST button of the activity
         *
         * When pressed, if sender is connected to the server and is active, this button
         * will stop any sender if exists and create a new one just send a test message
         */
        final Button SendButton = findViewById(R.id.Send_button);
        SendButton.setBackgroundColor(Color.GRAY);
        SendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(auto) {
                    auto = false;
                    AutoButton.setBackgroundColor(Color.GRAY);

                }
                sender.stop();
                IPimput = ((TextView)findViewById(R.id.IPimput)).getText().toString();
                //Send a test message to the server
                MainActivity.sendTest(IPimput);

            }
        });
        /**
         * USER FREE Button
         *
         * This button launches an activity to freely rotate the model by pressing one button
         */
        final Button UserFreeButton = findViewById(R.id.User_free_button);
        UserFreeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intent = new Intent(MainActivity.this, UserActivityFree.class);
                IPimput = ((TextView)findViewById(R.id.IPimput)).getText().toString();
                intent.putExtra("IP", IPimput);
                sender.stop();
                startActivity(intent);

            }

        });

        /**
         * USER AXIS Button
         *
         * This button launches an activity to rotate the model in one of its axis
         */
        final Button UserAxisButton = findViewById(R.id.User_axis_button);
        UserAxisButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intent = new Intent(MainActivity.this, UserActivityAxis.class);
                IPimput = ((TextView)findViewById(R.id.IPimput)).getText().toString();
                intent.putExtra("IP", IPimput);
                sender.stop();
                startActivity(intent);

            }

        });

        /**
         * JSON button
         *
         * This button launches an activity just to try the JSON encoding feature of the app
         */
        final Button JSONButton = findViewById(R.id.JSON_button);
        JSONButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){


                Intent intent = new Intent(MainActivity.this, JSONActivity.class);
                IPimput = ((TextView)findViewById(R.id.IPimput)).getText().toString();
                intent.putExtra("IP", IPimput);
                sender.stop();
                startActivity(intent);

            }

        });

        /**
         * LR Button
         *
         * This button launches an activity that lets you choose the device you are using to control the
         * model (Left or Right)
         */
        final Button LRButton = findViewById(R.id.LR_button);
        LRButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intent = new Intent(MainActivity.this, ChooseLRActivity.class);
                IPimput = ((TextView)findViewById(R.id.IPimput)).getText().toString();
                intent.putExtra("IP", IPimput);
                sender.stop();
                startActivity(intent);

            }

        });


    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //////////////////// Sensor methods ////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////


    /**
     * Method to start and register the sensors on the manager
     */
    private void Ini_Sensores() {

        //We only need to register the gyroscope
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

    }

    /**
     * Method to stop listening to the sensors
     */
    private void Parar_Sensores() {

        mSensorManager.unregisterListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));


    }

    /**
     * Method that is triggered when the sensor's data is changed
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {

                //If the sensor that triggers this method is the gyroscope
                case Sensor.TYPE_GYROSCOPE:
                    //Only if auto mode is enabled it will send the new data to the server
                    if(auto)
                        //Format: gyroscope.x gyroscope.y gyroscope.z zoom
                        this.send(event.values[0] + " " + event.values[1] + " " + event.values[2]+ " " + n_zoom + "\n");

                    //Update the information showed on screen
                    txtX.setText(String.valueOf(event.values[0]));
                    txtY.setText(String.valueOf(event.values[1]));
                    txtZ.setText(String.valueOf(event.values[2]));

                    break;


            }
        }
    }

    /**
     * Method that is triggered when the accuracy of the sensor is changed
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Method that is triggered when the sensors are told to stop
     */
    @Override
    protected void onStop() {

        Parar_Sensores();

        super.onStop();
    }

    /**
     * Method that is triggered when the activity is finished
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        Parar_Sensores();

        super.onDestroy();
    }

    /**
     * Method that is triggered when the activity is paused
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        Parar_Sensores();

        super.onPause();
    }

    /**
     * Method that is triggered when the activity is restarted
     */
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub

        Ini_Sensores();

        super.onRestart();
    }

    /**
     * Method that is triggered when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        Ini_Sensores();

    }

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //////////////////// Sender methods ////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    /**
     * Method to send a message to the sender
     * @param msg
     */
    public void send( String msg ){

        sender.send(msg);

    }

    /**
     * Method to create a sender just to send a test message to the server
     * @param ip
     */
    static void sendTest(String ip){


        MessageSender sender2 = new MessageSender();

        String []args = {ip,"7800"};
        sender2.execute(args);
        sender2.sendAndTerminate("TEST MESSAGE\n");



    }
}




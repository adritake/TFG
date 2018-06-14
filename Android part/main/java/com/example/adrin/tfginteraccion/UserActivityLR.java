package com.example.adrin.tfginteraccion;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import Utilities.JSONUtilities;
import Utilities.MessageReceiver;
import Utilities.MessageSender;

public class UserActivityLR extends AppCompatActivity implements SensorEventListener {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //IP of the server
    private String IP_server;
    //IP of the intermediary
    private String IP_inter;
    //Kind of device
    private String hand;
    //Sensor manager of the device
    private SensorManager mSensorManager;
    //Object that sends messages to the server
    private MessageSender sender;
    //Object that receives messeges from the other device (only used when it's the intermediary)
    private MessageReceiver receiver;
    //State variable to control if information is sended
    private boolean sendingRotation;

    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_lr);

        // Access to the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Get the hand (L or R)
        Intent intent = getIntent();
        hand = intent.getStringExtra("hand");

        //Message sender initialization
        this.IP_server = intent.getStringExtra("IP");
        this.IP_inter = intent.getStringExtra("IP_inter");
        sender = new MessageSender();


        //Create the sender wheter if it's going to send to the server or the intermediary
        String args[] = {IP_inter,"6800"}; //Intermediary
        if( hand.equals("R")) {            //server
            args[0] = IP_server;
            args[1] = "7800";
        }

        //Checking version to see if thread pool is available and execute the sender on the pool
        // Thread pool is neede because an AsyncTask can only be run one per execution
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,args);
        else
            sender.execute(args);



        //Creation and initialization of the receiver if the device is the intermediary (R)
        if(hand.equals("R")) {
            receiver = new MessageReceiver();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                receiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
            else
                receiver.execute();
        }

        //Initially it can't send the rotation until the button is pressed
        sendingRotation = false;

        /**
         * ROTATION Button
         * When pressed it will send the rotation to the right device
         */
        final ImageButton RotationButton = findViewById(R.id.LRRotationButton);
        RotationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        sendingRotation = true;
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        sendingRotation = false;
                        return true; // if you want to handle the touch event
                }
                return true;
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
    protected void Ini_Sensores() {

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
     * In this activity the behaviour of this method changes depending on the kind of device(L or R)
     * If it's L device it will always send data to R device when the button is pressed
     * If it's R device it will send data to the server whenever receives data from L or the button is pressed.
     * In the case of R button is pressed and is simultaneously receiving data from L it will append R data to
     * L data and send it to the server
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {

                case Sensor.TYPE_GYROSCOPE:

                    //Initialization of the JSONObject of the phone
                    JSONObject local_values = JSONUtilities.toJSONOBjectMovil(hand, Float.toString(event.values[0]),Float.toString(event.values[1]),Float.toString(event.values[2]));
                    //Message to send
                    String msg_env;
                    //Message to receive
                    String msg_rec = null;
                    //JSONObject to receive
                    JSONObject json_rec = null;

                    //If the button is being pressed
                    if(sendingRotation) {

                        //Intermediary case
                        if( hand.equals("R") ) {
                            //We try to receive from L
                            msg_rec = receiver.getMessage();
                            //If we have received a message from L
                            if(msg_rec != null) {
                                try {
                                    json_rec = new JSONObject(msg_rec);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //We create a JSONObject with both JSONObjects from L and R and parse it to String
                                msg_env = JSONUtilities.toJSONOBjectEnvolvente(json_rec,local_values).toString() + "\n";

                            }
                            //If we haven't receive a message from L
                            else
                                //We create a JSONObject with only R and parse it to String
                                msg_env = JSONUtilities.toJSONOBjectEnvolvente(null,local_values).toString() + "\n";
                        }
                        //Not the intermediary
                        else
                            //The message will be only the L JSONObject parsed to String
                            msg_env = local_values.toString() + "\n";


                        //We send whether the R message or L+R message to the intermediary or to the server
                        this.send(msg_env);



                    }
                    //The button is not pressed but we try to receive data from L anyway
                    else if ( hand.equals("R") ){

                        msg_rec = receiver.getMessage();
                        //If we receive a message from L
                        if(msg_rec != null) {

                            try {
                                json_rec = new JSONObject(msg_rec);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //We create a JSONObject with only L and parse it to String
                            msg_env = JSONUtilities.toJSONOBjectEnvolvente(json_rec,null).toString() + "\n";
                            //Send message to the server
                            this.send(msg_env);
                        }

                    }



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

}

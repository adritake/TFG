package com.example.adrin.tfginteraccion;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import Utilities.MessageSender;


/**
 * This class will control the rotation of the object.
 * It has 3 button, one for each axis. Whenever a button is pressed and hold
 * it will send the pressed axis and the information of the gyroscope in the y
 * axis.
 */
public class UserActivityAxis extends AppCompatActivity implements SensorEventListener {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //IP received from the main activity
    private String IP;
    //Sensor manager of the device
    private SensorManager mSensorManager;
    //Object that sends messages to the server
    private MessageSender sender;
    //String where the pressed axis is stored ("X","Y","Z","NONE")
    private String sendingAxis;


    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_axis);

        // Access to the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        //messageSender initialization
        Intent intent = getIntent();
        this.IP = intent.getStringExtra("IP");
        sender = new MessageSender();
        final String args[] = {IP,"7800"};
        sender.execute(args);

        //The default axis is NONE. It will change to an actual axis when a button is pressed
        // it will go back to NONE when the button is released.
        sendingAxis="NONE";


        /*
            AXIS BUTTONS
            When a button is pressed and hold it will change the current axis and
            information of the gyroscope will be send to the server with the axis.
         */

        final Button XAxisButton = findViewById(R.id.X_axis_button);
        XAxisButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        sendingAxis = "X";
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        sendingAxis = "NONE";
                        return true; // if you want to handle the touch event
                }
                return true;
            }

        });

        final Button YAxisButton = findViewById(R.id.Y_axis_button);
        YAxisButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        sendingAxis = "Y";
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        sendingAxis = "NONE";
                        return true; // if you want to handle the touch event
                }
                return true;
            }

        });


        final Button ZAxisButton = findViewById(R.id.Z_axis_button);
        ZAxisButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        sendingAxis = "Z";
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        sendingAxis = "NONE";
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
     * It will always send the data even if a button is not pressed because
     * the server needs to know when the buttons are not pressed. So, when
     * no button is pressed it will send NONE + Gyroscope.y
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {

                case Sensor.TYPE_GYROSCOPE:


                    //Format: Axis Gyroscope.y
                    this.send(sendingAxis + " " + event.values[1] + "\n");


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

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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import Utilities.MessageSender;

/**
 * Class that controls the rotation of the model by pressing one button. It also zooms in and
 * out on the model.
 */
public class UserActivityFree extends AppCompatActivity implements SensorEventListener {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //IP received from the main activity
    private String IP;
    //Current zoom of the object
    private int n_zoom;
    //Textview where the current zoom is showed
    TextView txtZoom;
    //Sensor manager of the device
    private SensorManager mSensorManager;
    //Object that sends messages to the server
    private MessageSender sender;
    //State variable that controls whether information can be sent or not
    private boolean sendingRotation;

    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_free);

        // Access to the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //messageSender initialization
        Intent intent = getIntent();
        this.IP = intent.getStringExtra("IP");
        sender = new MessageSender();
        final String args[] = {IP,"7800"};
        sender.execute(args);

        //Initialization of state and control variables
        txtZoom = (TextView)findViewById(R.id.ZoomText);
        n_zoom = 0;
        sendingRotation = false;

        /**
         * Zoom seekbar. It controls the zoom of the object by dragging the button of
         * the bar-
         */
        final SeekBar ZoomBar = findViewById(R.id.ZoomBar);
        ZoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){


            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                n_zoom = i;
                txtZoom.setText(String.valueOf(i)+"%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * ROTATION Button
         * Whenever this button is pressed and hold it will send information to the server
         */
        final ImageButton RotationButton = findViewById(R.id.RotationButton);
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
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            Log.d("sensor", event.sensor.getName());

            switch (event.sensor.getType()) {

                case Sensor.TYPE_GYROSCOPE:
                    if(sendingRotation)
                        //Formato: Giroscopio.x Giroscopio.y Giroscopio.z Zoom
                        this.send(event.values[0] + " " + event.values[1] + " " + event.values[2]+ " " + n_zoom + "\n");

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

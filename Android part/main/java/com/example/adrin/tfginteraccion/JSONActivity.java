package com.example.adrin.tfginteraccion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import Utilities.JSONUtilities;
import Utilities.MessageSender;


/**
 * This class is only used to try my own JSON methods and send messages to the server
 */
public class JSONActivity extends AppCompatActivity {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //Sensor manager of the device
    private MessageSender sender;
    //IP received from the main activity
    private String IP;
    //JSON Object where all the information will be stored
    private JSONObject msg_json;


    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);


        //messageSender initialization
        Intent intent = getIntent();
        this.IP = intent.getStringExtra("IP");
        sender = new MessageSender();
        final String args[] = {IP,"7800"};
        sender.execute(args);

        /**
         * SEND Button
         * When pressed it will send the JSON object in its String form
         */
        final Button SendJSONButton = findViewById(R.id.Send_JSON_button);
        SendJSONButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                send(msg_json.toString());

            }

        });

        /*
            Example of using the JSON methods with random values.
            The way I store the information in the object is the next one:

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
        JSONObject movilL = JSONUtilities.toJSONOBjectMovil("L", String.valueOf(1.1),String.valueOf(1.2),String.valueOf(1.3));
        JSONObject movilR = JSONUtilities.toJSONOBjectMovil("R", String.valueOf(2.1),String.valueOf(2.2),String.valueOf(2.3));
        msg_json = JSONUtilities.toJSONOBjectEnvolvente(movilL,movilR);

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

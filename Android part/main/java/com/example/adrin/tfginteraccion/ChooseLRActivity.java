package com.example.adrin.tfginteraccion;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * This class lets you choose if the device is going to ve the Left device or Right device.
 * The left device will send data to the right device while the right device will receive data
 * from the left device and send data to the server.
 *
 * Way to use this activity:
 *  - It needs the IP of the server which will be indicated in the main activity
 *  - If it's the left device you need to write the local IP of the right device and then press left button (the local IP of the device itself is shown on the screen)
 *  - If it's the right device you just need to press right button
 */
public class ChooseLRActivity extends AppCompatActivity {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //IP received from the main activity
    private String IP;
    //Local IP of the device itself
    private String IP_privada;
    //Local IP of the device of the intermediary
    private String IP_inter;

    /**
     *  Actions to do when the activity starts
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lr);

        //Get the server IP to use it in the next activity
        Intent intent = getIntent();
        this.IP = intent.getStringExtra("IP");


        //Get the local IP of the phone
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        this.IP_privada = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        /**
         * L Button
         * It will launch the LRActivity with an extra String called hand set to "L"
         * and the IP of the intermediary if needed
         */
        final Button LButton = findViewById(R.id.L_button);
        LButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intent = new Intent(ChooseLRActivity.this, UserActivityLR.class);

                intent.putExtra("IP", IP);
                intent.putExtra("hand", "L");
                IP_inter = ((TextView) findViewById(R.id.IP_input_inter)).getText().toString();
                intent.putExtra("IP_inter", IP_inter);
                startActivity(intent);

            }

        });

        /**
         * R Button
         * It will launch the LRActivity with an extra String called hand set to "R"
         * and the IP of the intermediary if needed
         */

        final Button RButton = findViewById(R.id.R_button);
        RButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                Intent intent = new Intent(ChooseLRActivity.this, UserActivityLR.class);

                intent.putExtra("IP", IP);
                intent.putExtra("hand", "R");
                IP_inter = ((TextView) findViewById(R.id.IP_input_inter)).getText().toString();
                intent.putExtra("IP_inter", IP_inter);
                startActivity(intent);

            }

        });

        //Show on the text view the local IP of the phone
        final TextView IPPrivadaVar = findViewById(R.id.IP_privada_textView_var);
        IPPrivadaVar.setText(IP_privada);



    }
}

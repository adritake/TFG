package Utilities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.*;

/**
 * Class in charge of creating a socket to listen if we receive data from another device
 */

public class MessageReceiver extends AsyncTask<Void, Void, Void> {


    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //State variable
    private boolean running;

    //Listening socket
    private ServerSocket ss;
    //Connection socket
    private Socket s;
    //Stream reader
    private InputStreamReader isr;
    //Buffer reader
    private BufferedReader br;
    //Message received
    private String message;

    /**
     * Constructor of MessageReceiver
     * Initially running is set to true and message is null in order to show
     * that no message has been received yet
     */
    public MessageReceiver(){
        running = true;
        message = null;
        Log.i("I","Constructor socket receiver");

    }
    /**
     * Process to do on the thread. It will be started when the start method of the object is called
     * @param voids Array of string that contains the information needed when the thread is started.
     */
    @Override
    protected Void doInBackground(Void... voids){

        try {
            Log.i("I","Creting socket receiver");
            ss = new ServerSocket(6800);
            Log.i("I","Socket receiver created");
            s = ss.accept();
            Log.i("I","New conection in socket receiver");
            isr = new InputStreamReader(s.getInputStream());
            br = new BufferedReader(isr);

            while (running) {

                message = br.readLine();

            }


            s.close();
            ss.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * Method that returns the last message received if so
     * @return String if a message is received and null otherwise
     */
    public String getMessage(){

        String ret_msg = message;
        message = null;
        return ret_msg;

    }

    @Override
    protected void onProgressUpdate(Void... Voids){

        super.onProgressUpdate();

    }

    /**
     * Method that stops the execution of the receiver
     */
    public void stop(){

        running = false;

    }


}

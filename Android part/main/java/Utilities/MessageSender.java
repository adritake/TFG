package Utilities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Class in charge of creating a socket to connect to the server and send messages to it
 * whenever requested.
 * A thread will be created where the sender will be running
 */

public class MessageSender extends AsyncTask<String, String, Void> {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////// Activity variables //////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    //Socket that create a connection to the requested IP and port
    private Socket s;
    //Object in charge to write data on the output stream
    private PrintWriter pw;
    //State variables
    private boolean canSend ;
    private boolean running;
    //Message to send
    String msg;

    /**
     * Constructor of MessageSender
     * Initially it will set to true the running state but it won't be able to send messages
     */
    public MessageSender(){

        canSend = false;
        running = true;

        //Initially the message is set to "fail" just to test if the object has received properly the message to send
        msg = "fail";
    }

    /**
     * Process to do on the thread. It will be started when the start method of the object is called
     * @param voids Array of string that contains the information needed when the thread is started.
     *              voids[0] = IP
     *              voids[1] = port
     */

    @Override
    protected Void doInBackground(String... voids){

        //Get the IP and port from the parameter
        String ip = voids[0];
        int port = Integer.parseInt(voids[1]);

        try{
            Log.i("I","Creando socket");
            s = new Socket(ip, port);
            Log.i("I","Socket creado, conectado a " + ip + " " + port);
            pw = new PrintWriter(s.getOutputStream());

            //Loop that will run while running == true
            while( running ){

                //It will only send a single message when the send method is called
                if(canSend){

                    pw.write(msg);
                    pw.flush();

                    canSend = false;
                }
            }

            //When it's told to stop it will close all object created
            pw.write("Disconnected");
            pw.flush();
            pw.close();
            s.close();

        }
        catch (IOException e){

            e.printStackTrace();
        }

        return null;
    }

    /**
     * Similar method to send, we will use the send method since is more intuitive
     * @param progress
     */
    @Override
    protected void onProgressUpdate(String... progress){

        this.msg = progress[0];
        canSend = true;

    }

    /**
     * Send method
     * When this method is called it will set the msg variable with new content and
     * set the canSend state to true
     * @param msg message to send to the server
     */
    public void send( String msg ){

        this.msg = msg;
        canSend = true;

    }

    /**
     * This method will send a message to the server and then stop the object
     * It is only used in debug mode.
     * @param msg message to send to the server
     */
    public void sendAndTerminate( String msg ){

        this.msg = msg;
        canSend = true;
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.stop();

    }

    /**
     * Method to stop the sender
     */
    public void stop(){
        running = false;
    }
}
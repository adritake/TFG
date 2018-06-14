package Utilities;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that contains all the methods I use to store the information in JSON Objects
 */

public class JSONUtilities {

    // Funcion que crea un objeto JSON formado por un array formado por los datos de ambos moviles

    /**
     * Functions that create the main JSON Object by putting togheter the information of both devices
     * @param L JSON Object with the information of the L device
     * @param R JSON Object with the information of the R device
     * @return JSON Object that contains both L and R JSON objects in this format
     *
     *          {
     *              "getL": L,
     *              "getR": R
     *          }
     */
    public static JSONObject toJSONOBjectEnvolvente(JSONObject L, JSONObject R){

        JSONObject ret = new JSONObject();


        try {
            ret.put("getL",L);
            ret.put("getR",R);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;

    }

    // Funcion que crea un objeto JSON formado por los datos de un movil

    /**
     * Function that create a JSON Object with the data of a device
     * @param hand Wether if the device is L or R
     * @param x value of x axis  of the sensor
     * @param y value of y axis  of the sensor
     * @param z value of z axis  of the sensor
     * @return  JSONObject that contains all the information needed of a device with the next format:
     *          {
                    "hand":"L",
                    "x":"value_x_L",
                    "y":"value_y_L",
                    "z":"value_z_L"
                }
     */
    public static JSONObject toJSONOBjectMovil(String hand, String x, String y, String z ){

        JSONObject ret = new JSONObject();

        try {
            ret.put("hand", hand);
            ret.put("x",    x);
            ret.put("y",    y);
            ret.put("z",    z);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return ret;

    }



}

package com.sidharth.hellokpa;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sidharth.hellokpa.dummy.Contact;
import com.sidharth.hellokpa.dummy.Notification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sidharthvinod on 26/03/17.
 */

public class DataHandler {


    public static void writeData(String data,String FILENAME){

        FileOutputStream fos = null;
        try {
            fos = MainActivity.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readData(String FILENAME) {
        String ret = "";

        try {
            InputStream inputStream = MainActivity.getContext().openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void getHash(final String type) {
        String URL = "http://kpa.info.tm/";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL+type, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("HAI",response.toString()+MainActivity.hashes.getString(type,""));
                try {
//                        Toast.makeText(MainActivity.getContext(),
//                                response, Toast.LENGTH_LONG).show();
//                    downloadContacts();
                    if(!MainActivity.hashes.getString(type,"").equals(response.toString())){
                        if(type=="phone")
                            downloadContacts(response.toString());
                        else
                            downloadNotifications(response.toString());
                    }
                }
                catch (Exception e) {
//                    Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
//                    MainActivity.snack("No internet connection");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, "req");
    }

    private static void downloadNotifications(final String hash) {
        String URL = "http://kpa.info.tm/notif/data/";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("NOTIF",response.toString());
                try {
                    MainActivity.snack("New notifications");
                    writeData(response.toString(),"notifications.json");
                    MainActivity.editor.putString("notif",hash);
                    MainActivity.editor.commit();
                    Notification.updateData();
                }
                catch (Exception e) {
//                    Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, "req");
    }

    private static void downloadContacts(final String hash) {
        String URL = "http://kpa.info.tm/phone/data/";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("HAI",response);
                try {
                    MainActivity.snack("Updated contacts");
//                    Toast.makeText(MainActivity.getContext(), "Updated contacts", Toast.LENGTH_LONG).show();
                    writeData(response.toString(),"contacts.json");
                    MainActivity.editor.putString("phone",hash);
                    MainActivity.editor.commit();
                    Contact.updateData();
                }
                catch (Exception e) {
//                    Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.getContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, "req");
    }

    public static void checkUpdate(){
        Log.e("HAI","Update Checked");
        getHash("phone");
        getHash("notif");
    }
}

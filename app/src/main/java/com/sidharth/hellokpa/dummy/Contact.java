package com.sidharth.hellokpa.dummy;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sidharth.hellokpa.DataHandler;
import com.sidharth.hellokpa.MainActivity;
import com.sidharth.hellokpa.MyContactRecyclerViewAdapter;
import com.sidharth.hellokpa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Contact {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ContactItem> ITEMS = new ArrayList<ContactItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
//    public static final Map<String, ContactItem> ITEM_MAP = new HashMap<String, ContactItem>();

    static {
//        InputStream is = MainActivity.getContext().getResources().openRawResource(R.raw.data);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        String jsonString = writer.toString();
//        Log.d("JSON",jsonString);
//
//        try {
//            JSONObject data=new JSONObject(jsonString);
//            Log.d("JSON", data.getString("hash"));
//            JSONArray cts = data.getJSONArray("data");
//            for(int i=0;i<cts.length();i++){
//                JSONObject p = cts.getJSONObject(i);
//                ITEMS.add(new ContactItem(p.getString("name"),p.getString("desig"),p.getString("phone"),p.getInt("category")));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ITEMS.add(new ContactItem("a","b","c",1));
//        updateData();
    }

    public static void updateData(){
        try {
            String jsonString = DataHandler.readData("contacts.json");
            Log.d("JSON read",jsonString);
            ITEMS.clear();
//            JSONObject data=new JSONObject(jsonString);
//            Log.d("JSON", data.getString("hash"));
//            Log.d("JSON", String.valueOf(String.valueOf(ITEMS.size())));
            JSONArray cts = new JSONArray(jsonString);
            for(int i=0;i<cts.length();i++){
                JSONObject p = cts.getJSONObject(i).getJSONObject("fields");
//                Log.d("JSON name",p.getString("name"));
                try{
                    ITEMS.add(new ContactItem(p.getString("name"),p.getString("rank"),p.getString("phone1"),p.getString("wing"), p.getInt("category_code"), p.getInt("sino")));
                }catch (Exception e){
                    e.printStackTrace();
                }
//                Log.d("JSON", String.valueOf(ITEMS.size()));
            }
            MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.updateData(new ArrayList<>(ITEMS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class ContactItem implements Comparable{
        public final String name;
        public final String designation;
        public final String phone;
        public final String wing;
        public final int category;
        public final int sino;

        public ContactItem(String name, String designation, String phone, String wing, int category, int sino) {
            this.name = name;
            this.designation = designation;
            this.phone = phone;
            this.wing = wing;
            this.category = category;
            this.sino = sino;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            ContactItem c = (ContactItem) o;
            return this.sino-c.sino;//this.name.compareTo(c.name);
        }
    }
}

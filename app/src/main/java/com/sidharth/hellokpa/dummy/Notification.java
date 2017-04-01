package com.sidharth.hellokpa.dummy;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sidharth.hellokpa.DataHandler;
import com.sidharth.hellokpa.MyContactRecyclerViewAdapter;
import com.sidharth.hellokpa.MyNotificationRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Notification {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<NotificationItem> ITEMS = new ArrayList<NotificationItem>();


    public static void updateData(){
        try {
            String jsonString = DataHandler.readData("notifications.json");
            Log.d("JSON read",jsonString);
            ITEMS.clear();
//            JSONObject data=new JSONObject(jsonString);
//            Log.d("JSON", data.getString("hash"));
//            Log.d("JSON", String.valueOf(String.valueOf(ITEMS.size())));
            JSONArray cts = new JSONArray(jsonString);
            for(int i=0;i<cts.length();i++){
                JSONObject p = cts.getJSONObject(i).getJSONObject("fields");
                ITEMS.add(new Notification.NotificationItem(cts.getJSONObject(i).getInt("pk"),p.getString("title"),p.getString("text")));
                Log.d("JSON", String.valueOf(ITEMS.size()));
            }
            MyNotificationRecyclerViewAdapter.myNotificationRecyclerViewAdapter.updateData(new ArrayList<>(ITEMS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class NotificationItem implements Comparable{
        public final int id;
        public final String title;
        public final String text;

        public NotificationItem(int id, String title, String text) {
            this.id = id;
            this.title = title;
            this.text = text;
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            NotificationItem n = (NotificationItem) o;
            return id<n.id?1:(id==n.id)?0:-1;
        }
    }
}

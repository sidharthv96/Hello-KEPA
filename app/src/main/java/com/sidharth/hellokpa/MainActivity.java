package com.sidharth.hellokpa;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sidharth.hellokpa.dummy.Category;
import com.sidharth.hellokpa.dummy.Contact;
import com.sidharth.hellokpa.dummy.Notification;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ContactFragment.OnListFragmentInteractionListener, NotificationFragment.OnListFragmentInteractionListener, CategoryFragment.OnListFragmentInteractionListener {

    private static final String TAG = "MAINACTIVITY";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static Context mContext;
    private static View mainView = null;
    private static int category = 0;
    public static final String PREFS_NAME = "UpdateHashes";
    public static SharedPreferences hashes;
    public static SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mainView = fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Showing all contacts", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(0);
                fab.hide();
            }
        });
        fab.hide();
        hashes = getSharedPreferences(PREFS_NAME, 0);
        editor = hashes.edit();
        DataHandler.checkUpdate();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(),user);
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        DataHandler.checkUpdate();
        Intent i =getIntent();
        if(i.hasExtra("TAB")){
            final TabLayout host = (TabLayout) findViewById(R.id.tabs);
            host.getTabAt(2).select();
        }
    }

    private void sendRegistrationToServer(final String token, final FirebaseUser user) {
        String URL = "http://kpa.info.tm/register/device/";
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

//        Log.d(TAG,token);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("HAI",response.toString());
//                try {
//                    String url = Uri.parse("http://sid.qpha.org/register/login/")
//                            .buildUpon()
//                            .appendQueryParameter("email", user.getEmail())
//                            .appendQueryParameter("uid", user.getUid())
//                            .build().toString();
//                    wv.loadUrl(url);
////                    Toast.makeText(getApplicationContext(),
////                            response, Toast.LENGTH_LONG).show();
//                }
//                catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network Error! Try Again!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("aid",android_id);
//                params.put("email",user.getEmail());
//                params.put("uid",user.getUid());
//                params.put("name",user.getDisplayName());
//                params.put("photo",user.getPhotoUrl().toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "req");
    }

    public static Context getContext(){
        return mContext;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();;
        final TabLayout host = (TabLayout) findViewById(R.id.tabs);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Toast like print
//                    UserFeedback.show( "SearchOnQueryTextSubmit: " + query);
                    MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(query);
                    if( ! searchView.isIconified()) {
                        searchView.setIconified(true);
                    }
                    searchItem.collapseActionView();
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    host.getTabAt(1).select();
                    MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(s);
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,AdminActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Contact.ContactItem item) {
//        Toast.makeText(this,item.phone,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+item.phone));
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Notification.NotificationItem item) {
//        Toast.makeText(this,item.title,Toast.LENGTH_SHORT).show();
    }

    public static void snack(String s) {
        if(mainView!=null){
            Snackbar.make(mainView, s, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void safe_init(){
        Log.d("SAFE","ASDAD");
//        if(MyNotificationRecyclerViewAdapter.myNotificationRecyclerViewAdapter!=null) {
//            if(Notification.ITEMS.size()<1){
//                Notification.updateData();
//            }
//        }
//        if(MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter!=null){
//            if (Contact.ITEMS.size() < 1) {
//                Contact.updateData();
//            }
//        }
    }

    @Override
    public void onListFragmentInteraction(Category.CategoryItem item) {
        final TabLayout host = (TabLayout) findViewById(R.id.tabs);
        Log.d(TAG,String.valueOf(item.id));
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.show();
        MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(item.id);
        host.getTabAt(1).select();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContactsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ContactsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactsFragment newInstance(int sectionNumber) {
            ContactsFragment fragment = new ContactsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button b1 = (Button) rootView.findViewById(R.id.button);
            final TabLayout host = (TabLayout) getActivity().findViewById(R.id.tabs);
            final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            host.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
//                    safe_init();
                    switch (tab.getPosition()){
                        case 0:
                            MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(0);
                            fab.hide();
                            break;
                        case 1:
                            if(MyContactRecyclerViewAdapter.filterCategory!=0)
                                fab.show();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
//                    safe_init();
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
//                    safe_init();
                    switch (tab.getPosition()){
                        case 0:
                            MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(0);
                            fab.hide();
                            break;
                    }
                }
            });

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(1);
                    host.getTabAt(1).select();
                }
            });

            Button b2 = (Button) rootView.findViewById(R.id.button2);

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(2);
                    host.getTabAt(1).select();
                }
            });



//            if(MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter!=null) {
//                Contact.updateData();
////                MyContactRecyclerViewAdapter.myContactRecyclerViewAdapter.filter(0);
//            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ContactsFragment (defined as a static inner class below).
            if(position==1){
                Log.d("TESTSETSET","ASDASD");
                return new ContactFragment().newInstance(category);
            }else if(position==2){
                return new NotificationFragment().newInstance(1);
            }
            return new CategoryFragment().newInstance(1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}

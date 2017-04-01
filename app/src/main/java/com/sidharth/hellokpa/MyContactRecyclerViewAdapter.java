package com.sidharth.hellokpa;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidharth.hellokpa.ContactFragment.OnListFragmentInteractionListener;
import com.sidharth.hellokpa.dummy.Contact;
import com.sidharth.hellokpa.dummy.Contact.ContactItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactItem> mValues;
    private final List<ContactItem> mValuesCopy;
    private final OnListFragmentInteractionListener mListener;
    public static MyContactRecyclerViewAdapter myContactRecyclerViewAdapter = null;
    public static int filterCategory = 0;
    public MyContactRecyclerViewAdapter(List<ContactItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mValuesCopy = new ArrayList<>(items);
        mListener = listener;
        myContactRecyclerViewAdapter = this;
        Contact.updateData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mDesignationView.setText(mValues.get(position).designation+" - "+mValues.get(position).wing);
        holder.mPhoneView.setText(mValues.get(position).phone);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void filter(int cat) {
        filterCategory = cat;
        mValues.clear();
        if(cat==0){
            mValues.addAll(mValuesCopy);
        } else{
            for (ContactItem c: mValuesCopy) {
                Log.d("SAD",String.valueOf(c.category)+String.valueOf(cat));
                if (c.category == cat) {
                    mValues.add(c);
                }
            }
        }
        Log.d("SAD",String.valueOf(mValues.size()));
        notifyDataSetChanged();
    }

    public void filter(String x) {
        x=x.toLowerCase().replaceAll(" ","");
        mValues.clear();
        if(x.length()<1){
            mValues.addAll(mValuesCopy);
        } else{
            for (ContactItem c: mValuesCopy) {
//                Log.d("SAD",String.valueOf(c.category)+String.valueOf(cat));
                if (c.name.toLowerCase().contains(x)) {
                    mValues.add(c);
                }
            }
        }
//        Log.d("SAD",String.valueOf(mValues.size()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateData(List<ContactItem> items) {
        Collections.sort(items);
        mValues.clear();
        mValues.addAll(items);
        mValuesCopy.clear();
        mValuesCopy.addAll(items);
        Contact.ITEMS.clear();
        Contact.ITEMS.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDesignationView;
        public final TextView mPhoneView;
        public ContactItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mDesignationView = (TextView) view.findViewById(R.id.designation);
            mPhoneView = (TextView) view.findViewById(R.id.phone);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}

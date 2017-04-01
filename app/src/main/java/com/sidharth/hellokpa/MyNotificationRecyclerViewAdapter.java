package com.sidharth.hellokpa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sidharth.hellokpa.NotificationFragment.OnListFragmentInteractionListener;
import com.sidharth.hellokpa.dummy.Notification.NotificationItem;
import com.sidharth.hellokpa.dummy.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NotificationItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNotificationRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationRecyclerViewAdapter.ViewHolder> {

    private final List<Notification.NotificationItem> mValues;
    private final List<Notification.NotificationItem> mValuesCopy;
    private final OnListFragmentInteractionListener mListener;
    public static MyNotificationRecyclerViewAdapter myNotificationRecyclerViewAdapter;

    public MyNotificationRecyclerViewAdapter(List<NotificationItem> items, OnListFragmentInteractionListener listener) {
        Collections.sort(items);
        mValues = items;
        mValuesCopy = new ArrayList<>(items);
        mListener = listener;
        myNotificationRecyclerViewAdapter = this;
        Notification.updateData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        holder.mTextView.setText(mValues.get(position).text);
//        holder.mPhoneView.setText(mValues.get(position).phone);

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateData(List<Notification.NotificationItem> items) {
        Collections.sort(items);
        mValues.clear();
        mValues.addAll(items);
        mValuesCopy.clear();
        mValuesCopy.addAll(items);
        Notification.ITEMS.clear();
        Notification.ITEMS.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTextView;
        public NotificationItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.notif_title);
            mTextView = (TextView) view.findViewById(R.id.notif_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}

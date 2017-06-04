package com.dcmmoguls.offthejailadmin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dcmmoguls.offthejailadmin.FilterUsersListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyFilterUserListRecyclerViewAdapter extends RecyclerView.Adapter<MyFilterUserListRecyclerViewAdapter.ViewHolder> {

    private final List<UserItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private FilterUsersListFragment mContext;

    public MyFilterUserListRecyclerViewAdapter(List<UserItem> items, OnListFragmentInteractionListener listener, FilterUsersListFragment context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filterusers_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserItem model = mValues.get(position);
        SharedPreferences sharedPref = mContext.getActivity().getSharedPreferences("com.dcmmoguls.offthejailadmin", Context.MODE_PRIVATE);

        if(!model.city.contains(sharedPref.getString("query", ""))) {
            return;
        }
        holder.mItem = model;
        holder.mIdView.setText(model.key);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mAddressView.setText(mValues.get(position).city);
        holder.mPhoneView.setText(mValues.get(position).phone);
        holder.mEmailView.setText(mValues.get(position).email);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mPhoneView;
        public final TextView mAddressView;
        public final TextView mEmailView;
        public UserItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mNameView = (TextView) view.findViewById(R.id.tvName);
            mPhoneView = (TextView) view.findViewById(R.id.tvPhone);
            mAddressView= (TextView) view.findViewById(R.id.tvAddress);
            mEmailView = (TextView) view.findViewById(R.id.tvEmail);
        }
    }
}

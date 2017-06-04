package com.dcmmoguls.offthejailadmin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dcmmoguls.offthejailadmin.ReportsFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyReportsRecyclerViewAdapter extends RecyclerView.Adapter<MyReportsRecyclerViewAdapter.ViewHolder> {

    private final List<Report> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyReportsRecyclerViewAdapter(List<Report> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reports_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).senderId);
        holder.mNameView.setText(mValues.get(position).senderName);
        holder.mAddressView.setText(mValues.get(position).senderAddress);
        holder.mPhoneView.setText(mValues.get(position).senderPhone);
        holder.mEmailView.setText(mValues.get(position).senderEmail);
        holder.mTimeView.setText(mValues.get(position).createdAt);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mPhoneView;
        public final TextView mAddressView;
        public final TextView mEmailView;
        public final TextView mTimeView;
        public Report mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mNameView = (TextView) view.findViewById(R.id.tvName);
            mPhoneView = (TextView) view.findViewById(R.id.tvPhone);
            mAddressView= (TextView) view.findViewById(R.id.tvAddress);
            mEmailView = (TextView) view.findViewById(R.id.tvEmail);
            mTimeView = (TextView) view.findViewById(R.id.tvTime);
        }

    }
}

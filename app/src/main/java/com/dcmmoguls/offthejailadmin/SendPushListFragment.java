package com.dcmmoguls.offthejailadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dcmmoguls.offthejailadmin.model.MyMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SendPushListFragment extends Fragment {

    public DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    private DatabaseReference channelRef = FirebaseDatabase.getInstance().getReference().child("channels");
    private DatabaseReference messageRef = channelRef.child("messages");


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private MySendPushListRecyclerViewAdapter adapter;
    private List<UserItem> channelItemList = new ArrayList<UserItem>();

    private List<String> receiversSingalIds = new ArrayList<String>();

    private SharedPreferences sharedPref;
    private String uid;
    private String uname;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SendPushListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SendPushListFragment newInstance(int columnCount) {
        SendPushListFragment fragment = new SendPushListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getSharedPreferences("com.dcmmoguls.offthejailadmin", Context.MODE_PRIVATE);
        uid = sharedPref.getString("userid", "");
        uname = sharedPref.getString("name", "Attorney");

        MainActivity activity = (MainActivity) getActivity();
        activity.selectedUsers.clear();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    private void sendPush(String text) {
        JSONObject payload = new JSONObject();
        try {
            if(receiversSingalIds.size() > 0) {
                JSONArray jsArray = new JSONArray(receiversSingalIds);
                payload.put("include_player_ids", jsArray);
            }
            JSONObject data = new JSONObject();
            data.put("name", sharedPref.getString("user_name", ""));
            data.put("uid", sharedPref.getString("userid", ""));
            data.put("type", "notification");
            JSONObject contents = new JSONObject();
            contents.put("en", text );
            payload.put("contents", contents);
            payload.put("content-available", 1);
            payload.put("data", data);
            payload.put("ios_badgeType", "Increase");
            payload.put("ios_badgeCount", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.postNotification(payload, new OneSignal.PostNotificationResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("Post Push", "success");
            }

            @Override
            public void onFailure(JSONObject response) {
                Log.d("Post Push", "fail");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sendpush, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Button btnSend = (Button) view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                if(activity.selectedUsers.size() == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("")
                            .setMessage("Choose at least one user to send push notification")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                    return;
                }

                EditText etPush = (EditText) view.findViewById(R.id.push_content);
                Date d = new Date();
                CharSequence s  = DateFormat.format("yyyy-MM-dd HH:mm:ss", d.getTime());

                String strMessage = etPush.getText().toString();

                for(UserItem item:activity.selectedUsers) {
                    receiversSingalIds.add(item.OneSignalId);

                    messageRef = channelRef.child(item.key).child("messages");
                    messageRef.push().setValue(new MyMessage(uid, uname, strMessage, null, s.toString()));
                }

                sendPush(strMessage);
                etPush.setText("");

                Toast.makeText(getActivity(), "Sent Push notification", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new MySendPushListRecyclerViewAdapter(channelItemList, mListener);
            recyclerView.setAdapter(adapter);

            userRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserItem obj = dataSnapshot.getValue(UserItem.class);
                    obj.key = dataSnapshot.getKey();
                    channelItemList.add(obj);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(UserItem item);
    }
}

package com.dcmmoguls.offthejailadmin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ReportsFragment.OnListFragmentInteractionListener,
        RoutesFragment.OnFragmentInteractionListener, ChatHistoryFragment.OnListFragmentInteractionListener,
        SendPushListFragment.OnListFragmentInteractionListener, FilterUsersListFragment.OnListFragmentInteractionListener {

    private boolean viewIsAtHome;
    protected List<UserItem> selectedUsers = new ArrayList<UserItem>();

    private static final String[] INITIAL_PERMS={
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE
    };

    public void onListFragmentInteraction(Report item) {
        Fragment fragment = RoutesFragment.newInstance(item.key, item.senderId);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment).addToBackStack(null);
            ft.commit();
        }
        viewIsAtHome = false;
    }

    public void onListFragmentInteraction(ChannelItem item) {
        Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
        intent.putExtra("channel", item.key);
        intent.putExtra("chatting", true);
        startActivity(intent);
        viewIsAtHome = false;
    }

    public void onListFragmentInteraction(UserItem item) {
        if(item.isSelected()) {
            selectedUsers.add(item);
        } else {
            selectedUsers.remove(item);
        }
    }

    public void onListFragmentInteraction(UserItem item, int i) {
    }
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.actionbar_layout,
                null);
        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bar));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_reportHistory);
        displayView(R.id.nav_reportHistory);

        if (!canAccessLocation() || !canAccessContacts()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
        }
    }

    private boolean canAccessLocation() {
        return(hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean canAccessContacts() {
        return(hasPermission(android.Manifest.permission.CALL_PHONE));
    }

    private boolean hasPermission(String perm) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (!viewIsAtHome) { //if the current view is not the News fragment
            displayView(R.id.nav_reportHistory); //display the News fragment
        } else {
            moveTaskToBack(true);  //If view is in News fragment, exit application
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayView(int id) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        viewIsAtHome = false;
        if (id == R.id.nav_reportHistory) {
            fragment = ReportsFragment.newInstance(1);
            title = "Reports History";
            viewIsAtHome = true;
        } else if (id == R.id.nav_chatHistory) {
            fragment = ChatHistoryFragment.newInstance(1);
            title = "Chat History";
            viewIsAtHome = false;

        } else if (id == R.id.nav_sendPush) {
            fragment = SendPushListFragment.newInstance(1);
            title = "Send Push";
            viewIsAtHome = false;
        } else if (id == R.id.nav_filterPeople) {
            fragment = FilterUsersListFragment.newInstance(1);
            title = "Filter users by city";
            viewIsAtHome = false;
        } else if (id == R.id.nav_resetPwd) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            SharedPreferences sharedPref = getSharedPreferences("com.dcmmoguls.offthejailadmin", Context.MODE_PRIVATE);
            String emailAddress = sharedPref.getString("email", "");

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Success")
                                        .setMessage("A password reset link has been sent to your email address!")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        })

                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else if (id == R.id.nav_signOut) {
            FirebaseAuth.getInstance().signOut();

            SharedPreferences sharedPref = getSharedPreferences("com.dcmmoguls.offthejailadmin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("userid");
            editor.remove("email");
            editor.commit();

            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}

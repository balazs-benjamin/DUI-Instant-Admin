package com.dcmmoguls.offthejailadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RegionIterator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static String TAG = "User Signin";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private EditText etEmail, etPassword;
    private Button btnSubmit, btnRegister;

    private SharedPreferences sharedPref;

    private AVLoadingIndicatorView avLoadingIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("com.dcmmoguls.offthejailadmin", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avLoadingIndicatorView.hide();

        Typeface opensansRegular = Typeface.createFromAsset(getAssets(), "fonts/opensans_regular.ttf");
        Typeface opensansSemiBold = Typeface.createFromAsset(getAssets(), "fonts/opensans_semibold.ttf");

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        etEmail.setTypeface(opensansRegular);

        btnSubmit.setTypeface(opensansSemiBold);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForms())
                    register();
            }
        });

        btnRegister.setTypeface(opensansSemiBold);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });

        if(sharedPref.contains("userid")) {
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private boolean validateForms() {
        if( etEmail.getText().toString().length() == 0 )
            etEmail.setError( "Email is required!" );
        else if (!etEmail.getText().toString().matches(emailPattern))
            etEmail.setError( "Invalid email address!" );
        else if( etPassword.getText().toString().length() == 0 )
            etPassword.setError( "Password is required!" );
        else if(etPassword.getText().toString().length() < 5)
            etPassword.setError( "Password must be longer than 5" );
        else
            return true;
        return false;
    }

    private void register() {
        avLoadingIndicatorView.show();
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        avLoadingIndicatorView.hide();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signIn", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if(task.getResult().getUser() != null) {

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("userid", task.getResult().getUser().getUid());
                            editor.putString("email", etEmail.getText().toString());
                            editor.commit();


                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    Log.d("debug", "User:" + userId);
                                    if (registrationId != null)
                                        Log.d("debug", "registrationId:" + registrationId);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("OneSignalId", userId);
                                    editor.commit();

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin/" + uid + "/OneSignalId");
                                    ref.setValue(userId);
                                }
                            });

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }
}

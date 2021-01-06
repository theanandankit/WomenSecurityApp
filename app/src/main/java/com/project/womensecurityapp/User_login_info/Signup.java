package com.project.womensecurityapp.User_login_info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.maps.errors.ApiException;
import com.project.womensecurityapp.R;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import me.ibrahimsn.lib.CirclesLoadingView;

public class Signup extends AppCompatActivity {

    FirebaseAuth auth;
    CircularProgressButton signup;
    TextInputLayout id, password;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CirclesLoadingView loading;
    SignInButton google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loading=findViewById(R.id.loading);

        auth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        id = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
         google=findViewById(R.id.google_auth);

        google.setOnClickListener(v -> signIn());

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signup.setOnClickListener(v -> {
            startanim();
            google.setEnabled(false);
            auth.createUserWithEmailAndPassword(id.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        signup.setText("Logging in");
                        Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(getApplicationContext(),Account_setup.class);
                        i.putExtra("edit",0);
                        startActivity(i);

                    } else {
                        signup.stopAnimation();
                        signup.revertAnimation();
                        Toast.makeText(getApplicationContext(),"Failed Signing Up",Toast.LENGTH_SHORT).show();
                        google.setEnabled(true);
                    }
                }
            });
        });
    }
    void startanim()
    {
        signup.startAnimation();
        // btn.setBackgroundColor(getResources().getColor(R.color.white));
        signup.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonbganim));
        signup.setSpinningBarColor(R.color.white);
        signup.setSpinningBarWidth(12.0f);
        Toast.makeText(getApplicationContext(),"SEE", Toast.LENGTH_LONG).show();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Google Sign In failed",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("123", "firebaseAuthWithGoogle:" + acct.getId());
        signup.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("123", "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                            Intent i=new Intent(getApplicationContext(),Account_setup.class);
                            i.putExtra("edit",0);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("123", "signInWithCredential:failure", task.getException());
                            loading.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Google Sign In failed",Toast.LENGTH_LONG).show();
                            signup.setEnabled(true);
                        }
                    }
                });
    }
}

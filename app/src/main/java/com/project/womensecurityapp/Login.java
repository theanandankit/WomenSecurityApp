package com.project.womensecurityapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import me.ibrahimsn.lib.CirclesLoadingView;

public class Login extends AppCompatActivity {

    FirebaseAuth auth;
    Button login;
    TextInputLayout id, password;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CircularProgressButton btn;
    LinearLayout linear;
    CirclesLoadingView loading;
    SignInButton google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
//        login = findViewById(R.id.log_in);
        id = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        google = findViewById(R.id.google_auth);
        btn = findViewById(R.id.test);
        linear = findViewById(R.id.linear);
        loading = findViewById(R.id.loading);

        google.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            signIn();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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
                Toast.makeText(getApplicationContext(), "Google Sign In failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("123", "firebaseAuthWithGoogle:" + acct.getId());
        loading.setVisibility(View.VISIBLE);
        btn.setEnabled(false);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("123", "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(), Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Login.this, Main2Activity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("123", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Google Sign In failed", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.INVISIBLE);
                            btn.setEnabled(true);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.Alert_red));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startanim();
                google.setEnabled(false);

                auth.signInWithEmailAndPassword(id.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            btn.setText("Logging in");
                            Toast.makeText(getApplicationContext(), "Logging you in", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Login.this, Main2Activity.class);
                            startActivity(i);

                        } else {
                            Toast.makeText(getApplicationContext(), "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                            btn.stopAnimation();
                            btn.revertAnimation();
                            google.setEnabled(true);

                           /* btn.revertAnimation(new OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.quantum_pink));
                                }
                            });*/
                           /* Snackbar snackbar=Snackbar.make(linear,"Try Again!",Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    id.getEditText().setText("");
                                    password.getEditText().setText("");


                                }
                            });
                            snackbar.setActionTextColor(Color.RED);
                            View sbView = snackbar.getView();*/


                        }
                    }
                });
            }
        });

    }

    void startanim() {
        btn.startAnimation();
        // btn.setBackgroundColor(getResources().getColor(R.color.white));
        btn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonbganim));
        btn.setSpinningBarColor(R.color.white);
        btn.setSpinningBarWidth(12.0f);
        Toast.makeText(getApplicationContext(), "SEE", Toast.LENGTH_LONG).show();

    }
}


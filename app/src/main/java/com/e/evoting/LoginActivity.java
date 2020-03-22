package com.e.evoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends AppCompatActivity {




    private FirebaseAuth mAuth;
    TextView  userPassword, userEmail;
    static FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        userEmail = (TextView) findViewById(R.id.editTextEmail);
        userPassword = (TextView) findViewById(R.id.editTextPassword);



    }

    public void viewRegisterClicked(View view) {
       // finish();
        startActivity(new Intent(this,RegisterActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
         currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            //registerNewUser();

        }else {
            updateUI(currentUser);
        }
    }



    private void updateUI(FirebaseUser currentUser) {

        Toast.makeText(this,"Welcome"+currentUser.getEmail(),Toast.LENGTH_LONG).show();

    }

    public void loginExistingUser(View view) {

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if(email.isEmpty()) {
            warn("Email");
        }else if(password.isEmpty()) {
            warn("Password");
        }else{

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                               // finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));





                            } else {
                                // If sign in fails, display a message to the user.
                                // Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login failed."+e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });



        }


    }


    private void warn(String value) {

        Toast.makeText(getApplicationContext(),value + " cannot be empty",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mAuth.signOut();

    }

    public void viewForgotPassword(View view) {

        Toast.makeText(getApplicationContext(),"Will be implemented shortly",Toast.LENGTH_SHORT).show();

    }
}

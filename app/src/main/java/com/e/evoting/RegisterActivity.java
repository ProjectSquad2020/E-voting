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

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    TextView userName, userPhone, userPassword, userPasswordConfirmation, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhoneNo);
        userPassword = (TextView) findViewById(R.id.userPassword);
        userPasswordConfirmation = (TextView) findViewById(R.id.userPasswordConfirmation);
        userEmail = (TextView) findViewById(R.id.userEmail);

    }

    public void existingUser(View view) {
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }



    private void registerNewUser() {

        String name = userName.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String passwordConfirm = userPasswordConfirmation.getText().toString().trim();

        if(name.isEmpty()) {
            warn("name");
        }else if(phone.isEmpty()) {
            warn("phone");
        }else if(email.isEmpty()) {
            warn("email");
        }else if(password.isEmpty()) {
            warn("password");
        }else if(passwordConfirm.isEmpty()) {
            warn("passwordConfirm");
        }else if(!(password.equals(passwordConfirm))){
            userPassword.setText("");
            userPasswordConfirmation.setText("");

            Toast.makeText(getApplicationContext(), R.string.password_not_matched,Toast.LENGTH_SHORT).show();

        }else{

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(RegisterActivity.this, "createUserWithEmail:success",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                finish();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                            } else {
                                // If sign in fails, display a message to the user.
                                // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Authentication failed."+e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        }






    }

    private void warn(String value) {

        Toast.makeText(getApplicationContext(),value + " cannot be empty",Toast.LENGTH_SHORT).show();


    }

    private void updateUI(FirebaseUser currentUser) {

        Toast.makeText(this,"Welcome "+currentUser,Toast.LENGTH_LONG).show();



    }

    public void registerNewUser(View view) {
        registerNewUser();
    }
}

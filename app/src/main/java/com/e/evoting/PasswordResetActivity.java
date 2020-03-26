package com.e.evoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {


    EditText editTextEmail, editTextEmailConfirm;
    FirebaseAuth firebaseAuth;
    TextView textViewErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editTextEmail = (EditText)findViewById(R.id.editTextEmailReset);
        editTextEmailConfirm = (EditText)findViewById(R.id.editTextEmailConfirmReset);
        textViewErrorMessage = (TextView)findViewById(R.id.textViewError);
        textViewErrorMessage.setText("");


        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void sendPasswordResetLink(View view) {

        String email = editTextEmail.getText().toString().trim();
        String emailConfirm = editTextEmailConfirm.getText().toString().trim();

        if(email.isEmpty()){
            textViewErrorMessage.setText("Email cannot be empty!");

        }else if(emailConfirm.isEmpty()){
            textViewErrorMessage.setText("Confirm Email cannot be empty!");

        }else if(!(email.equals(emailConfirm))){
            textViewErrorMessage.setText("Both the emails must be same!");

        }else{

            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Password reset link sent successfully!",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();


                }
            });
        }

    }
}

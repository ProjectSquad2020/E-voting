package com.e.evoting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.e.evoting.ui.gallery.GalleryFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private View root;
    private FirebaseUser user;
    private String selectedPartyName;

    private String prevHash;
    private Block newBlock;
    DatabaseReference previousHashRef;

    public FingerprintHandler(Context context) {

        this.context = context;

    }

    public FingerprintHandler(Context context, View root) {
        this.context = context;
        this.root = root;
    }

    public FingerprintHandler(Context context, View root, FirebaseUser user, String selectedPartyName) {
        this.context = context;
        this.root = root;
        this.user = user;
        this.selectedPartyName = selectedPartyName;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an Auth Error. " + errString, false);

    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Auth Failed. ", false);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("You can now access the app.", true);

        //Intent i = new Intent(context,Main2Activity.class);
        //context.startActivity(i);

    }

    private void update(String s, boolean b) {

        TextView paraLabel = (TextView) root.findViewById(R.id.fingerprintScanStatusLabel);
        ImageView imageView = (ImageView) ((Activity) context).findViewById(R.id.fingerprintImage);

        paraLabel.setText(s);

        if (b == false) {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.ic_action_done);
            GalleryFragment.success = true;
            GalleryFragment.printSuccessMessage(this.context);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference mRef = database.getReference("blocks");
            final DatabaseReference mRefUser = database.getReference("users");


            previousHashRef = database.getReference("previousHash");

            previousHashRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    prevHash = (String) dataSnapshot.getValue();
                    Log.w("Previous Hash", prevHash + "");


                    Vote vote = new Vote(user.getUid(), selectedPartyName);
                    newBlock = new Block(vote, prevHash);
                    Log.w("Last Block hash", newBlock.getPreviousHash() + "");


                    mRef.child(newBlock.getTimeStamp()).setValue(newBlock)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "You voted successfully", Toast.LENGTH_LONG).show();

                                    mRefUser.child(user.getUid()).child("voted").setValue(Boolean.valueOf("true")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "User data updated successfully", Toast.LENGTH_LONG).show();


                                            DatabaseReference newPrevHashRef = database.getReference("previousHash");

                                            newPrevHashRef.setValue(newBlock.getHash()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Log.w("Previous Hash status", "success");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Previous Hash status", e.getLocalizedMessage());

                                                }
                                            });


                                            Intent intent = ((Activity) context).getIntent();
                                            ((Activity) context).finish();
                                            //context.startActivity(new Intent(context,DummyActivity.class));
                                            context.startActivity(intent);


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();


                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }//else case

    }//update method


}//fragment

package com.e.evoting.ui.gallery;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.e.evoting.FingerprintHandler;
import com.e.evoting.MainActivity;
import com.e.evoting.Person;
import com.e.evoting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.ContentValues.TAG;
import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

public class GalleryFragment extends Fragment {

    public static boolean success;
    private GalleryViewModel galleryViewModel;
    Button buttonVote;
    TextView textViewfingerprintCaption, textViewfingerprintScanStatus;
    EditText editTextName, editTextAadharNo, editTextAddress;
    Spinner dynamicSpinner;
    ImageView imageViewFinger;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    String aadharNo;
    String aadharNumberEntered;
    Boolean voted;
    String selectedPartyName;


    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

    View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        buttonVote = root.findViewById(R.id.buttonVote);

        editTextName = root.findViewById(R.id.editTextName);
        editTextAadharNo = root.findViewById(R.id.editTextAadhar);
        editTextAddress = root.findViewById(R.id.editTextAddress);
        imageViewFinger = root.findViewById(R.id.fingerprintImage);

        textViewfingerprintCaption = root.findViewById(R.id.fingerprintHeadingLabel);
        textViewfingerprintScanStatus = root.findViewById(R.id.fingerprintScanStatusLabel);

        dynamicSpinner = (Spinner) root.findViewById(R.id.spinnerParty);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        success = false;


        fetchFromDatabase();


        String[] items = new String[]{"Select one", "Party 1", "Party 2", "Party 3", "Party 4", "Party 5"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedPartyName = parent.getItemAtPosition(position).toString();
                Log.v("item", selectedPartyName);
                Toast.makeText(getContext(), selectedPartyName, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
        });


        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vote button clicked
                aadharNumberEntered = editTextAadharNo.getText().toString().trim();

                if (aadharNumberEntered.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter your Aadhar No", Toast.LENGTH_SHORT).show();
                } else if (selectedPartyName.equalsIgnoreCase("Select one")) {
                    Toast.makeText(getContext(), "Please select one party to vote", Toast.LENGTH_SHORT).show();
                } else if (!(aadharNumberEntered.equalsIgnoreCase(aadharNo))) {
                    Toast.makeText(getContext(), "Aadhar Number entered is incorrect!!", Toast.LENGTH_SHORT).show();
                } else {

                    textViewfingerprintCaption.setText("Fingerprint Authentication is required!!");
                    getFingerprintScanStatus();

                    if (success) {

                    } else {
                        // Toast.makeText(getContext(),"Not success",Toast.LENGTH_SHORT).show();

                    }

                }


            }
        });


        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });
        return root;
    }

    public static void printSuccessMessage(Context context) {
        Toast.makeText(context, "Success in the method", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("votes");


    }


    private void getFingerprintScanStatus() {

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has Fingerprint Scanner
        // Check 3: Have permission to use fingerprint scanner in the app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 Fingerprint is registered

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {

                textViewfingerprintScanStatus.setText("Fingerprint Scanner not detected in Device");

            } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

                textViewfingerprintScanStatus.setText("Permission not granted to use Fingerprint Scanner");

            } else if (!keyguardManager.isKeyguardSecure()) {

                textViewfingerprintScanStatus.setText("Add Lock to your Phone in Settings");

            } else if (!fingerprintManager.hasEnrolledFingerprints()) {

                textViewfingerprintScanStatus.setText("You should add atleast 1 Fingerprint to use this Feature");

            } else {

                textViewfingerprintScanStatus.setText("Place your Finger on Scanner to Access the App.");

                generateKey();

                if (cipherInit()) {

                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(getContext(), root, MainActivity.user, selectedPartyName);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);

                }
            }

        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {

        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

    private void fetchFromDatabase() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(MainActivity.user.getUid());

        DatabaseReference countedReference = database.getReference("counted");

        countedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             Boolean counted = (Boolean) dataSnapshot.getValue(Boolean.class);

             if(counted){

                 buttonVote.setEnabled(false);
                 buttonVote.setText("Voting Timed out!!");
                 dynamicSpinner.setEnabled(false);

             }else{
                 // Read from the database
                 myRef.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         // This method is called once with the initial value and again
                         // whenever data at this location is updated.
                         Person p = (Person) dataSnapshot.getValue(Person.class);

                         editTextName.setText(p.getName());

                         aadharNo = p.getAadhar();
                         voted = p.getVoted();
                         //Toast.makeText(getActivity(),"voted="+voted,Toast.LENGTH_LONG).show();
                         if (voted) {
                             //Toast.makeText(getActivity(),"You have already voted!!",Toast.LENGTH_SHORT).show();
                             Log.w(TAG, "Voted in if: " + voted);
                             editTextAadharNo.setText(aadharNo);
                             editTextAadharNo.setEnabled(false);
                             buttonVote.setEnabled(false);
                             buttonVote.setText("Already Voted!!!");
                             dynamicSpinner.setEnabled(false);
                         }


                         editTextAddress.setText(p.getAddress());


                         Log.w(TAG, "Voted: " + voted);
                     }

                     @Override
                     public void onCancelled(DatabaseError error) {
                         // Failed to read value
                         //Log.w(TAG, "Failed to read value.", error.toException());
                         Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 });

             }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}

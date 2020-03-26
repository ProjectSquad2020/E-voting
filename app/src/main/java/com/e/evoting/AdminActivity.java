package com.e.evoting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceBlocks,databaseReferenceCounted;
    private FirebaseAuth mAuth;
    ArrayList<Block> blocks;
    ArrayList<Vote> votes;
    private Button buttonCountVotes;

    ArrayList<String> partyNames;

    EditText editTextTotalVotes,editTextWinner;

    ArrayList<Result> countedResults;
    Boolean counted;
    Boolean upload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editTextTotalVotes = (EditText)findViewById(R.id.editTextTotalVotes);
        editTextWinner = (EditText)findViewById(R.id.editTextWinner);
        buttonCountVotes = (Button)findViewById(R.id.buttonCountVotes);

        buttonCountVotes.setEnabled(false);
        buttonCountVotes.setText("Loading the data....");


        blocks = new ArrayList<>();
        votes = new ArrayList<>();
        partyNames = new ArrayList<>();
        countedResults = new ArrayList<>();

        upload = true;

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceBlocks = firebaseDatabase.getReference("blocks");
        databaseReferenceCounted = firebaseDatabase.getReference("counted");

        getData();
    }

    private void getData() {


        databaseReferenceCounted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                counted = (Boolean)dataSnapshot.getValue(Boolean.class);
               // Toast.makeText(getApplicationContext(),"Counted value "+counted,Toast.LENGTH_LONG).show();

               if(!counted) {

                    databaseReferenceBlocks.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                Block block = dataSnapshot.getValue(Block.class);

                                //blocks.add(block);

                                //Toast.makeText(getApplicationContext(),"Block value :"+block.getTimeStamp(),Toast.LENGTH_LONG).show();
                                //Log.w("Block value :",block.getTimeStamp());

                                Vote vote = (Vote) block.getVote();
                                //votes.add(vote);

                                partyNames.add(vote.getPartyName());

                                //Toast.makeText(getApplicationContext(),"Vote value :"+vote.getPartyName(),Toast.LENGTH_LONG).show();
                                //Log.w("Vote value :",vote.getPartyName());

                            }

                            //Enabling count button
                            buttonCountVotes.setEnabled(true);
                            buttonCountVotes.setText("Count the Votes");


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

               }else {
                   //results already counted
                   upload = false;
                   Toast.makeText(getApplicationContext(),"Votes already counted!!",Toast.LENGTH_LONG).show();

                   DatabaseReference countedReference = firebaseDatabase.getReference("results");

                   countedReference.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {

                           for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                               Result r = (Result)dataSnapshot.getValue(Result.class);

                               countedResults.add(r);

                               buttonCountVotes.setEnabled(true);
                               buttonCountVotes.setText("See the results");


                           }

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


               }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void CountTheVotes(View view) {

        ArrayList<Result> results;

        if(!counted){
            editTextTotalVotes.setText("Total no. of Votes :"+partyNames.size());
            results = countFreq(getStringArray(partyNames));


        }else{
            int total=0;
            for(Result rr : countedResults){
                total+=rr.numberOfVotes;
            }
            editTextTotalVotes.setText("Total no. of Votes :"+total);
            results = countedResults;

        }



        String winner = "";

        int maxVotes = results.get(0).getNumberOfVotes();
        for(Result r: results){

            if(r.getNumberOfVotes() == maxVotes){
                winner+=r.getPartyName()+'\n';

            }


        }
        editTextWinner.setText("Winner :"+winner);




        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ResultViewAdapter adapter = new ResultViewAdapter(results);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        if(!counted && upload) {
            Toast.makeText(getApplicationContext(),"Counted value : "+counted+"\nUpload value : "+upload,Toast.LENGTH_LONG).show();
            savedata(results);
        }

        buttonCountVotes.setEnabled(false);
        buttonCountVotes.setText("Results declared");




    }

    private void savedata(ArrayList<Result> results) {

        DatabaseReference resultsReference = firebaseDatabase.getReference("results");

        for(Result r : results){

            resultsReference.push().setValue(r).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.w("Results",r.partyName+"uploaded");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Results",r.partyName+"upload failed");

                }
            });

        }

        DatabaseReference countedReference = firebaseDatabase.getReference("counted");

        countedReference.setValue(Boolean.TRUE).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w("Results counted","set to true");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Results counted","upload failed");
            }
        });




    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        welcome(currentUser);
    }

    private void welcome(FirebaseUser currentUser) {
        if(currentUser!=null){
            Toast.makeText(getApplicationContext(),"welcome : "+currentUser.getEmail(),Toast.LENGTH_LONG).show();
            //getData();
        }else{
            Toast.makeText(getApplicationContext(),"User not logged in",Toast.LENGTH_LONG).show();


        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static ArrayList<Result> countFreq(String[] arr)
    {
        Map<String, Integer> mp = new HashMap<>();

        // Traverse through array elements and
        // count frequencies
        for (int i = 0; i < arr.length; i++)
        {
            if (mp.containsKey(arr[i]))
            {
                mp.put(arr[i], mp.get(arr[i]) + 1);
            }
            else
            {
                mp.put(arr[i], 1);
            }
        }

        return  sortByValueJava8Stream(mp);


    }

    // Function to convert ArrayList<String> to String[]
    public static String[] getStringArray(ArrayList<String> arr)
    {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static ArrayList<Result> sortByValueJava8Stream(Map<String, Integer> unSortedMap)
    {

        System.out.println("Unsorted Map : " + unSortedMap);

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        System.out.println("Sorted Map   : " + sortedMap);

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        System.out.println("Reverse Sorted Map   : " + reverseSortedMap);

        ArrayList<Result> results = new ArrayList<>();

        // Traverse through map and print frequencies
        for (Map.Entry<String, Integer> entry : reverseSortedMap.entrySet())
        {
            System.out.println(entry.getKey() + " " + entry.getValue());

            results.add(new Result(entry.getKey(),entry.getValue()));

        }

        return results;


    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

package com.e.evoting.ui.slideshow;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.evoting.Block;
import com.e.evoting.R;
import com.e.evoting.Result;
import com.e.evoting.ResultViewAdapter;
import com.e.evoting.Vote;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    private Button buttonCountVotes;
    private EditText editTextTotalVotes, editTextWinner, editTextPartyNameFrag, editTextCountFrag;
    private ArrayList<Result> countedResults;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceCounted;
    private ResultViewAdapter adapter;
    private Boolean counted;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        editTextTotalVotes = (EditText) root.findViewById(R.id.editTextTotalVotesFrag);
        editTextWinner = (EditText) root.findViewById(R.id.editTextWinnerFrag);

        editTextPartyNameFrag = (EditText) root.findViewById(R.id.editTextPartyNameFrag);
        editTextCountFrag = (EditText) root.findViewById(R.id.editTextCountFrag);

        buttonCountVotes = (Button) root.findViewById(R.id.buttonShowVotesFrag);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerViewFrag);
        relativeLayout = (RelativeLayout) root.findViewById(R.id.relativeLayoutFrag);


        buttonCountVotes.setEnabled(false);
        buttonCountVotes.setText("Loading the data....");

        countedResults = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceCounted = firebaseDatabase.getReference("counted");

        getData();

        buttonCountVotes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                CountTheVotes();
            }
        });


        return root;
    }

    private void getData() {

        databaseReferenceCounted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                counted = (Boolean) dataSnapshot.getValue(Boolean.class);
                // Toast.makeText(getApplicationContext(),"Counted value "+counted,Toast.LENGTH_LONG).show();

                if (counted) {
                    //results already counted

                    editTextCountFrag.setText("Party Name");

                    editTextPartyNameFrag.setText("Count");
                    relativeLayout.setBackgroundColor(Color.parseColor("#8F8F86"));


                    //Toast.makeText(getContext(), "Votes already counted!!", Toast.LENGTH_LONG).show();

                    DatabaseReference countedReference = firebaseDatabase.getReference("results");

                    countedReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                Result r = (Result) dataSnapshot.getValue(Result.class);

                                countedResults.add(r);

                                buttonCountVotes.setEnabled(true);
                                buttonCountVotes.setText("See the results");


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {

                    buttonCountVotes.setText("Results will be out soon!!");
                    buttonCountVotes.setEnabled(false);

                    editTextCountFrag.setText("");
                    editTextCountFrag.setHint("");
                    editTextPartyNameFrag.setText("");
                    editTextPartyNameFrag.setHint("");

                    editTextTotalVotes.setText("");
                    editTextTotalVotes.setHint("");

                    editTextWinner.setText("");
                    editTextWinner.setHint("");

                    relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void CountTheVotes() {

        ArrayList<Result> results;

        int total = 0;
        for (Result rr : countedResults) {
            total += rr.getNumberOfVotes();
        }
        editTextTotalVotes.setText("Total no. of Votes : " + total);
        results = countedResults;


        String winner = "";

        int maxVotes = results.get(0).getNumberOfVotes();
        for (Result r : results) {

            if (r.getNumberOfVotes() == maxVotes) {
                winner += r.getPartyName() + '\n';

            }


        }
        editTextWinner.setText("Winner :" + winner);


        adapter = new ResultViewAdapter(results);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonCountVotes.setEnabled(false);
        buttonCountVotes.setText("Results declared");


    }

}

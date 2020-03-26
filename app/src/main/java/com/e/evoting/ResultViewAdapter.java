package com.e.evoting;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultViewAdapter extends RecyclerView.Adapter<ResultViewAdapter.ViewHolder>{
    private ArrayList<Result> results;

    // RecyclerView recyclerView;
    public ResultViewAdapter(ArrayList<Result> results) {
        this.results = results;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result resultData = results.get(position);
        holder.editTextPartyName.setText(results.get(position).getPartyName());
        holder.editTextCount.setText(String.format("%d", results.get(position).getNumberOfVotes()));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+resultData.getPartyName(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EditText editTextPartyName;
        public EditText editTextCount;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.editTextPartyName = (EditText) itemView.findViewById(R.id.editTextPartyName);
            this.editTextCount = (EditText) itemView.findViewById(R.id.editTextCount);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}

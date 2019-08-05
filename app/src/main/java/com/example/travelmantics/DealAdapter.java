package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {
    ArrayList<TravelDeal> deals;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;

    public DealAdapter() {
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        deals = FirebaseUtil.deals;
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
//                Log.d("Deal", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TravelDeal deal = deals.get(i);
        viewHolder.bind(deal);

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitle, mDesc, mPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.dealtitle);
            mDesc = itemView.findViewById(R.id.dealdesc);
            mPrice = itemView.findViewById(R.id.dealprice);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal) {
            mTitle.setText(deal.getTitle());
            mDesc.setText(deal.getDescription());
            mPrice.setText(deal.getPrice());
        }

        @Override
        public void onClick(View v) {
            if (FirebaseUtil.isAdmin){
                int pos = getAdapterPosition();
                TravelDeal travelDeal = deals.get(pos);
                Intent intent = new Intent(v.getContext(),Insert.class);
                intent.putExtra("Deal",travelDeal);
                v.getContext().startActivity(intent);
            }else{
                Toast.makeText(v.getContext(), "can not click", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

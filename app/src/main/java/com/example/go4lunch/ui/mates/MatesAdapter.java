package com.example.go4lunch.ui.mates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.User;

import java.util.List;

public class MatesAdapter extends RecyclerView.Adapter<MatesViewHolder> {
    // FOR DATA
    private List<User> mResults;

    // CONSTRUCTOR
    public MatesAdapter(List<User> result) {
        this.mResults = result;
    }

    @Override
    public MatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_mates_item, parent,false);
        return new MatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MatesViewHolder viewHolder, int position) {
        viewHolder.updateWithData(this.mResults.get(position));
    }

    public User getMates(int position){
        return this.mResults.get(position);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mResults != null) itemCount = mResults.size();
        return itemCount;
    }
}

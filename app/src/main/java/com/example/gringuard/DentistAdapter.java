package com.example.gringuard; // Replace with your actual package name

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DentistAdapter extends RecyclerView.Adapter<DentistAdapter.ViewHolder> {

    private List<Dentist> dentistList;
    private List<Dentist> dentistListFull;

    public DentistAdapter(List<Dentist> dentistList) {
        this.dentistList = dentistList;
        // This copy is required for the filter to work
        this.dentistListFull = new ArrayList<>(dentistList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dentist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dentist currentDentist = dentistList.get(position);

        holder.tvName.setText(currentDentist.getName());
        holder.tvState.setText(currentDentist.getState());
        holder.tvPhone.setText(currentDentist.getPhone());

        // Pink Theme Call Action
        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + currentDentist.getPhone()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dentistList.size();
    }

    // This method must be INSIDE the DentistAdapter class brackets
    public void filter(String text) {
        dentistList.clear();
        if (text.isEmpty()) {
            dentistList.addAll(dentistListFull);
        } else {
            text = text.toLowerCase().trim();
            for (Dentist item : dentistListFull) {
                if (item.getName().toLowerCase().contains(text) ||
                        item.getState().toLowerCase().contains(text)) {
                    dentistList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvState, tvPhone;
        ImageView btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvState = itemView.findViewById(R.id.tvState);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}
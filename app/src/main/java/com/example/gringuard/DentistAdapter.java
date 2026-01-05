package com.example.gringuard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DentistAdapter extends RecyclerView.Adapter<DentistAdapter.ViewHolder> {

    private List<Dentist> dentistList;

    // Simplified constructor
    public DentistAdapter(List<Dentist> dentistList) {
        this.dentistList = dentistList;
    }

    // This method will be used to refresh the list safely
    public void updateList(List<Dentist> newList) {
        this.dentistList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dentist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dentist dentist = dentistList.get(position);
        holder.tvName.setText(dentist.name);
        holder.tvQual.setText(dentist.qualification);
        holder.tvReg.setText(dentist.regNo + " | " + dentist.state);
        holder.tvPhone.setText(dentist.phone);
        holder.tvEmail.setText(dentist.email);
    }

    @Override
    public int getItemCount() {
        return dentistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQual, tvReg, tvPhone, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQual = itemView.findViewById(R.id.tvQualification);
            tvReg = itemView.findViewById(R.id.tvRegNo);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}
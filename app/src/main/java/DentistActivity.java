package com.example.dentistapp; // ⚠️ package name same rakho jo tumhare project ka hai

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class DentistListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Dentist> dentistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_list);

        // connect RecyclerView
        recyclerView = findViewById(R.id.recyclerDentist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize list
        dentistList = new ArrayList<>();

        // sample dentist (add remaining states later)
        dentistList.add(new Dentist(
                "Dr. Amit Sharma",
                "BDS, MDS",
                "9876543210",
                "amit.sharma@gmail.com",
                "Uttar Pradesh",
                R.drawable.dentist
        ));

        // adapter
        DentistAdapter adapter = new DentistAdapter(dentistList);
        recyclerView.setAdapter(adapter);
    }
}
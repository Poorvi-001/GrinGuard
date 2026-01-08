package com.example.gringuard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DentistActivity extends AppCompatActivity {

    private DentistAdapter adapter;
    private List<Dentist> dentistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_list);

        initializeData();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter WITH the list
        adapter = new DentistAdapter(dentistList);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void initializeData() {
        dentistList = new ArrayList<>();
        dentistList.add(new Dentist("Dr. Aarav Sharma", "Maharashtra", "+91 98765 43210"));
        dentistList.add(new Dentist("Dr. Ishita Iyer", "Tamil Nadu", "+91 91234 56789"));
        dentistList.add(new Dentist("Dr. Kabir Singh", "Punjab", "+91 99887 76655"));
        dentistList.add(new Dentist("Dr. Meera Reddy", "Andhra Pradesh", "+91 88776 65544"));
        dentistList.add(new Dentist("Dr. Rohan Gupta", "Delhi", "+91 95554 43322"));
        dentistList.add(new Dentist("Dr. Ananya Das", "West Bengal", "+91 94443 32211"));
        dentistList.add(new Dentist("Dr. Vikram Verma", "Karnataka", "+91 96665 54433"));
        dentistList.add(new Dentist("Dr. Sana Khan", "Uttar Pradesh", "+91 97776 65544"));
    }
}